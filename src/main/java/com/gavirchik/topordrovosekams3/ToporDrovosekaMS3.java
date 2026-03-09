package com.gavirchik.topordrovosekams3;

import com.gavirchik.topordrovosekams3.configuration.ModConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ToporDrovosekaMS3.MOD_ID)
public class ToporDrovosekaMS3 {
    public static final String MOD_ID = "topordrovosekams3";
    public static final String MOD_NAME = "Topor Drovoseka MS3";
    public static final Logger LOGGER = LogManager.getLogger();

    public ToporDrovosekaMS3() {
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC, MOD_ID + "-common.toml");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("{} initialized!", MOD_NAME);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("{} common setup complete!", MOD_NAME);
    }
}