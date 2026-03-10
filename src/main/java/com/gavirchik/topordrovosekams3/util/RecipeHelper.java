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
        // Recipe 3x3
        // 0 1 2
        // 3 4 5
        // 6 7 8

        RECIPE.put(0, ItemStack.EMPTY);
        RECIPE.put(1, new ItemStack(Items.NETHERITE_BLOCK));
        RECIPE.put(2, new ItemStack(Items.NETHERITE_BLOCK));

        RECIPE.put(3, createEnchantedBook(Enchantments.UNBREAKING, 3));
        // Slot 4: checking separately (golden axe)
        RECIPE.put(5, new ItemStack(Items.NETHERITE_BLOCK));

        RECIPE.put(6, new ItemStack(Items.NETHER_STAR));
        RECIPE.put(7, createEnchantedBook(Enchantments.MENDING, 1));
        RECIPE.put(8, ItemStack.EMPTY);
    }

    public static ItemStack checkRecipe(Container container) {
        if (!ModConfig.COMMON.enableMod.get()) return ItemStack.EMPTY;
        if (container.getContainerSize() != 9) return ItemStack.EMPTY;

        // We check all slots except 4 (axe)
        for (Map.Entry<Integer, ItemStack> entry : RECIPE.entrySet()) {
            int slot = entry.getKey();
            if (slot == 4) continue; // We check the axe separately

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

        // Checking the axe in slot 4
        ItemStack axe = container.getItem(4);

        // 1. There must be a golden axe
        if (!axe.is(Items.GOLDEN_AXE)) return ItemStack.EMPTY;

        // 2. 'Damage' check - ONLY 0 (new axe)
        if (!isAxeBrandNew(axe)) return ItemStack.EMPTY;

        // 3. Checking NBT tags according to configuration
        if (!isNbtAllowed(axe)) return ItemStack.EMPTY;

        // All checks have been passed - we are creating a new axe
        return createLumberjackAxe();
    }

    private static boolean isAxeBrandNew(ItemStack axe) {
        // We get the value of 'Damage' (how many damage units)
        int currentDamage = axe.getDamageValue();

        // Maximum durabulity of the golden axe
        int maxDurability = axe.getMaxDamage(); // = 32

        ToporDrovosekaMS3.LOGGER.debug("Axe - Damage: {}, Remaining: {}/{}",
                currentDamage, maxDurability - currentDamage, maxDurability);

        // ONLY new axes with 'Damage = 0'
        return currentDamage == 0;
    }

    private static boolean isNbtAllowed(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        // Creating a copy for verification
        CompoundTag tagCopy = tag.copy();

        // Checking the 'Enchantments'
        if (ModConfig.COMMON.requireNoEnchantments.get() && tagCopy.contains("Enchantments")) {
            ToporDrovosekaMS3.LOGGER.debug("Axe has Enchantments tag");
            return false;
        }
        tagCopy.remove("Enchantments");

        // Checking the 'display' tag (name, description)
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

            // If the 'display' is empty after checking, delete it.
            if (display.isEmpty()) {
                tagCopy.remove("display");
            }
        }

        // Checking the 'Unbreakable'
        if (ModConfig.COMMON.requireNoUnbreakable.get() && tagCopy.contains("Unbreakable")) {
            ToporDrovosekaMS3.LOGGER.debug("Axe has Unbreakable");
            return false;
        }
        tagCopy.remove("Unbreakable");

        // Checking the 'CanDestroy'
        if (ModConfig.COMMON.requireNoCanDestroy.get() && tagCopy.contains("CanDestroy")) {
            ToporDrovosekaMS3.LOGGER.debug("Axe has CanDestroy");
            return false;
        }
        tagCopy.remove("CanDestroy");

        // Cheсking the 'CanPlaceOn'
        if (ModConfig.COMMON.requireNoCanPlaceOn.get() && tagCopy.contains("CanPlaceOn")) {
            ToporDrovosekaMS3.LOGGER.debug("Axe has CanPlaceOn");
            return false;
        }
        tagCopy.remove("CanPlaceOn");

        // 'Damage' is always present on the instruments, but we have already checked that it is '= 0'
        tagCopy.remove("Damage");

        // Checking the remaining tags if the option is enabled
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