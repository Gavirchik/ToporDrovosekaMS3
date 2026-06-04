package com.gavirchik.topordrovosekasms3;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = TopordrovosekasMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // [general]
    private static final ModConfigSpec.BooleanValue ENABLE_MOD;
    private static final ModConfigSpec.IntValue MIN_AXE_DURABILITY;

    // [nbt_checks]
    private static final ModConfigSpec.BooleanValue REQUIRE_NO_ENCHANTMENTS;
    private static final ModConfigSpec.BooleanValue REQUIRE_NO_CUSTOM_NAME;
    private static final ModConfigSpec.BooleanValue REQUIRE_NO_LORE;
    private static final ModConfigSpec.BooleanValue REQUIRE_NO_UNBREAKABLE;
    private static final ModConfigSpec.BooleanValue REQUIRE_NO_CAN_DESTROY;
    private static final ModConfigSpec.BooleanValue REQUIRE_NO_CAN_PLACE_ON;
    private static final ModConfigSpec.BooleanValue REQUIRE_NO_OTHER_TAGS;

    static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("General settings").push("general");

        ENABLE_MOD = BUILDER
                .comment("Enable/disable the mod")
                .define("enableMod", true);

        MIN_AXE_DURABILITY = BUILDER
                .comment(
                        "Minimum axe durability required (0-31)",
                        "0 = any golden axe, even broken",
                        "31 = only brand new axe (full durability)"
                )
                .defineInRange("minAxeDurability", 31, 0, 31);

        BUILDER.pop();

        BUILDER.comment(
                "NBT Tags Check Settings (Basic tags only)",
                "Configure which NBT tags are allowed/prohibited on the golden axe",
                "True = axe must NOT have this tag",
                "False = axe can have this tag"
        ).push("nbt_checks");

        REQUIRE_NO_ENCHANTMENTS = BUILDER
                .comment("Require no enchantments (Enchantments tag)")
                .define("requireNoEnchantments", true);

        REQUIRE_NO_CUSTOM_NAME = BUILDER
                .comment("Require no custom name (display.Name tag)")
                .define("requireNoCustomName", true);

        REQUIRE_NO_LORE = BUILDER
                .comment("Require no lore text (display.Lore tag)")
                .define("requireNoLore", true);

        REQUIRE_NO_UNBREAKABLE = BUILDER
                .comment("Require no Unbreakable tag")
                .define("requireNoUnbreakable", true);

        REQUIRE_NO_CAN_DESTROY = BUILDER
                .comment("Require no CanDestroy tag")
                .define("requireNoCanDestroy", true);

        REQUIRE_NO_CAN_PLACE_ON = BUILDER
                .comment("Require no CanPlaceOn tag")
                .define("requireNoCanPlaceOn", true);

        REQUIRE_NO_OTHER_TAGS = BUILDER
                .comment(
                        "Require no other custom tags not listed above",
                        "If true, any tag not in the list above will cause rejection"
                )
                .define("requireNoOtherTags", true);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static boolean enableMod;
    public static int minAxeDurability;
    public static boolean requireNoEnchantments;
    public static boolean requireNoCustomName;
    public static boolean requireNoLore;
    public static boolean requireNoUnbreakable;
    public static boolean requireNoCanDestroy;
    public static boolean requireNoCanPlaceOn;
    public static boolean requireNoOtherTags;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enableMod          = ENABLE_MOD.get();
        minAxeDurability   = MIN_AXE_DURABILITY.get();
        requireNoEnchantments = REQUIRE_NO_ENCHANTMENTS.get();
        requireNoCustomName   = REQUIRE_NO_CUSTOM_NAME.get();
        requireNoLore         = REQUIRE_NO_LORE.get();
        requireNoUnbreakable  = REQUIRE_NO_UNBREAKABLE.get();
        requireNoCanDestroy   = REQUIRE_NO_CAN_DESTROY.get();
        requireNoCanPlaceOn   = REQUIRE_NO_CAN_PLACE_ON.get();
        requireNoOtherTags    = REQUIRE_NO_OTHER_TAGS.get();
    }
}