package com.gavirchik.topordrovoseka.mixin;

import com.gavirchik.topordrovoseka.config.ModConfig;
import com.gavirchik.topordrovoseka.util.RecipeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserBlock.class)
public class DispenserMixin {

    @Inject(method = "dispenseFrom", at = @At("HEAD"), cancellable = true)
    private void onDispense(ServerLevel level, BlockPos pos, CallbackInfo ci) {
        if (!ModConfig.ENABLE_MOD.get()) return;

        if (level.getBlockEntity(pos) instanceof DispenserBlockEntity dispenser) {
            ItemStack resultAxe = RecipeHelper.checkRecipe(dispenser);

            if (!resultAxe.isEmpty()) {
                // Clearing all slots
                for (int i = 0; i < dispenser.getContainerSize(); i++) {
                    dispenser.setItem(i, ItemStack.EMPTY);
                }
                dispenser.setChanged();

                // We get the dispenser's direction
                Direction direction = level.getBlockState(pos).getValue(BlockStateProperties.FACING);
                BlockPos frontPos = pos.relative(direction);

                // The position for throwing the item
                double x = pos.getX() + 0.5 + direction.getStepX() * 0.6;
                double y = pos.getY() + 0.5 + direction.getStepY() * 0.6;
                double z = pos.getZ() + 0.5 + direction.getStepZ() * 0.6;

                // Create and discard an item
                net.minecraft.world.entity.item.ItemEntity itemEntity =
                        new net.minecraft.world.entity.item.ItemEntity(
                                level,
                                x,
                                y,
                                z,
                                resultAxe
                        );

                // Ejection rate
                double speed = 0.2;
                double randomSpread = level.random.nextDouble() * 0.1 - 0.05;

                itemEntity.setDeltaMovement(
                        direction.getStepX() * speed + (direction.getStepY() == 0 ? randomSpread : 0),
                        direction.getStepY() * speed + (direction.getStepY() >= 0 ? 0.2 : 0),
                        direction.getStepZ() * speed + (direction.getStepY() == 0 ? randomSpread : 0)
                );

                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);

                // Standard dispenser sounds and effects
                level.levelEvent(1000, pos, 0);
                level.levelEvent(2000, pos, direction.get3DDataValue());

                ci.cancel();
            }
        }
    }
}