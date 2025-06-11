package com.zeml.rotp_zhp.init;

import com.github.standobyte.jojo.init.ModSounds;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.github.standobyte.jojo.util.mc.OstSoundList;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RotpHermitPurpleAddon.MOD_ID);

    public static final RegistryObject<SoundEvent> HERMITO_PURPLE_SUMMON = SOUNDS.register("hp_summon",
            () -> new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "hp_summon")));

    public static final RegistryObject<SoundEvent> HP_GRAPPLE_CATCH =SOUNDS.register("hp_grap",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID,"hp_grap")));

    public static final RegistryObject<SoundEvent> HP_GRAPPLE_ENT =SOUNDS.register("hp_grp_ent",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID,"hp_grp_ent")));

    public static final RegistryObject<SoundEvent> VINE_TRHOW =SOUNDS.register("hp_throw",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID,"hp_throw")));

    public static final RegistryObject<SoundEvent> HERMITO_PURPLE_SPARK = SOUNDS.register("hp_spark",
            () -> new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "hp_spark")));

    public static final RegistryObject<SoundEvent> VOID =SOUNDS.register("void",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID,"void")));

    public static final RegistryObject<SoundEvent> HERMITO_PURPLE_UNSUMMON = SOUNDS.register("hp_unsummon",
            () -> new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "hp_unsummon")));


    public static final RegistryObject<SoundEvent> USER_HP = SOUNDS.register("joseph_hp",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "joseph_hp"))
            );

    public static final RegistryObject<SoundEvent> USER_OVER = SOUNDS.register("joseph_od",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "joseph_od"))
    );

    public static final RegistryObject<SoundEvent> USER_OVER_DRIVE = SOUNDS.register("joseph_over_drive",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "joseph_over_drive"))
    );

    public static final RegistryObject<SoundEvent> USER_BREATH = SOUNDS.register("joseph_breath",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "joseph_breath"))
    );

    public static final RegistryObject<SoundEvent> USER_CRINGE = SOUNDS.register("joseph_cringe",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "joseph_cringe"))
    );

    static final OstSoundList JOSEPH_OST = new OstSoundList(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "hp_ost"), SOUNDS);


    // ======================================== The Emperor ========================================

    public static final RegistryObject<SoundEvent> EMPEROR_SUMMON = SOUNDS.register("emp_summon",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID,"emp_summon")));

    public static final RegistryObject<SoundEvent> EMPEROR_UNSUMMON = SOUNDS.register("emp_unsummon",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID,"emp_unsummon")));


    public static final RegistryObject<SoundEvent> USER_EMPEROR = SOUNDS.register("hol_emp",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "hol_emp"))
    );

    public static final RegistryObject<SoundEvent> EMP_SHOT =SOUNDS.register("emp_shot",
            ()->new SoundEvent(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID,"emp_shot")));


    static final OstSoundList HOL_OSST = new OstSoundList(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "emp_ost"), SOUNDS);

}
