package com.zeml.rotp_zhp.power.impl.stand.type;

import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.item.GlovesItem;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.github.standobyte.jojo.util.mc.OstSoundList;
import com.zeml.rotp_zhp.HermitConfig;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.init.InitSounds;
import com.zeml.rotp_zhp.init.InitStands;
import com.zeml.rotp_zhp.network.ModNetwork;
import com.zeml.rotp_zhp.network.packets.CanLeapPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HermitPurpleStandType<T extends StandStats> extends EntityStandType<T> {
    private boolean leapUnlocked = true;


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


            if(power.getResolveLevel() > 3 && power.getStandManifestation() instanceof HermitPurpleEntity && HermitConfig.getCommonConfigInstance(false).hermitHamon.get()){
                if(user.getMainHandItem().isEmpty() || MCUtil.isHandFree(user, Hand.MAIN_HAND)){
                    user.addEffect(new EffectInstance(ModStatusEffects.INTEGRATED_STAND.get(),10,0,false,false,false));
                }
            }

            INonStandPower.getNonStandPowerOptional(user).ifPresent(ipower->{
                Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                if(hamonOp.isPresent()) {
                    HamonData hamon = hamonOp.get();
                    this.setLeapUnlocked(hamon.isSkillLearned(ModHamonSkills.JUMP.get()));
                    if(user instanceof ServerPlayerEntity){
                        ModNetwork.sendToClient(new CanLeapPacket(user.getId(), hamon.isSkillLearned(ModHamonSkills.JUMP.get())),(ServerPlayerEntity) user);
                    }
                    if(hamon.isSkillLearned(ModHamonSkills.SUNLIGHT_YELLOW_OVERDRIVE.get())){
                        power.unlockAction(InitStands.HP_GRAB_OVERDRIVE.get());
                    }
                    if(hamon.isSkillLearned(ModHamonSkills.ROPE_TRAP.get())){
                        power.unlockAction(InitStands.HP_BARRIER.get());
                        power.unlockAction(InitStands.HP_UNBARRIER.get());
                    }
                    if(hamon.isSkillLearned(ModHamonSkills.HEALING_TOUCH.get())){
                        power.unlockAction(InitStands.HP_HEAL_VINE.get());
                    }
                    if(hamon.isSkillLearned(ModHamonSkills.SCARLET_OVERDRIVE.get())){
                        power.unlockAction(InitStands.HP_GRAB_SCARLET.get());
                    }
                }
            });

        }
    }





    /* THIS CAUSES CRASH
    public OstSoundList getOst(@Nullable LivingEntity user) {
        String standoSkin =
        IStandPower.getStandPowerOptional(user).map(power -> power.getStandInstance()
                .flatMap(StandSkinsManager.getInstance()::getStandSkin).map(standSkin -> standSkin.resLoc).toString()).orElse("");
        if(standoSkin.contains("spider_man")){
            return InitSounds.SPIDER_OST;
        }
        if(standoSkin.contains("jonathan")){
            return InitSounds.JONATHAN_OST;
        }

        return super.getOst(user);
    }


     */

    @Override
    public boolean canLeap() {
        return leapUnlocked;
    }

    public void setLeapUnlocked(boolean leapUnlocked) {
        this.leapUnlocked = leapUnlocked;
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
