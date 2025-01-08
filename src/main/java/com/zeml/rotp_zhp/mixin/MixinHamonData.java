package com.zeml.rotp_zhp.mixin;


import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.init.ModItems;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonActions;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


@Mixin(value = HamonData.class, remap = false)
public abstract class MixinHamonData extends TypeSpecificData {

    @Shadow
    private boolean playedEnergySound;
    @Shadow
    private int noEnergyDecayTicks;

    public MixinHamonData() {
    }

    /**
     * @author Makuta Zeml
     * @reason Implement hamon breathing
     */
    @Overwrite
    public float tickEnergy() {
        LivingEntity user = this.power.getUser();
        AtomicReference<Float> energy = new AtomicReference<>(0.0F);
        AtomicInteger ticksMaskWithNoHamonBreath = new AtomicInteger(((HamonDataAccesor)((HamonData)(Object)this)).getTicksMaskWithNoHamonBreath());
        IStandPower.getStandPowerOptional(user).ifPresent((standPower) -> {
            if (( standPower.getHeldAction() == InitStands.HP_BREATH.get()) &&  user.getAirSupply() >= user.getMaxAirSupply()) {
                energy.set(this.power.getEnergy() + this.tickHamonBreath(InitStands.HP_BREATH.get()));
            } else if (this.power.getHeldAction() == ModHamonActions.HAMON_BREATH.get() &&  user.getAirSupply() >= user.getMaxAirSupply()) {
                energy.set(this.power.getEnergy() + this.tickHamonBreath(ModHamonActions.HAMON_BREATH.get()));
            } else {
                if (this.hermitPurpleCamera$isUserWearingBreathMask()) {
                    ticksMaskWithNoHamonBreath.getAndIncrement();
                } else {
                    ticksMaskWithNoHamonBreath.set(0);
                }

                if (this.power.getEnergy() <= 0.0F) {
                    this.setHamonProtection(false);
                }

                this.playedEnergySound = false;
                if (this.noEnergyDecayTicks > 0) {
                    --this.noEnergyDecayTicks;
                    energy.set(this.power.getEnergy());
                } else if (JojoModConfig.getCommonConfigInstance(user.level.isClientSide()).hamonEnergyTicksDown.get()) {
                    energy.set(this.power.getEnergy() - 20.0F);
                } else {
                    energy.set(this.power.getEnergy());
                }
            }

        });
        return energy.get();
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

    @Shadow
    public abstract void setHamonProtection(boolean var1);

    @Unique
    private boolean hermitPurpleCamera$isUserWearingBreathMask() {
        ItemStack headItem = this.power.getUser().getItemBySlot(EquipmentSlotType.HEAD);
        return !headItem.isEmpty() && headItem.getItem() == ModItems.BREATH_CONTROL_MASK.get();
    }



}
