package com.gavirchik.topordrovosekams3.configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {
    public static class Common {
        // Basic settings
        public final ForgeConfigSpec.BooleanValue enableMod;
        public final ForgeConfigSpec.IntValue minAxeDurability; // Backward compatibility value

        // NBT Tag Verification Settings
        public final ForgeConfigSpec.BooleanValue requireNoEnchantments;
        public final ForgeConfigSpec.BooleanValue requireNoCustomName;
        public final ForgeConfigSpec.BooleanValue requireNoLore;
        public final ForgeConfigSpec.BooleanValue requireNoUnbreakable;
        public final ForgeConfigSpec.BooleanValue requireNoCanDestroy;
        public final ForgeConfigSpec.BooleanValue requireNoCanPlaceOn;
        public final ForgeConfigSpec.BooleanValue requireNoOtherTags;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("General settings")
                    .push("general");

            enableMod = builder
                    .comment("Enable/disable the mod")
                    .define("enableMod", true);

            minAxeDurability = builder
                    .comment("Minimum axe durability required (reserved for future use)",
                            "Note: Currently only axes with Damage=0 (brand new) are accepted",
                            "This setting is kept for backward compatibility")
                    .defineInRange("minAxeDurability", 32, 0, 32);

            builder.pop();

            builder.comment("NBT Tags Check Settings",
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