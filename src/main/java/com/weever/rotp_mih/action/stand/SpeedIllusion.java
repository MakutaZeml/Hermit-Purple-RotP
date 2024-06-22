package com.weever.rotp_mih.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.action.stand.StandEntityHeavyAttack;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.weever.rotp_mih.entity.stand.stands.MihEntity;
import com.weever.rotp_mih.init.InitStands;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class SpeedIllusion extends StandEntityAction {
    public SpeedIllusion(Builder builder) {
        super(builder);
    }

    @Override
    protected ActionConditionResult checkStandConditions(StandEntity stand, IStandPower power, ActionTarget target) {
        MihEntity MiH = (MihEntity) stand;
        if (power.getStamina() < 75) return ActionConditionResult.NEGATIVE;
        return ActionConditionResult.POSITIVE;
    }

    private static final int RANGE = 7;
    private static final int ATTACK_RANGE = 3;
    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        MihEntity MiH = (MihEntity) standEntity;
        LivingEntity player = userPower.getUser();
        if (!world.isClientSide()) {
            if (!MiH.isTimeAccel()) {
                ((PlayerEntity) player).displayClientMessage(new TranslationTextComponent("rotp_mih.message.action_condition.cant_use_without_timeaccel"), true);
                return;
            }
            LivingEntity entity = standEntity.isManuallyControlled() ? standEntity : player;
            RayTraceResult rayTrace = JojoModUtil.rayTrace(entity.getEyePosition(1.0F), entity.getLookAngle(), RANGE,
                    world, entity, e -> !(e.is(standEntity) || !e.is(player)), 0, 0);
            final Vector3d[] tpPos = {rayTrace.getLocation()};
            if (rayTrace.getType() == RayTraceResult.Type.ENTITY) {
                if (entity instanceof PlayerEntity) {
                    PlayerEntity playerEntity = (PlayerEntity) entity;
                    IStandPower.getStandPowerOptional(playerEntity).ifPresent(power -> {
                        if (power != null && power.isActive()) {
                            for (LivingEntity entity1 : MCUtil.entitiesAround(
                                    LivingEntity.class, playerEntity, ATTACK_RANGE, false,
                                    entity1 -> (!(entity1 instanceof StandEntity) || !player.is(((StandEntity) entity1).getUser())))) {
                                if (tpPos[0].distanceTo(entity1.position()) < tpPos[0].distanceTo(entity1.position())) {
                                    tpPos[0] = entity1.position();
                                }
                                // attack nearest entity
                            }
                        }
                    });
                }
            }
        }
    }
}