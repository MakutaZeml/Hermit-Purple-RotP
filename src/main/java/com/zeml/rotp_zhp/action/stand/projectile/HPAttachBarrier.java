package com.zeml.rotp_zhp.action.stand.projectile;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class HPAttachBarrier extends StandEntityAction {
    public HPAttachBarrier(StandEntityAction.Builder builder) {
        super(builder);
    }
    @Override
    protected ActionConditionResult checkStandConditions(StandEntity stand, IStandPower power, ActionTarget target) {
        AtomicBoolean string = new AtomicBoolean(false);
        INonStandPower.getNonStandPowerOptional(power.getUser()).ifPresent(ipower->{
            Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
            if(hamonOp.isPresent()){
                HamonData hamon = hamonOp.get();
                string.set(hamon.isSkillLearned(ModHamonSkills.ROPE_TRAP.get()));
            }
        });

        if(string.get()){
            if (stand instanceof HermitPurpleEntity) {
                HermitPurpleEntity spoderman = (HermitPurpleEntity) stand;
                if (!spoderman.canPlaceBarrier()) {
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
        return 5;
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

}
