package com.gavirchik.topordrovosekasms3.mixin;

import com.gavirchik.topordrovosekasms3.NewAxeRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    // In 1.21.1 the activation method is dispenseFrom(ServerLevel, BlockState, BlockPos)
    @Inject(
        method = "dispenseFrom(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onDispenseFrom(ServerLevel level, BlockState state, BlockPos pos, CallbackInfo ci) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof DispenserBlockEntity dispenser)) return;

        if (NewAxeRecipe.matches(dispenser, level)) {
            NewAxeRecipe.craft(level, pos, dispenser);
            ci.cancel();
        }
    }
}