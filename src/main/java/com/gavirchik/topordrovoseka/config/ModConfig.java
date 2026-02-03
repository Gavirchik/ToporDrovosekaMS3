package com.gavirchik.topordrovoseka.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE_MOD;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Mod Settings Topor Drovoseka MS3")
                .push("general");

        ENABLE_MOD = builder
                .comment("Enable the mod")
                .define("enableMod", true);

        builder.pop();
        SPEC = builder.build();
    }
}