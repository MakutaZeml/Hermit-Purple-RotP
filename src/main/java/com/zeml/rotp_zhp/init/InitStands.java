package com.zeml.rotp_zhp.init;

import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandPose;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.power.impl.stand.StandInstance;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.action.stand.HPCamera;
import com.zeml.rotp_zhp.action.stand.HPDoxx;
import com.zeml.rotp_zhp.action.stand.HPThorns;
import com.zeml.rotp_zhp.action.stand.HamonBreath;
import com.zeml.rotp_zhp.action.stand.projectile.HPGrapple;
import com.zeml.rotp_zhp.action.stand.projectile.HPVineAttack;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.github.standobyte.jojo.init.power.stand.ModStandsInit;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.power.stand.EntityStandRegistryObject;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;

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
            () -> new HPVineAttack(new StandEntityAction.Builder().staminaCost(40).standPerformDuration(10).cooldown(20,10,0.5F)
                    .partsRequired(StandInstance.StandPart.ARMS).standOffsetFromUser(0,0).standUserWalkSpeed(1.0F)
                    .swingHand().standSound(StandEntityAction.Phase.WINDUP,InitSounds.VINE_TRHOW)
                    .standPose(StandPose.RANGED_ATTACK)
                    ));

    public static final RegistryObject<StandEntityAction> HP_VINE_SHIFT = ACTIONS.register("hp_vine_shift",
            () -> new HPVineAttack(new StandEntityAction.Builder().staminaCost(40).standPerformDuration(10)
                    .cooldown(30,35,0.5F)
                    .partsRequired(StandInstance.StandPart.ARMS).standOffsetFromUser(0,0).standUserWalkSpeed(1.0F)
                    .swingHand().standSound(StandEntityAction.Phase.WINDUP,InitSounds.VINE_TRHOW)
                    .standPose(StandPose.RANGED_ATTACK).shiftVariationOf(HP_VINE_ATTACK)
            ));

    public static final RegistryObject<StandEntityAction> HP_BLOCK = ACTIONS.register("hp_block",
            ()->new HPThorns(new StandEntityAction.Builder().staminaCostTick(1).holdType().standUserWalkSpeed(1)
                    .standSound(InitSounds.HP_GRAPPLE_CATCH).resolveLevelToUnlock(3)
                    ));

    public static final RegistryObject<StandEntityAction> HP_GRAPPLE = ACTIONS.register("hp_vine",
            () -> new HPGrapple(new StandEntityAction.Builder().staminaCostTick(1).holdType().standUserWalkSpeed(1.0F)
                    .resolveLevelToUnlock(1).swingHand()
                    .standPose(StandPose.RANGED_ATTACK).standSound(InitSounds.VINE_TRHOW)
                    .partsRequired(StandInstance.StandPart.ARMS)));

    public static final RegistryObject<StandEntityAction> HP_GRAPPLE_ENTITY = ACTIONS.register("hp_vine_entity",
            () -> new HPGrapple(new StandEntityAction.Builder().staminaCostTick(1).holdType().standUserWalkSpeed(1.0F)
                    .resolveLevelToUnlock(2).shiftVariationOf(HP_GRAPPLE).swingHand()
                    .standPose(StandPose.RANGED_ATTACK).standSound(InitSounds.VINE_TRHOW)
                    .partsRequired(StandInstance.StandPart.ARMS)
                    .shiftVariationOf(HP_GRAPPLE)));


    public static final RegistryObject<StandEntityAction> HP_BREATH =ACTIONS.register("hp_breath",
            ()-> new HamonBreath(new StandEntityAction.Builder().heldWalkSpeed(0.0F).holdType()
                    .standSound(ModSounds.BREATH_DEFAULT).standSound(ModSounds.HAMON_CONCENTRATION)));

    public static final RegistryObject<StandEntityAction> HP_DOXX =ACTIONS.register("hp_doxx",
            ()-> new HPDoxx(new StandEntityAction.Builder().standWindupDuration(10).cooldown(20)
                    ));

    public static final RegistryObject<StandEntityAction> HP_CAMERA = ACTIONS.register("hp_camera",
            ()->new HPCamera(new StandEntityAction.Builder().standWindupDuration(10).cooldown(20)));



    public static final EntityStandRegistryObject<EntityStandType<StandStats>, StandEntityType<HermitPurpleEntity>> STAND_HERMITO_PURPLE =
            new EntityStandRegistryObject<>("hermito_purple",
                    STANDS,
                    () -> new EntityStandType.Builder<StandStats>()
                            .color(0xF070D0)
                            .storyPartName(ModStandsInit.PART_3_NAME)
                            .leftClickHotbar(
                                    HP_VINE_ATTACK.get()
                            )
                            .rightClickHotbar(
                                    HP_BREATH.get(),
                                    HP_DOXX.get(),
                                    HP_GRAPPLE.get(),
                                    HP_BLOCK.get()

                            )
                            .defaultStats(StandStats.class, new StandStats.Builder()
                                    .tier(2)
                                    .power(6.0)
                                    .speed(10.0)
                                    .range(3.0)
                                    .durability(14.0)
                                    .precision(7.0)
                                    .randomWeight(0)
                            )
                            .addOst(InitSounds.JOSEPH_OST)
                            .disableManualControl().addSummonShout(InitSounds.USER_HP)
                            .build(),

                    InitEntities.ENTITIES,
                    () -> new StandEntityType<HermitPurpleEntity>(HermitPurpleEntity::new, 0.65F, 1.8F)
                            .summonSound(InitSounds.HERMITO_PURPLE_SUMMON)
                            .unsummonSound(InitSounds.HERMITO_PURPLE_UNSUMMON))
                    .withDefaultStandAttributes();
}
