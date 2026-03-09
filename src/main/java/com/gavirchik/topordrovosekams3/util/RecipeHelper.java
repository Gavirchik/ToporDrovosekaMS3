package com.gavirchik.topordrovosekams3.util;

import com.gavirchik.topordrovosekams3.ToporDrovosekaMS3;
import com.gavirchik.topordrovosekams3.configuration.ModConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import java.util.HashMap;
import java.util.Map;

public class RecipeHelper {

    private static final Map<Integer, ItemStack> RECIPE = new HashMap<>();

    static {
        // Рецепт 3x3
        // 0 1 2
        // 3 4 5
        // 6 7 8

        RECIPE.put(0, ItemStack.EMPTY);
        RECIPE.put(1, new ItemStack(Items.NETHERITE_BLOCK));
        RECIPE.put(2, new ItemStack(Items.NETHERITE_BLOCK));

        RECIPE.put(3, createEnchantedBook(Enchantments.UNBREAKING, 3));
        // Слот 4: проверяется отдельно (золотой топор)
        RECIPE.put(5, new ItemStack(Items.NETHERITE_BLOCK));

        RECIPE.put(6, new ItemStack(Items.NETHER_STAR));
        RECIPE.put(7, createEnchantedBook(Enchantments.MENDING, 1));
        RECIPE.put(8, ItemStack.EMPTY);
    }

    public static ItemStack checkRecipe(Container container) {
        if (!ModConfig.COMMON.enableMod.get()) return ItemStack.EMPTY;
        if (container.getContainerSize() != 9) return ItemStack.EMPTY;

        // Проверяем все слоты кроме 4 (топор)
        for (Map.Entry<Integer, ItemStack> entry : RECIPE.entrySet()) {
            int slot = entry.getKey();
            if (slot == 4) continue; // Топор проверяем отдельно

            ItemStack required = entry.getValue();
            ItemStack actual = container.getItem(slot);

            if (required.isEmpty()) {
                if (!actual.isEmpty()) return ItemStack.EMPTY;
            } else {
                if (!actual.is(required.getItem())) return ItemStack.EMPTY;

                if (required.getItem() == Items.ENCHANTED_BOOK) {
                    if (!hasRequiredEnchantments(actual, required)) return ItemStack.EMPTY;
                }
            }
        }

        // Проверяем топор в слоте 4
        ItemStack axe = container.getItem(4);

        // 1. Должен быть золотой топор
        if (!axe.is(Items.GOLDEN_AXE)) return ItemStack.EMPTY;

        // 2. Проверка минимальной прочности из конфига
        if (!isAxeDurabilityValid(axe)) return ItemStack.EMPTY;

        // 3. Проверка NBT тегов согласно конфигурации
        if (!isNbtAllowed(axe)) return ItemStack.EMPTY;

        // Все проверки пройдены - создаем новый топор
        return createLumberjackAxe();
    }

    private static boolean isAxeDurabilityValid(ItemStack axe) {
        int minDurability = ModConfig.COMMON.minAxeDurability.get();

        // Если прочность 0, принимаем любой топор (даже сломанный)
        if (minDurability == 0) return true;

        // Вычисляем текущую прочность
        int maxDurability = axe.getMaxDamage(); // Для золотого топора = 32
        int currentDamage = axe.getDamageValue();
        int currentDurability = maxDurability - currentDamage;

        ToporDrovosekaMS3.LOGGER.debug("Axe durability: {}/{} (min required: {})",
                currentDurability, maxDurability, minDurability);

        return currentDurability >= minDurability;
    }

    private static boolean isNbtAllowed(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) {
            return true; // Нет NBT тегов - всегда разрешено
        }

        // Создаем копию для проверки
        CompoundTag tagCopy = tag.copy();

        // Проверяем Enchantments
        if (ModConfig.COMMON.requireNoEnchantments.get() && tagCopy.contains("Enchantments")) {
            ToporDrovosekaMS3.LOGGER.debug("Axe has Enchantments tag");
            return false;
        }
        tagCopy.remove("Enchantments");

        // Проверяем display тег (имя, описание)
        if (tagCopy.contains("display")) {
            CompoundTag display = tagCopy.getCompound("display");

            if (ModConfig.COMMON.requireNoCustomName.get() && display.contains("Name")) {
                ToporDrovosekaMS3.LOGGER.debug("Axe has custom name");
                return false;
            }

            if (ModConfig.COMMON.requireNoLore.get() && display.contains("Lore")) {
                ToporDrovosekaMS3.LOGGER.debug("Axe has lore");
                return false;
            }

            // Если display пустой после проверок, удаляем его
            if (display.isEmpty()) {
                tagCopy.remove("display");
            }
        }

        // Проверяем Unbreakable
        if (ModConfig.COMMON.requireNoUnbreakable.get() && tagCopy.contains("Unbreakable")) {
            ToporDrovosekaMS3.LOGGER.debug("Axe has Unbreakable");
            return false;
        }
        tagCopy.remove("Unbreakable");

        // Проверяем CanDestroy
        if (ModConfig.COMMON.requireNoCanDestroy.get() && tagCopy.contains("CanDestroy")) {
            ToporDrovosekaMS3.LOGGER.debug("Axe has CanDestroy");
            return false;
        }
        tagCopy.remove("CanDestroy");

        // Проверяем CanPlaceOn
        if (ModConfig.COMMON.requireNoCanPlaceOn.get() && tagCopy.contains("CanPlaceOn")) {
            ToporDrovosekaMS3.LOGGER.debug("Axe has CanPlaceOn");
            return false;
        }
        tagCopy.remove("CanPlaceOn");

        // Damage всегда присутствует на инструментах, поэтому не проверяем его как "другой тег"
        tagCopy.remove("Damage");

        // Проверка остальных тегов, если включена опция
        if (ModConfig.COMMON.requireNoOtherTags.get() && !tagCopy.isEmpty()) {
            ToporDrovosekaMS3.LOGGER.debug("Axe has unknown NBT tags: {}", tagCopy.getAllKeys());
            return false;
        }

        return true;
    }

    private static boolean hasRequiredEnchantments(ItemStack actualBook, ItemStack requiredBook) {
        var actualEnchants = EnchantmentHelper.getEnchantments(actualBook);
        var requiredEnchants = EnchantmentHelper.getEnchantments(requiredBook);

        for (var entry : requiredEnchants.entrySet()) {
            if (!actualEnchants.containsKey(entry.getKey()) ||
                    actualEnchants.get(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private static ItemStack createEnchantedBook(net.minecraft.world.item.enchantment.Enchantment enchantment, int level) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        var enchants = new HashMap<net.minecraft.world.item.enchantment.Enchantment, Integer>();
        enchants.put(enchantment, level);
        EnchantmentHelper.setEnchantments(enchants, book);
        return book;
    }

    private static ItemStack createLumberjackAxe() {
        ItemStack axe = new ItemStack(Items.GOLDEN_AXE);
        CompoundTag tag = new CompoundTag();

        // 1. DISPLAY
        CompoundTag display = new CompoundTag();
        display.putString("Name", "{\"text\":\"Топор Дровосека\",\"bold\":true,\"italic\":false,\"color\":\"#ffa500\"}");
        tag.put("display", display);

        // 2. UNBREAKABLE
        tag.putBoolean("Unbreakable", true);

        axe.setTag(tag);

        ToporDrovosekaMS3.LOGGER.info("Lumberjack axe created successfully!");

        return axe;
    }
}