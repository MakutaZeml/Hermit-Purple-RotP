package com.zeml.rotp_zhp.epicfight;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.client.render.entity.renderer.HermitoThorns;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import yesman.epicfight.api.client.model.ClientModel;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.layer.PatchedLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class HermitoThornsEpicFight<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends BipedModel<E>> extends PatchedLayer<E, T, M, HermitoThorns<E, M>> {
    public static ResourceLocation HERMIT_LAYER = new  ResourceLocation(RotpHermitPurpleAddon.MOD_ID,"/textures/entity/stand/hermito_torns.png");

    @Override
    public void renderLayer(T t, E e, HermitoThorns<E, M> emHermitoThorns, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, OpenMatrix4f[] openMatrix4fs, float v, float v1, float v2) {
        if (!ClientUtil.canSeeStands()) {
            return;
        }


        IStandPower.getStandPowerOptional(e).ifPresent((stand) -> {
            StandType<?> hm = InitStands.STAND_HERMITO_PURPLE.getStandType();
            if (stand.getType() == hm && stand.getStandManifestation() instanceof StandEntity && stand.getHeldAction() == InitStands.HP_BLOCK.get()) {
                matrixStack.pushPose();
                ClientModel model = ClientModels.LOGICAL_CLIENT.biped;
                HERMIT_LAYER = StandSkinsManager.getInstance().getRemappedResPath(manager -> manager
                        .getStandSkin(stand.getStandInstance().get()), HERMIT_LAYER);
                model.drawAnimatedModel(matrixStack, iRenderTypeBuffer.getBuffer(EpicFightRenderTypes.animatedModel(HERMIT_LAYER)), i, 1.0F, 1.0F, 1.0F, 1.0F, LivingRenderer.getOverlayCoords(e, 0.0F), openMatrix4fs);
                matrixStack.popPose();
            }
        });
    }
}
