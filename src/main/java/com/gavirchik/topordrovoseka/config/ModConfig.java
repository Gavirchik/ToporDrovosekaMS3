package com.gavirchik.topordrovoseka.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE_MOD;
    public static final ForgeConfigSpec.IntValue MIN_AXE_DURABILITY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Mod settings Topor Drovoseka MS3")
                .push("general");

        ENABLE_MOD = builder
                .comment("Enable the mod")
                .define("enableMod", true);

        MIN_AXE_DURABILITY = builder
                .comment("Minimum durability of a golden axe for crafting",
                        "0 = any",
                        "31 = only a new")
                .defineInRange("minAxeDurability", 31, 0, 31);

        builder.pop();
        SPEC = builder.build();
    }
}