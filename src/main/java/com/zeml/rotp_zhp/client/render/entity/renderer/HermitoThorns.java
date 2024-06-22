package com.zeml.rotp_zhp.client.render.entity.renderer;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.playeranim.PlayerAnimationHandler;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class HermitoThorns <T extends LivingEntity, M extends PlayerModel<T>> extends LayerRenderer<T, M> {
    private static final Map<PlayerRenderer, HermitoThorns<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>> RENDERER_LAYERS = new HashMap<>();
    private final M thornsModel;
    private final boolean slim;
    private boolean playerAnimHandled = false;

    public HermitoThorns(IEntityRenderer<T, M> renderer, M hermitoModel, boolean slim) {
        super(renderer);
        if(renderer instanceof PlayerRenderer){
            RENDERER_LAYERS.put((PlayerRenderer) renderer,(HermitoThorns<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>) this);
        }
        this.thornsModel = hermitoModel;
        this.slim = slim;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ticks, float yRot, float xRot){
        if (!ClientUtil.canSeeStands()) {
            return;
        }

        if (!playerAnimHandled) {
            PlayerAnimationHandler.getPlayerAnimator().onArmorLayerInit(this);
            playerAnimHandled = true;
        }

        IStandPower.getStandPowerOptional(entity).ifPresent((stand)->{
            StandType<?> hm = InitStands.STAND_HERMITO_PURPLE.getStandType();
            if(stand.getType() == hm && stand.getStandManifestation()instanceof StandEntity && stand.getHeldAction()==InitStands.HP_BLOCK.get()){
                M playerModel = getParentModel();
                thornsModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
                playerModel.copyPropertiesTo(thornsModel);
                thornsModel.setupAnim(entity, limbSwing, limbSwingAmount, ticks, yRot, xRot);

                thornsModel.leftArm.visible = playerModel.leftArm.visible;
                thornsModel.leftSleeve.visible = playerModel.leftArm.visible;
                thornsModel.rightArm.visible = playerModel.rightArm.visible;
                thornsModel.rightSleeve.visible = playerModel.rightArm.visible;
                ResourceLocation texture = new  ResourceLocation(RotpHermitPurpleAddon.MOD_ID,"/textures/entity/stand/hermito_torns"+(slim ? "_slim" : "") + ".png");
                IVertexBuilder vertexBuilder = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(texture), false, false);
                thornsModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            }

        });
    }
}
