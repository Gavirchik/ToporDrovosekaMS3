package com.gavirchik.topordrovosekams3.mixins;

import com.gavirchik.topordrovosekams3.configuration.ModConfig;
import com.gavirchik.topordrovosekams3.util.RecipeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserBlock.class)
public class DispenserMixin {

    @Inject(method = "dispenseFrom", at = @At("HEAD"), cancellable = true)
    private void onDispense(ServerLevel level, BlockPos pos, CallbackInfo ci) {
        if (!ModConfig.COMMON.enableMod.get()) return;

        if (level.getBlockEntity(pos) instanceof DispenserBlockEntity dispenser) {
            ItemStack resultAxe = RecipeHelper.checkRecipe(dispenser);

            if (!resultAxe.isEmpty()) {
                // Очищаем все слоты
                for (int i = 0; i < dispenser.getContainerSize(); i++) {
                    dispenser.setItem(i, ItemStack.EMPTY);
                }
                dispenser.setChanged();

                // Получаем направление раздатчика
                Direction direction = level.getBlockState(pos).getValue(BlockStateProperties.FACING);

                // Ставим топор в слот, откуда раздатчик сам его выбросит
                dispenser.setItem(0, resultAxe);

                // Не отменяем событие - пусть раздатчик сам выбросит предмет
                // ci.cancel(); // НЕ ОТМЕНЯЕМ!
            }
        }
    }
}