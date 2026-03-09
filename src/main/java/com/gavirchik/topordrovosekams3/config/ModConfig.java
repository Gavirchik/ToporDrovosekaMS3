package com.gavirchik.topordrovosekams3.configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {
    public static class Common {
        // Основные настройки
        public final ForgeConfigSpec.BooleanValue enableMod;
        public final ForgeConfigSpec.IntValue minAxeDurability;

        // Настройки проверки NBT тегов (только основные)
        public final ForgeConfigSpec.BooleanValue requireNoEnchantments;   // Enchantments - чары
        public final ForgeConfigSpec.BooleanValue requireNoCustomName;     // display.Name - кастомное имя
        public final ForgeConfigSpec.BooleanValue requireNoLore;           // display.Lore - описание
        public final ForgeConfigSpec.BooleanValue requireNoUnbreakable;    // Unbreakable - неломаемый
        public final ForgeConfigSpec.BooleanValue requireNoCanDestroy;     // CanDestroy - может разрушать
        public final ForgeConfigSpec.BooleanValue requireNoCanPlaceOn;     // CanPlaceOn - может ставить на
        public final ForgeConfigSpec.BooleanValue requireNoOtherTags;      // Любые другие теги

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("General settings")
                    .push("general");

            enableMod = builder
                    .comment("Enable/disable the mod")
                    .define("enableMod", true);

            minAxeDurability = builder
                    .comment("Minimum axe durability required (0-31)",
                            "0 = any golden axe, even broken",
                            "31 = only brand new axe (full durability)")
                    .defineInRange("minAxeDurability", 31, 0, 31);

            builder.pop();

            builder.comment("NBT Tags Check Settings (Basic tags only)",
                            "Configure which NBT tags are allowed/prohibited on the golden axe",
                            "True = axe must NOT have this tag",
                            "False = axe can have this tag")
                    .push("nbt_checks");

            requireNoEnchantments = builder
                    .comment("Require no enchantments (Enchantments tag)")
                    .define("requireNoEnchantments", true);

            requireNoCustomName = builder
                    .comment("Require no custom name (display.Name tag)")
                    .define("requireNoCustomName", true);

            requireNoLore = builder
                    .comment("Require no lore text (display.Lore tag)")
                    .define("requireNoLore", true);

            requireNoUnbreakable = builder
                    .comment("Require no Unbreakable tag")
                    .define("requireNoUnbreakable", true);

            requireNoCanDestroy = builder
                    .comment("Require no CanDestroy tag")
                    .define("requireNoCanDestroy", true);

            requireNoCanPlaceOn = builder
                    .comment("Require no CanPlaceOn tag")
                    .define("requireNoCanPlaceOn", true);

            requireNoOtherTags = builder
                    .comment("Require no other custom tags not listed above",
                            "If true, any tag not in the list above will cause rejection")
                    .define("requireNoOtherTags", true);

            builder.pop();
        }
    }

    public static final ForgeConfigSpec SPEC;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }
}