package com.zeml.rotp_zhp.power.impl.stand.type;

import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.zeml.rotp_zhp.init.InitStands;
import com.zeml.rotp_zhp.util.GameplayHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HermitPurpleStandType<T extends StandStats> extends EntityStandType<T> {
    private boolean leapUnlocked;
    private boolean remote;

    @Deprecated
    public HermitPurpleStandType(int color, ITextComponent partName, StandAction[] attacks, StandAction[] abilities, Class<T> statsClass, T defaultStats, @Nullable StandType.StandTypeOptionals additions) {
        super(color, partName, attacks, abilities, statsClass, defaultStats, additions);
    }

    protected HermitPurpleStandType(EntityStandType.AbstractBuilder<?, T> builder) {
        super(builder);
    }


    @Override
    public void tickUser(LivingEntity user, IStandPower power) {
        super.tickUser(user, power);
        if(!user.level.isClientSide){
            INonStandPower.getNonStandPowerOptional(user).ifPresent(ipower->{
                Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                if(hamonOp.isPresent()) {
                    HamonData hamon = hamonOp.get();
                    leapUnlocked = hamon.isSkillLearned(ModHamonSkills.JUMP.get());
                    if(hamon.isSkillLearned(ModHamonSkills.ROPE_TRAP.get())){
                        power.unlockAction(InitStands.HP_BARRIER.get());
                        power.unlockAction(InitStands.HP_UNBARRIER.get());
                    }
                    if(hamon.isSkillLearned(ModHamonSkills.SUNLIGHT_YELLOW_OVERDRIVE.get())){
                        power.unlockAction(InitStands.HP_GRAB_OVERDRIVE.get());
                    }
                    if(hamon.isSkillLearned(ModHamonSkills.HEALING_TOUCH.get())){
                        power.unlockAction(InitStands.HP_HEAL_VINE.get());
                    }
                }
            });

           remote = GameplayHandler.hermitManual.contains(user);
        }
    }




    @Override
    public boolean canBeManuallyControlled() {
        return this.remote;
    }


    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    @Override
    public boolean canLeap() {
        return leapUnlocked;
    }

    public static class Builder<T extends StandStats> extends EntityStandType.AbstractBuilder<Builder<T>, T> {

        @Override
        protected Builder<T> getThis() {
            return this;
        }

        @Override
        public HermitPurpleStandType<T> build() {
            return new HermitPurpleStandType<>(this);
        }

    }

}
