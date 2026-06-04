package com.gavirchik.topordrovosekams3;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class NewAxeRecipe {

    // Dispenser 3x3 slot layout:
    //  [0]пусто  [1]N  [2]N
    //  [3]U      [4]A  [5]N
    //  [6]S      [7]M  [8]пусто
    //
    // N=Netherite Block, U=Enchanted Book (Unbreaking III),
    // A=Golden Axe, S=Nether Star, M=Enchanted Book (Mending I)

    public static boolean matches(DispenserBlockEntity blockEntity, ServerLevel level) {
        if (!Config.enableMod) return false;
        if (blockEntity.getContainerSize() < 9) return false;

        HolderLookup.RegistryLookup<Enchantment> enchantLookup =
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        Holder.Reference<Enchantment> unbreaking = enchantLookup.getOrThrow(Enchantments.UNBREAKING);
        Holder.Reference<Enchantment> mending    = enchantLookup.getOrThrow(Enchantments.MENDING);

        return blockEntity.getItem(0).isEmpty()
            && blockEntity.getItem(1).is(Items.NETHERITE_BLOCK)
            && blockEntity.getItem(2).is(Items.NETHERITE_BLOCK)
            && isEnchantedBook(blockEntity.getItem(3), unbreaking, 3)
            && isGoldenAxe(blockEntity.getItem(4))
            && blockEntity.getItem(5).is(Items.NETHERITE_BLOCK)
            && blockEntity.getItem(6).is(Items.NETHER_STAR)
            && isEnchantedBook(blockEntity.getItem(7), mending, 1)
            && blockEntity.getItem(8).isEmpty();
    }

    public static void craft(ServerLevel level, BlockPos pos, DispenserBlockEntity blockEntity) {
        ItemStack axe = createWoodcutterAxe();

        for (int i = 0; i < blockEntity.getContainerSize(); i++) {
            blockEntity.setItem(i, ItemStack.EMPTY);
        }
        blockEntity.setChanged();

        Direction facing = level.getBlockState(pos).getValue(DispenserBlock.FACING);
        spawnItemInFront(level, pos, facing, axe);

        level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static ItemStack createWoodcutterAxe() {
        ItemStack axe = new ItemStack(Items.GOLDEN_AXE);
        axe.set(DataComponents.CUSTOM_NAME,
            Component.literal("Топор Дровосека")
                .withStyle(Style.EMPTY
                    .withBold(true)
                    .withItalic(false)
                    .withColor(TextColor.fromRgb(0xFFA500))
                )
        );
        axe.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
        return axe;
    }

    private static boolean isGoldenAxe(ItemStack stack) {
        if (!stack.is(Items.GOLDEN_AXE)) return false;

        // Durability: remaining = maxDamage - damage; must be >= minAxeDurability
        int remaining = stack.getMaxDamage() - stack.getDamageValue();
        if (remaining < Config.minAxeDurability) return false;

        DataComponentPatch patch = stack.getComponentsPatch();

        if (Config.requireNoEnchantments  && isComponentSet(patch, DataComponents.ENCHANTMENTS))  return false;
        if (Config.requireNoCustomName    && isComponentSet(patch, DataComponents.CUSTOM_NAME))    return false;
        if (Config.requireNoLore          && isComponentSet(patch, DataComponents.LORE))           return false;
        if (Config.requireNoUnbreakable   && isComponentSet(patch, DataComponents.UNBREAKABLE))    return false;
        if (Config.requireNoCanDestroy    && isComponentSet(patch, DataComponents.CAN_BREAK))      return false;
        if (Config.requireNoCanPlaceOn    && isComponentSet(patch, DataComponents.CAN_PLACE_ON))   return false;

        if (Config.requireNoOtherTags) {
            // Build the set of component types that are explicitly permitted in the patch
            Set<DataComponentType<?>> allowed = new HashSet<>();
            allowed.add(DataComponents.DAMAGE); // durability is always OK
            if (!Config.requireNoEnchantments) allowed.add(DataComponents.ENCHANTMENTS);
            if (!Config.requireNoCustomName)   allowed.add(DataComponents.CUSTOM_NAME);
            if (!Config.requireNoLore)         allowed.add(DataComponents.LORE);
            if (!Config.requireNoUnbreakable)  allowed.add(DataComponents.UNBREAKABLE);
            if (!Config.requireNoCanDestroy)   allowed.add(DataComponents.CAN_BREAK);
            if (!Config.requireNoCanPlaceOn)   allowed.add(DataComponents.CAN_PLACE_ON);

            for (var entry : patch.entrySet()) {
                if (entry.getValue().isPresent() && !allowed.contains(entry.getKey())) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isComponentSet(DataComponentPatch patch, DataComponentType<?> type) {
        Optional<?> opt = patch.get(type);
        return opt != null && opt.isPresent();
    }

    // Checks that the book has exactly one stored enchantment at the required level
    private static boolean isEnchantedBook(ItemStack stack, Holder<Enchantment> enchantment, int requiredLevel) {
        if (!stack.is(Items.ENCHANTED_BOOK)) return false;
        ItemEnchantments enchants = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchants == null || enchants.entrySet().size() != 1) return false;
        return enchants.getLevel(enchantment) == requiredLevel;
    }

    private static void spawnItemInFront(ServerLevel level, BlockPos pos, Direction facing, ItemStack stack) {
        double x = pos.getX() + 0.5 + 0.7 * facing.getStepX();
        double y = pos.getY() + 0.5
                - (facing.getAxis() == Direction.Axis.Y ? 0.0 : 0.125)
                + 0.7 * facing.getStepY();
        double z = pos.getZ() + 0.5 + 0.7 * facing.getStepZ();

        double vx = 0.25 * facing.getStepX();
        double vy = facing.getAxis() == Direction.Axis.Y ? 0.25 * facing.getStepY() : 0.1;
        double vz = 0.25 * facing.getStepZ();

        ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack, vx, vy, vz);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }
}