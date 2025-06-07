package com.zeml.rotp_zhp.mixin.actions;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.HamonAction;
import com.github.standobyte.jojo.action.non_stand.HamonOverdrive;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.util.StandHamonDamage;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HamonOverdrive.class, remap = false)
public class MixinHamonOverdrive extends HamonAction {

    public MixinHamonOverdrive(HamonAction.Builder builder) {
        super(builder.withUserPunch());
    }


    @Inject(method = "dealDamage", at = @At("HEAD"), cancellable = true)
    protected void dealDamage(ActionTarget target, LivingEntity targetEntity, float dmgAmount, LivingEntity user, INonStandPower power, HamonData hamon, CallbackInfoReturnable<Boolean> cir) {
        if(IStandPower.getStandPowerOptional(user).map(standPower -> standPower.getStandManifestation() instanceof HermitPurpleEntity).orElse(false)){
            IStandPower.getStandPowerOptional(user).ifPresent(standPower -> {
                cir.setReturnValue(StandHamonDamage.dealHamonDamage(targetEntity, dmgAmount, user, null,standPower,1,1));
            });
            return;
        }
    }



}
