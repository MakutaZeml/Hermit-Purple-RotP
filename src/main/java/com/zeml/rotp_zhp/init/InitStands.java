package com.zeml.rotp_zhp.init;

import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandPose;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.power.impl.stand.StandInstance;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.action.stand.*;
import com.zeml.rotp_zhp.action.stand.projectile.*;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.github.standobyte.jojo.init.power.stand.ModStandsInit;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.power.stand.EntityStandRegistryObject;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;

import com.zeml.rotp_zhp.power.impl.stand.type.HermitPurpleStandType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class InitStands {
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<Action<?>> ACTIONS = DeferredRegister.create(
            (Class<Action<?>>) ((Class<?>) Action.class), RotpHermitPurpleAddon.MOD_ID);
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<StandType<?>> STANDS = DeferredRegister.create(
            (Class<StandType<?>>) ((Class<?>) StandType.class), RotpHermitPurpleAddon.MOD_ID);

    // ======================================== Hermit Purple ========================================


    public static final RegistryObject<StandEntityAction> HP_VINE_ATTACK = ACTIONS.register("hp_vine_attack",
            () -> new HPVineAttack(new StandEntityAction.Builder().staminaCost(40).standPerformDuration(10).cooldown(10,5,0.5F)
                    .standOffsetFromUser(0,0).standUserWalkSpeed(1.0F)
                    .swingHand().standSound(StandEntityAction.Phase.WINDUP,InitSounds.VINE_TRHOW)
                    .standPose(StandPose.RANGED_ATTACK)
                    ));

    public static final RegistryObject<StandEntityAction> HP_VINE_SHIFT = ACTIONS.register("hp_vine_shift",
            () -> new HPVineAttack(new StandEntityAction.Builder().staminaCost(40).standPerformDuration(10)
                    .cooldown(30,35,0.5F)
                    .standOffsetFromUser(0,0).standUserWalkSpeed(1.0F)
                    .swingHand().standSound(StandEntityAction.Phase.WINDUP,InitSounds.VINE_TRHOW)
                    .standPose(StandPose.RANGED_ATTACK).shiftVariationOf(HP_VINE_ATTACK)
            ));


    public static final RegistryObject<StandEntityAction> HP_GRAB_COMMAND = ACTIONS.register("hp_grab",
            () -> new HPGrabCommand(new StandEntityAction.Builder().staminaCost(40)
                    .resolveLevelToUnlock(2).swingHand().holdType().standUserWalkSpeed(1.0F)
                    .cooldown(100,0,0.5F)
                    .standPose(StandPose.RANGED_ATTACK).standSound(InitSounds.VINE_TRHOW)
                    ));
    public static final RegistryObject<StandEntityAction> HP_GRAB_OVERDRIVE = ACTIONS.register("hp_grab_od",
            () -> new HPGrabOverdrive(new StandEntityAction.Builder().staminaCost(200).standSound(ModSounds.HAMON_CONCENTRATION)
                    .standUserWalkSpeed(1.0F).shout(InitSounds.USER_OVER_DRIVE)
            ));

    public static final RegistryObject<StandEntityAction> HP_HEAL_VINE = ACTIONS.register("hp_grab_heal",
            () -> new HPGrabHealOverDrive(new StandEntityAction.Builder().staminaCost(200).standSound(ModSounds.HAMON_CONCENTRATION)
                    .standUserWalkSpeed(1.0F).shiftVariationOf(HP_GRAB_OVERDRIVE)
            ));


    public static final RegistryObject<StandEntityAction> HP_BLOCK = ACTIONS.register("hp_block",
            ()->new HPThorns(new StandEntityAction.Builder().staminaCostTick(1).holdType().standUserWalkSpeed(1)
                    .standSound(InitSounds.HP_GRAPPLE_CATCH).resolveLevelToUnlock(3)
                    ));



    public static final RegistryObject<StandEntityAction> HP_GRAPPLE = ACTIONS.register("hp_vine",
            () -> new HPGrapple(new StandEntityAction.Builder().staminaCostTick(1).holdType().standUserWalkSpeed(1.0F)
                    .resolveLevelToUnlock(1).swingHand()
                    .standPose(StandPose.RANGED_ATTACK).standSound(InitSounds.VINE_TRHOW)
            ));

    public static final RegistryObject<StandEntityAction> HP_GRAPPLE_ENTITY = ACTIONS.register("hp_vine_entity",
            () -> new HPGrapple(new StandEntityAction.Builder().staminaCostTick(1).holdType().standUserWalkSpeed(1.0F)
                    .resolveLevelToUnlock(1).shiftVariationOf(HP_GRAPPLE).swingHand()
                    .standPose(StandPose.RANGED_ATTACK).standSound(InitSounds.VINE_TRHOW)
                    .shiftVariationOf(HP_GRAPPLE)));

    public static final RegistryObject<StandEntityAction> HP_BARRIER = ACTIONS.register("hp_barrier",
            ()->new HPAttachBarrier(new StandEntityAction.Builder().staminaCost(50)
                    .noResolveUnlock()
            ));

    public static final RegistryObject<StandEntityAction> HP_NET = ACTIONS.register("hp_net",
            ()->new HPAttachBarrier(new StandEntityAction.Builder().staminaCost(300)
                    .resolveLevelToUnlock(4)
            ));

    public static final RegistryObject<StandEntityAction> HP_UNBARRIER = ACTIONS.register("hp_unbarrier",
            ()->new HPUnattachBarrier(new StandEntityAction.Builder().shiftVariationOf(HP_BARRIER)
                    .noResolveUnlock()
            ));
    public static final RegistryObject<StandEntityAction> HP_OH_NO = ACTIONS.register("cringe",
            ()->new HPCringe(new StandEntityAction.Builder().staminaCost(50)
                    .resolveLevelToUnlock(4).shout(InitSounds.USER_CRINGE).cooldown(300)
            ));


    public static final RegistryObject<StandEntityAction> HP_BREATH =ACTIONS.register("hp_breath",
            ()-> new HamonBreath(new StandEntityAction.Builder().heldWalkSpeed(0.0F).holdType()
                    .standSound(InitSounds.USER_BREATH).standSound(ModSounds.HAMON_CONCENTRATION)));

    public static final RegistryObject<StandEntityAction> HP_DOXX =ACTIONS.register("hp_doxx",
            ()-> new HPDoxx(new StandEntityAction.Builder().standWindupDuration(10).cooldown(200)
                    .standSound(InitSounds.HERMITO_PURPLE_SUMMON).shout(InitSounds.USER_HP)
                    .staminaCost(30)
                    ));

    public static final RegistryObject<StandEntityAction> HP_TARGET =ACTIONS.register("hp_target",
            ()-> new HPTargetSelection(new StandEntityAction.Builder()
            ));

    public static final EntityStandRegistryObject<HermitPurpleStandType<StandStats>, StandEntityType<HermitPurpleEntity>> STAND_HERMITO_PURPLE =
            new EntityStandRegistryObject<>("hermito_purple",
                    STANDS,
                    () -> new HermitPurpleStandType.Builder<StandStats>()
                            .color(0xF070D0)
                            .storyPartName(ModStandsInit.PART_3_NAME)
                            .leftClickHotbar(
                                    HP_VINE_ATTACK.get(),
                                    HP_GRAB_COMMAND.get(),
                                    HP_OH_NO.get()
                            )
                            .rightClickHotbar(
                                    HP_BREATH.get(),
                                    HP_DOXX.get(),
                                    HP_TARGET.get(),
                                    HP_GRAPPLE.get(),
                                    HP_BARRIER.get(),
                                    HP_BLOCK.get()

                            )
                            .defaultStats(StandStats.class, new StandStats.Builder()
                                    .tier(2)
                                    .power(6.0)
                                    .speed(10.0)
                                    .range(3.0)
                                    .durability(14.0)
                                    .precision(7.0)
                                    .randomWeight(2)
                            )
                            .addOst(InitSounds.JOSEPH_OST)
                            .addSummonShout(InitSounds.USER_HP)
                            .build(),

                    InitEntities.ENTITIES,
                    () -> new StandEntityType<HermitPurpleEntity>(HermitPurpleEntity::new, 0.65F, 1.8F)
                            .summonSound(InitSounds.HERMITO_PURPLE_SUMMON)
                            .unsummonSound(InitSounds.HERMITO_PURPLE_UNSUMMON))
                    .withDefaultStandAttributes();
}
