package com.weever.rotp_mih.init;

import com.weever.rotp_mih.RotpMadeInHeavenAddon;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.util.mc.OstSoundList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class InitSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RotpMadeInHeavenAddon.MOD_ID);
    
    public static final RegistryObject<SoundEvent> PUCCI_MIH = SOUNDS.register("pucci_mih", 
            () -> new SoundEvent(new ResourceLocation(RotpMadeInHeavenAddon.MOD_ID, "pucci_mih")));

    public static final Supplier<SoundEvent> MIH_SUMMON = ModSounds.STAND_SUMMON_DEFAULT;
    
    public static final Supplier<SoundEvent> MIH_UNSUMMON = ModSounds.STAND_UNSUMMON_DEFAULT;
    
    public static final Supplier<SoundEvent> MIH_PUNCH_LIGHT = ModSounds.STAND_PUNCH_LIGHT;
    
    public static final Supplier<SoundEvent> MIH_PUNCH_HEAVY = ModSounds.STAND_PUNCH_HEAVY;
    
    public static final Supplier<SoundEvent> MIH_BARRAGE = SOUNDS.register("mih_barrage",
            () -> new SoundEvent(new ResourceLocation(RotpMadeInHeavenAddon.MOD_ID, "mih_barrage")));

    public static final RegistryObject<SoundEvent> MIH_REMOVE_DISC = SOUNDS.register("mih_remove_disc",
            () -> new SoundEvent(new ResourceLocation(RotpMadeInHeavenAddon.MOD_ID, "mih_remove_disc")));

    public static final RegistryObject<SoundEvent> MIH_USHYA = SOUNDS.register("mih_ushya",
            () -> new SoundEvent(new ResourceLocation(RotpMadeInHeavenAddon.MOD_ID, "mih_ushya")));


    static final OstSoundList MIH_OST = new OstSoundList(new ResourceLocation(RotpMadeInHeavenAddon.MOD_ID, "mih_ost"), SOUNDS);

}
