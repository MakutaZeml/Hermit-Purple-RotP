package com.zeml.rotp_zhp.util;

import com.mojang.brigadier.CommandDispatcher;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.command.HermitStructureCommand;
import com.zeml.rotp_zhp.command.HermitTargetCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.world.World;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = RotpHermitPurpleAddon.MOD_ID)
public class HermitForgeBusEventSubscriber {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event){
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        HermitTargetCommand.register(dispatcher);
        HermitStructureCommand.register(dispatcher);

    }
}
