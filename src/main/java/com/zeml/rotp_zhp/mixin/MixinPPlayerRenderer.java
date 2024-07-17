package com.zeml.rotp_zhp.mixin;

import com.zeml.rotp_zhp.client.render.entity.renderer.HermitoThorns;
import com.zeml.rotp_zhp.client.render.entity.renderer.HermitoUserLayer;
import com.zeml.rotp_zhp.epicfight.HermitPurpleEpicFightLayer;
import com.zeml.rotp_zhp.epicfight.HermitoThornsEpicFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;

@Mixin(value = PPlayerRenderer.class)
public class MixinPPlayerRenderer {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        PPlayerRenderer renderer = (PPlayerRenderer)((Object) this);
        renderer.addPatchedLayer(HermitoUserLayer.class, new HermitPurpleEpicFightLayer<>());
        renderer.addPatchedLayer(HermitoThorns.class,new HermitoThornsEpicFight<>());
    }



}
