package com.zeml.rotp_zhp.mixin;

import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.TypeSpecificData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TypeSpecificData.class, remap = false)
public interface TypeSpecificDataAccesor {

    @Accessor("power")
    INonStandPower getIPower();
}
