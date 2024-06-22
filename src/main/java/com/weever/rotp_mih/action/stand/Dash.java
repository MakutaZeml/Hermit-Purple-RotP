package com.weever.rotp_mih.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.weever.rotp_mih.entity.stand.stands.MihEntity;
import com.weever.rotp_mih.init.InitParticles;
import com.weever.rotp_mih.utils.ParticleUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
public class Dash extends StandEntityAction {
    public Dash(Builder builder) {
        super(builder);
    }

    private static final double RANGE = 7;

    @Override
    protected ActionConditionResult checkStandConditions(StandEntity stand, IStandPower power, ActionTarget target) {
        MihEntity MiH = (MihEntity) stand;
        if (power.getStamina() < 50) return ActionConditionResult.NEGATIVE;
        return ActionConditionResult.POSITIVE;
    }

    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        MihEntity MiH = (MihEntity) standEntity;
        Vector3d start;
        Vector3d end;
        Vector3d motion = new Vector3d(0, 0.5, 0);
        LivingEntity player = userPower.getUser();
        if (!world.isClientSide()) {
            if (!MiH.isTimeAccel()) {
                ((PlayerEntity) player).displayClientMessage(new TranslationTextComponent("rotp_mih.message.action_condition.cant_use_without_timeaccel"), true);
                return;
            }
            LivingEntity entity = standEntity.isManuallyControlled() ? standEntity : player;
            RayTraceResult rayTrace = JojoModUtil.rayTrace(entity.getEyePosition(1.0F), entity.getLookAngle(), RANGE,
                    world, entity, e -> !(e.is(standEntity) || e.is(player)), 0, 0);
            end = rayTrace.getLocation();
            start = entity.position();
            entity.teleportTo(end.x, end.y, end.z);
            ParticleUtils.createLine(InitParticles.SPARK.get(), player.level, start, end, 5, new Vector3d(0, 0.5, 0)); // doesnt working with serverside
        }
    }
}