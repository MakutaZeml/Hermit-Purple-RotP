package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonUtil;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.BaseHamonSkill;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineGrabEntity;
import net.minecraft.client.gui.social.SocialInteractionsScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HPGrabOverdrive extends StandEntityAction {
    public HPGrabOverdrive(StandEntityAction.Builder builder) {
        super(builder);
    }


    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (!world.isClientSide()) {
            LivingEntity target = getLandedVineStand(standEntity).get().getEntityAttachedTo();
            if(target != null){
                INonStandPower.getNonStandPowerOptional(userPower.getUser()).ifPresent(ipower->{
                    Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                    if(hamonOp.isPresent()){
                        HamonData hamon = hamonOp.get();
                        if(hamon.isSkillLearned(ModHamonSkills.SCARLET_OVERDRIVE.get())&& ipower.getEnergy()>49){
                            float hamomlevel = hamon.getHamonStrengthLevel();
                            hamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.STRENGTH,5);
                            DamageUtil.dealDamageAndSetOnFire(target,
                                    entity -> DamageUtil.dealHamonDamage(entity, 5, userPower.getUser() , null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK_RED.get())),
                                    MathHelper.floor(2 + 8F *  hamomlevel / (float) HamonData.MAX_STAT_LEVEL *2), false);
                        } else if (hamon.isSkillLearned(ModHamonSkills.THROWABLES_INFUSION.get())&&ipower.getEnergy()>49) {
                            hamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.STRENGTH,5);
                            DamageUtil.dealHamonDamage(target, 5,userPower.getUser() , null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK.get()));
                            ipower.consumeEnergy(.2F*ipower.getMaxEnergy());
                        }
                        if(hamon.isSkillLearned(ModHamonSkills.HAMON_SPREAD.get())&&ipower.getEnergy()>0){
                            hamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.CONTROL,5);
                            target.addEffect(new EffectInstance(ModStatusEffects.HAMON_SPREAD.get(),200,6));
                            ipower.consumeEnergy(100);
                        }
                    }
                });
            }

        }
    }


    public static Optional<HPVineGrabEntity> getLandedVineStand(StandEntity stand) {
        List<HPVineGrabEntity> vineLanded = stand.level.getEntitiesOfClass(HPVineGrabEntity.class,
                stand.getBoundingBox().inflate(16), redBind -> stand.is(redBind.getOwner()) && redBind.isAttachedToAnEntity());
        return !vineLanded.isEmpty() ? Optional.of(vineLanded.get(0)) : Optional.empty();
    }
}
