package com.zeml.rotp_zhp.mixin.actions.interfaces;

import com.github.standobyte.jojo.action.non_stand.HamonSunlightYellowOverdriveBarrage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = HamonSunlightYellowOverdriveBarrage.Instance.class)
public interface HamonSunlightYellowOverdriveBarrageAccessor {

    @Accessor("MAX_BARRAGE_DURATION")
    int getMAX_BARRAGE_DURATION();

    @Accessor("finishingPunch")
    boolean getFinishingPunch();
}
