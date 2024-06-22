package com.weever.rotp_mih.effects;

import com.github.standobyte.jojo.capability.entity.LivingUtilCapProvider;
import com.github.standobyte.jojo.potion.IApplicableEffect;
import com.github.standobyte.jojo.potion.UncurableEffect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.EffectType;
import net.minecraftforge.common.ForgeMod;

public class AccelerationEffect extends UncurableEffect implements IApplicableEffect {
    public AccelerationEffect(int color) {
        super(EffectType.HARMFUL, color);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,"fdc4fde7-8fea-4301-a899-7fcd94eea9a4" , 0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL)
        	.addAttributeModifier(ForgeMod.SWIM_SPEED.get(), "c73d6d8c-6e4a-428d-ae60-b9e1620fcc07", 0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
    
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
	
	public boolean isApplicable(LivingEntity entity) {
        return true;
    }
}
