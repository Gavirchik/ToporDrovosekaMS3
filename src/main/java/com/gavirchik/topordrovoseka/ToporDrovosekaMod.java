package com.gavirchik.topordrovoseka;

import com.gavirchik.topordrovoseka.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;

@Mod("topor_drovoseka_ms3")
public class ToporDrovosekaMod {

    public ToporDrovosekaMod() {
        // Configuration registration
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC);
    }
}