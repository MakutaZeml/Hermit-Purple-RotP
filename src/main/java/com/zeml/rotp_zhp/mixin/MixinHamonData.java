package com.zeml.rotp_zhp.mixin;


import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.init.ModItems;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonActions;
import com.github.standobyte.jojo.power.IPower;
import com.github.standobyte.jojo.power.impl.nonstand.TypeSpecificData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


@Mixin(value = HamonData.class, remap = false)
public abstract class MixinHamonData extends TypeSpecificData {

    public MixinHamonData() {
    }



    @Inject(method = "tickEnergy", at = @At("RETURN"), cancellable = true)
    private void onTickEnergy(CallbackInfoReturnable<Float> cir){
        LivingEntity user = power.getUser();
        IStandPower.getStandPowerOptional(user).ifPresent(standPower->{
            if(standPower.getHeldAction() == InitStands.HP_BREATH.get() && user.getAirSupply() >= user.getMaxAirSupply()){
                cir.cancel();
                cir.setReturnValue(power.getEnergy()+tickHamonBreath(InitStands.HP_BREATH.get()));
            }
        });
    }

    @Shadow
    public abstract CompoundNBT writeNBT();

    @Shadow
    public abstract void readNBT(CompoundNBT var1);

    @Shadow
    public abstract void syncWithUserOnly(ServerPlayerEntity var1);

    @Shadow
    public abstract void syncWithTrackingOrUser(LivingEntity var1, ServerPlayerEntity var2);

    @Shadow
    public abstract float tickHamonBreath(Action<?> var1);

    

}
