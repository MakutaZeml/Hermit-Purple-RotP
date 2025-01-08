package com.zeml.rotp_zhp.mixin;

import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = HamonData.class, remap = false)
public interface HamonDataAccesor {

    @Accessor("ticksMaskWithNoHamonBreath")
    int getTicksMaskWithNoHamonBreath();



}
