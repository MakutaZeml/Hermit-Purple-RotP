package com.zeml.rotp_zhp.util;

import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import net.minecraft.world.World;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = RotpHermitPurpleAddon.MOD_ID)
public class HermitForgeBusEventSubscriber {

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        HermitEntityTypeToInstance.init((World) event.getWorld());
    }


}
