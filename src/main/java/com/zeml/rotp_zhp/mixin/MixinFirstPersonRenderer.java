package com.zeml.rotp_zhp.mixin;

import com.zeml.rotp_zhp.client.render.entity.renderer.HermitoThorns;
import com.zeml.rotp_zhp.client.render.entity.renderer.HermitoUserLayer;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.renderer.FirstPersonRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.layer.EmptyLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

@Mixin(value = FirstPersonRenderer.class)
public class MixinFirstPersonRenderer extends PatchedLivingEntityRenderer<ClientPlayerEntity, LocalPlayerPatch, PlayerModel<ClientPlayerEntity>> {


    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci){
        this.addPatchedLayer(HermitoUserLayer.class, new EmptyLayer<>());
        this.addPatchedLayer(HermitoThorns.class, new EmptyLayer<>());
    }
}
