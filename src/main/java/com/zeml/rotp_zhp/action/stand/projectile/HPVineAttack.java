package com.zeml.rotp_zhp.action.stand.projectile;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.IHasStandPunch;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.action.stand.StandEntityHeavyAttack;
import com.github.standobyte.jojo.action.stand.punch.StandBlockPunch;
import com.github.standobyte.jojo.action.stand.punch.StandEntityPunch;
import com.github.standobyte.jojo.capability.entity.hamonutil.EntityHamonChargeCapProvider;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.BaseHamonSkill;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.general.MathUtil;
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineEntity;
import com.zeml.rotp_zhp.init.InitSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Optional;

public class HPVineAttack extends StandEntityAction {

    public HPVineAttack(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    public int getStandWindupTicks(IStandPower standPower, StandEntity standEntity) {
        return Math.max(super.getStandWindupTicks(standPower, standEntity) - getStandActionTicks(standPower, standEntity) / 2, 3);
    }


    @Override
    public void onTaskSet(World world, StandEntity standEntity, IStandPower standPower, Phase phase, StandEntityTask task, int ticks) {
        super.onTaskSet(world, standEntity, standPower, phase, task, ticks);
        if (!world.isClientSide()) {
            boolean shift = isShiftVariation();
            int n = shift ? 2: 5;
            for (int i = 0; i < n; i++) {
                Vector2f rotOffsets = MathUtil.xRotYRotOffsets(Math.random()*1.5/ (double) n * Math.PI * 2, 10);
                addProjectile(world, standPower, standEntity, rotOffsets.y, rotOffsets.x, shift);
            }
            addProjectile(world, standPower, standEntity, 0, 0, shift);
        }
        INonStandPower.getNonStandPowerOptional(standPower.getUser()).ifPresent(ipower->{
            Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
            if(hamonOp.isPresent()){
                HamonData hamon = hamonOp.get();
                if ((hamon.isSkillLearned(ModHamonSkills.THROWABLES_INFUSION.get())||(hamon.isSkillLearned(ModHamonSkills.SCARLET_OVERDRIVE.get()))) && ipower.getEnergy()>0){
                    standEntity.playSound(InitSounds.USER_OVER.get(),1F,1);
                }
            }
        });
    }



    private void addProjectile(World world, IStandPower standPower, StandEntity standEntity, float yRotDelta, float xRotDelta, boolean shift) {
        HPVineEntity vine = new HPVineEntity(world, standEntity, yRotDelta, xRotDelta, shift);
        if (!shift) {
            vine.addKnockback(standEntity.guardCounter());
        }
        vine.setLifeSpan(getStandActionTicks(standPower, standEntity));
        vine.isCharged(false);
        vine.isSpread(false);
        vine.isScarlet(false,0);
        standEntity.addProjectile(vine);
        INonStandPower.getNonStandPowerOptional(standPower.getUser()).ifPresent(ipower->{
            Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
            if(hamonOp.isPresent()){
                HamonData hamon = hamonOp.get();

                if(hamon.isSkillLearned(ModHamonSkills.HAMON_SPREAD.get())&& ipower.getEnergy()>0){
                   vine.isSpread(true);
                }
                if (hamon.isSkillLearned(ModHamonSkills.THROWABLES_INFUSION.get()) && ipower.getEnergy()>0){
                    float cost = 30+ (float) hamon.getHamonStrengthLevel()/2;
                    hamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.STRENGTH,cost);
                    hamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.CONTROL,cost);
                    float hamonEfficiency = hamon.getActionEfficiency(cost, true);
                    standEntity.playSound(ModSounds.HAMON_CONCENTRATION.get(),0.5F,1);
                    vine.isCharged(true);
                    vine.setHamonDamageOnHit(hamonEfficiency, cost, ipower.getEnergy() <= 0);
                    vine.setBaseUsageStatPoints(Math.min(30, ipower.getEnergy()) * hamonEfficiency);

                }
                if(hamon.isSkillLearned(ModHamonSkills.HAMON_SHOCK.get())&& ipower.getEnergy()>0){
                    vine.hamonStuned(true);
                }
                if(hamon.isSkillLearned(ModHamonSkills.SCARLET_OVERDRIVE.get())){
                    vine.isScarlet(true,hamon.getHamonStrengthLevel());
                }

            }
        });
    }


    @Override
    protected boolean standKeepsTarget(ActionTarget target) {
        return true;
    }



}
