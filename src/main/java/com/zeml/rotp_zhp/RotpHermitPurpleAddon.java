package com.zeml.rotp_zhp;

import com.zeml.rotp_zhp.capability.CapabilityHandler;
import com.zeml.rotp_zhp.init.InitEntities;
import com.zeml.rotp_zhp.init.InitTags;
import com.zeml.rotp_zhp.network.ModNetwork;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zeml.rotp_zhp.init.InitSounds;
import com.zeml.rotp_zhp.init.InitStands;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RotpHermitPurpleAddon.MOD_ID)
public class RotpHermitPurpleAddon {
    // The value here should match an entry in the META-INF/mods.toml file
    public static final String MOD_ID = "rotp_zhp";
    public static final Logger LOGGER = LogManager.getLogger();

    public RotpHermitPurpleAddon() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,HermitConfig.commonSpec);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        InitEntities.ENTITIES.register(modEventBus);
        InitSounds.SOUNDS.register(modEventBus);
        InitStands.ACTIONS.register(modEventBus);
        InitStands.STANDS.register(modEventBus);


        modEventBus.addListener(this::preInit);

    }

    private void preInit(FMLCommonSetupEvent event){
        CapabilityHandler.commonSetupRegister();
        InitTags.iniTags();
        ModNetwork.init();
    }

    private void doClientStuff(final FMLClientSetupEvent event){
    }
    public static Logger getLogger() {
        return LOGGER;
    }
}
