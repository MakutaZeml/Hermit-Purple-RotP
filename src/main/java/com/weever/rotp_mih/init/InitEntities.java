package com.weever.rotp_mih.init;

import com.weever.rotp_mih.RotpMadeInHeavenAddon;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = RotpMadeInHeavenAddon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class InitEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, RotpMadeInHeavenAddon.MOD_ID);


}
