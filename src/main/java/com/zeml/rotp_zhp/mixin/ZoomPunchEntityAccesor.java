package com.zeml.rotp_zhp.mixin;

import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.ZoomPunchEntity;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(value = ZoomPunchEntity.class, remap = false)
public interface ZoomPunchEntityAccesor {

    @Accessor("speed")
    float getSpeed();

    @Accessor("userPower")
    Optional<INonStandPower> getUserPower();

    @Accessor("hamon")
    Optional<HamonData> getHamon();

    @Accessor("spendHamonStability")
    boolean getSpendHamonStability();

    @Accessor("hamonDamage")
    float getHamonDamage();

    @Accessor("hamonDamageCost")
    float getHamonDamageCost();

    @Accessor("gaveHamonPointsForBaseHit")
    boolean getGaveHamonPointsForBaseHit();

    @Accessor("baseHitPoints")
    float getBaseHitPoints();

}
