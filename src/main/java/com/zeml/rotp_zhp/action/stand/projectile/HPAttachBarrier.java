package com.zeml.rotp_zhp.action.stand.projectile;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandManifestation;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class HPAttachBarrier extends StandEntityAction {
    public HPAttachBarrier(StandEntityAction.Builder builder) {
        super(builder);
    }




    @Override
    protected ActionConditionResult checkStandConditions(StandEntity stand, IStandPower power, ActionTarget target) {
        if(INonStandPower.getNonStandPowerOptional(power.getUser()).map(
                ipower -> ipower.getTypeSpecificData(ModPowers.HAMON.get()).map(
                        hamonData -> hamonData.isSkillLearned(ModHamonSkills.ROPE_TRAP.get())).orElse(false)
        ).orElse(false)){
            if (stand instanceof HermitPurpleEntity) {
                HermitPurpleEntity hermitPurple = (HermitPurpleEntity) stand;
                if (!hermitPurple.canPlaceBarrier()) {
                    return conditionMessage("barrier");
                }
                return ActionConditionResult.POSITIVE;
            }
            return ActionConditionResult.NEGATIVE;
        }
        return conditionMessage("strings_learn");

    }


    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (!world.isClientSide()) {
            HermitPurpleEntity hermitPurple = (HermitPurpleEntity) standEntity;
            hermitPurple.attachBarrier(task.getTarget().getBlockPos());
        }
    }

    public static int getMaxBarriersPlaceable(IStandPower power) {
        return 15;
    }

    @Override
    public TranslationTextComponent getTranslatedName(IStandPower power, String key) {
        IStandManifestation stand = power.getStandManifestation();
        int barriers = stand instanceof HermitPurpleEntity ? ((HermitPurpleEntity) stand).getPlacedBarriersCount() : 0;
        return new TranslationTextComponent(key, barriers, getMaxBarriersPlaceable(power));
    }

    @Override
    public TargetRequirement getTargetRequirement() {
        return TargetRequirement.BLOCK;
    }


    @Override
    public boolean isLegalInHud(IStandPower power) {
        INonStandPower.getNonStandPowerOptional(power.getUser())
                .map(ipower->ipower.getTypeSpecificData(ModPowers.HAMON.get())
                        .map(hamonData -> hamonData.isSkillLearned(ModHamonSkills.ROPE_TRAP.get())));
        return super.isLegalInHud(power);
    }
}
