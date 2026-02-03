package com.gavirchik.topordrovoseka.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import java.util.Map;
import java.util.HashMap;

public class RecipeHelper {

    private static final Map<Integer, ItemStack> RECIPE = new HashMap<>();

    static {
        // Recipe 3x3
        RECIPE.put(0, ItemStack.EMPTY);
        RECIPE.put(1, new ItemStack(Items.NETHERITE_BLOCK));
        RECIPE.put(2, new ItemStack(Items.NETHERITE_BLOCK));

        RECIPE.put(3, createEnchantedBook(Enchantments.UNBREAKING, 3));
        // Slot 4: checked separately
        RECIPE.put(5, new ItemStack(Items.NETHERITE_BLOCK));

        RECIPE.put(6, new ItemStack(Items.NETHER_STAR));
        RECIPE.put(7, createEnchantedBook(Enchantments.MENDING, 1));
        RECIPE.put(8, ItemStack.EMPTY);
    }

    public static ItemStack checkRecipe(Container container) {
        if (container.getContainerSize() != 9) return ItemStack.EMPTY;

        // We check all slots except 4 (axe)
        for (Map.Entry<Integer, ItemStack> entry : RECIPE.entrySet()) {
            int slot = entry.getKey();
            if (slot == 4) continue;

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

        // 2. Must be fully repaired (Damage: 0)
        if (axe.isDamaged()) return ItemStack.EMPTY;

        // All checks have been completed
        return createLumberjackAxe();
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

        // 1. Custom title
        CompoundTag display = new CompoundTag();
        display.putString("Name", "{\"text\":\"Топор Дровосека\",\"bold\":true,\"italic\":false,\"color\":\"#ffa500\"}");
        tag.put("display", display);

        // 2. Unbreakable
        tag.putBoolean("Unbreakable", true);

        axe.setTag(tag);
        return axe;
    }
}