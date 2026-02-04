package com.gavirchik.topordrovoseka.util;

import com.gavirchik.topordrovoseka.config.ModConfig;
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
        // Slot 4: checked separately (golden axe)
        RECIPE.put(5, new ItemStack(Items.NETHERITE_BLOCK));

        RECIPE.put(6, new ItemStack(Items.NETHER_STAR));
        RECIPE.put(7, createEnchantedBook(Enchantments.MENDING, 1));
        RECIPE.put(8, ItemStack.EMPTY);
    }

    public static ItemStack checkRecipe(Container container) {
        if (!ModConfig.ENABLE_MOD.get()) return ItemStack.EMPTY;
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

        // 2. Checking the minimum durability from the config
        if (!isAxeDurabilityValid(axe)) return ItemStack.EMPTY;

        // All checks have been passed - we are creating a new axe
        return createLumberjackAxe();
    }

    private static boolean isAxeDurabilityValid(ItemStack axe) {
        int minDurability = ModConfig.MIN_AXE_DURABILITY.get();

        // If the durability is 0, we accept any axe (even a broken one)
        if (minDurability == 0) return true;

        // Calculating the current durability
        int maxDurability = axe.getMaxDamage(); // For the golden axe = 32
        int currentDamage = axe.getDamageValue();
        int currentDurability = maxDurability - currentDamage;

        // We check whether the minimum durability from the config matches
        return currentDurability >= minDurability;
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
        return axe;
    }
}