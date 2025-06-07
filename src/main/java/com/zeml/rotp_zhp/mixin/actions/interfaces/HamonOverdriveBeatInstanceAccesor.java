package com.zeml.rotp_zhp.mixin.actions.interfaces;

import com.github.standobyte.jojo.action.non_stand.HamonOverdriveBeat;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = HamonOverdriveBeat.Instance.class, remap = false)
public interface HamonOverdriveBeatInstanceAccesor {

    @Accessor("userHamon")
    HamonData getUserHamon();


}
