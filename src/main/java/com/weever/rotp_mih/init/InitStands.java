package com.weever.rotp_mih.init;

import com.weever.rotp_mih.entity.stand.stands.MihEntity;
import com.weever.rotp_mih.RotpMadeInHeavenAddon;
import com.weever.rotp_mih.action.stand.*;
import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.stand.*;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.power.stand.EntityStandRegistryObject;
import com.github.standobyte.jojo.init.power.stand.ModStandsInit;
import com.github.standobyte.jojo.power.impl.stand.StandInstance.StandPart;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class InitStands {
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<Action<?>> ACTIONS = DeferredRegister.create(
            (Class<Action<?>>) ((Class<?>) Action.class), RotpMadeInHeavenAddon.MOD_ID);
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<StandType<?>> STANDS = DeferredRegister.create(
            (Class<StandType<?>>) ((Class<?>) StandType.class), RotpMadeInHeavenAddon.MOD_ID);

    // ======================================== Made In Heaven! ========================================

    public static final RegistryObject<StandEntityAction> MIH_PUNCH = ACTIONS.register("mih_punch",
            () -> new StandEntityLightAttack(new StandEntityLightAttack.Builder()));

    public static final RegistryObject<StandEntityAction> MIH_BARRAGE = ACTIONS.register("mih_barrage",
            () -> new StandEntityMeleeBarrage(new StandEntityMeleeBarrage.Builder()
                    .barrageHitSound(ModSounds.THE_WORLD_PUNCH_BARRAGE)));

    public static final RegistryObject<StandEntityHeavyAttack> MIH_IMPALE = ACTIONS.register("mih_impale",
            () -> new Impale(new StandEntityHeavyAttack.Builder()
                    .partsRequired(StandPart.ARMS)));

    public static final RegistryObject<StandEntityHeavyAttack> MIH_HEAVY_PUNCH = ACTIONS.register("mih_heavy_punch",
            () -> new StandEntityHeavyAttack(new StandEntityHeavyAttack.Builder()
                    .partsRequired(StandPart.ARMS)
                    .setFinisherVariation(MIH_IMPALE)
                    .shiftVariationOf(MIH_PUNCH).shiftVariationOf(MIH_BARRAGE)));

//    public static final RegistryObject<StandEntityAction> MIH_LUNGE_STRIKE = ACTIONS.register("mih_lunge_strike",
//            () -> new LungeStrike(new StandEntityAction.Builder()
//                    .standAutoSummonMode(StandEntityAction.AutoSummonMode.OFF_ARM)
//                    .partsRequired(StandPart.MAIN_BODY)));

    public static final RegistryObject<StandEntityAction> MIH_DASH = ACTIONS.register("mih_dash",
            () -> new Dash(new StandEntityAction.Builder()
                    .standAutoSummonMode(StandEntityAction.AutoSummonMode.OFF_ARM)
                    .partsRequired(StandPart.MAIN_BODY)));

    public static final RegistryObject<StandEntityAction> MIH_TWO_STEPS = ACTIONS.register("mih_two_steps",
            () -> new TwoStepsBehind(new StandEntityAction.Builder()
                    .partsRequired(StandPart.ARMS)
                    .shiftVariationOf(MIH_DASH)));

    public static final RegistryObject<StandEntityAction> MIH_TIME_ACCELERATION = ACTIONS.register("mih_time_acceleration",
            () -> new TimeAcceleration(new StandEntityAction.Builder()
                    .partsRequired(StandPart.ARMS)));

    public static final RegistryObject<StandEntityAction> MIH_UNIVERSE_RESET = ACTIONS.register("mih_universe_reset",
            () -> new UniverseReset(new StandEntityAction.Builder().standPerformDuration(500).standUserWalkSpeed(1F)
                    .partsRequired(StandPart.ARMS)));

    public static final RegistryObject<StandEntityAction> MIH_CANCEL_ACTION = ACTIONS.register("mih_cancel",
            () -> new Cancel(new StandEntityAction.Builder()
                    .shiftVariationOf(MIH_TIME_ACCELERATION)
                    .shiftVariationOf(MIH_UNIVERSE_RESET)));

    public static final RegistryObject<StandEntityAction> MIH_BLOCK = ACTIONS.register("mih_block",
            () -> new StandEntityBlock());

    public static final EntityStandRegistryObject<EntityStandType<StandStats>, StandEntityType<MihEntity>> MIH =
            new EntityStandRegistryObject<>("madeinheaven",
                    STANDS,
                    () -> new EntityStandType.Builder<StandStats>()
                            .color(0xFFFFFF)
                            .storyPartName(ModStandsInit.PART_6_NAME)
                            .leftClickHotbar(
                                    MIH_PUNCH.get(),
                                    MIH_BARRAGE.get()
                                    //MIH_LUNGE_STRIKE.get()
                            )
                            .rightClickHotbar(
                                    MIH_BLOCK.get(),
                                    MIH_DASH.get(),
                                    MIH_TIME_ACCELERATION.get()
                            )
                            .defaultStats(StandStats.class, new StandStats.Builder()
                                    .power(13.0)// b
                                    .speed(20.0) // a+
                                    .range(8, 10)
                                    .durability(14.0)
                                    .precision(10.0)
                                    .randomWeight(0.1)
                            )
                            .addSummonShout(InitSounds.PUCCI_MIH)
                            .addOst(InitSounds.MIH_OST)
                            .build(),

                    InitEntities.ENTITIES,
                    () -> new StandEntityType<MihEntity>(MihEntity::new, 0.65F, 1.75F)
                            .summonSound(InitSounds.MIH_SUMMON)
                            .unsummonSound(InitSounds.MIH_UNSUMMON))
                    .withDefaultStandAttributes();
}