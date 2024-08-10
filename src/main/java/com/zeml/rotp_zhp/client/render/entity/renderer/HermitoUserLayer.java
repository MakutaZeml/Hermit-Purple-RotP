package com.zeml.rotp_zhp.client.render.entity.renderer;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.playeranim.PlayerAnimationHandler;
import com.github.standobyte.jojo.client.render.entity.layerrenderer.GlovesLayer;
import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class HermitoUserLayer<T extends LivingEntity, M extends BipedModel<T>> extends LayerRenderer<T, M> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RotpHermitPurpleAddon.MOD_ID,
            "textures/entity/stand/hermito_purple_slim.png");
    private static final ResourceLocation TEXTURE_SLIM = new ResourceLocation(RotpHermitPurpleAddon.MOD_ID,
            "textures/entity/stand/hermito_purple.png");
    private static final Map<PlayerRenderer, HermitoUserLayer<AbstractClientPlayerEntity, BipedModel<AbstractClientPlayerEntity>>> RENDERER_LAYERS = new HashMap<>();
    private final M glovesModel;
    private final boolean slim;
    private boolean playerAnimHandled = false;

    public HermitoUserLayer(IEntityRenderer<T, M> renderer, M hermitoModel, boolean slim) {
        super(renderer);
        if (renderer instanceof PlayerRenderer) {
            RENDERER_LAYERS.put((PlayerRenderer) renderer, (HermitoUserLayer<AbstractClientPlayerEntity, BipedModel<AbstractClientPlayerEntity>>) this);
        }
        this.glovesModel = hermitoModel;
        this.slim = slim;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ticks, float yRot, float xRot) {

        if (!ClientUtil.canSeeStands()) {
            return;
        }

        if (!playerAnimHandled) {
            PlayerAnimationHandler.getPlayerAnimator().onArmorLayerInit(this);
            playerAnimHandled = true;
        }

        IStandPower.getStandPowerOptional(entity).ifPresent((stand) -> {
            StandType<?> hm = InitStands.STAND_HERMITO_PURPLE.getStandType();
            if (stand.getType() == hm && stand.getStandManifestation() instanceof StandEntity) {
                M playerModel = getParentModel();
                glovesModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
                playerModel.copyPropertiesTo(glovesModel);
                glovesModel.setupAnim(entity, limbSwing, limbSwingAmount, ticks, yRot, xRot);
                glovesModel.leftArm.visible = playerModel.leftArm.visible;
                glovesModel.rightArm.visible = playerModel.rightArm.visible;

                ResourceLocation texture =  StandSkinsManager.getInstance().getRemappedResPath(manager -> manager
                        .getStandSkin(stand.getStandInstance().get()), getTexture());
                IVertexBuilder vertexBuilder = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(texture), false, false);
                glovesModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            }
        });
    }

    public static void renderFirstPerson(HandSide side, MatrixStack matrixStack,
                                         IRenderTypeBuffer buffer, int light, AbstractClientPlayerEntity player){
        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        if (renderer instanceof PlayerRenderer) {
            PlayerRenderer playerRenderer = (PlayerRenderer) renderer;
            if (RENDERER_LAYERS.containsKey(playerRenderer)) {
                HermitoUserLayer<?, ?> layer = RENDERER_LAYERS.get(playerRenderer);
                if (layer != null) {
                    layer.renderHandFirstPerson(side, matrixStack,
                            buffer, light, player, playerRenderer);
                }
            }
        }
    }

    private void renderHandFirstPerson(HandSide side, MatrixStack matrixStack,
                                       IRenderTypeBuffer buffer, int light, AbstractClientPlayerEntity player,
                                       PlayerRenderer playerRenderer){
        if (player.isSpectator()) return;
        IStandPower.getStandPowerOptional(player).ifPresent((stand) -> {
            StandType<?> hm = InitStands.STAND_HERMITO_PURPLE.getStandType();
            if (stand.getType() == hm && stand.getStandManifestation() instanceof StandEntity) {
                PlayerModel<AbstractClientPlayerEntity> model = playerRenderer.getModel();
                ClientUtil.setupForFirstPersonRender(model, player);
                ClientUtil.setupForFirstPersonRender((PlayerModel<AbstractClientPlayerEntity>) glovesModel, player);
                ModelRenderer vines = ClientUtil.getArm(model, side);
                ModelRenderer vinesOuter = ClientUtil.getArmOuter(model, side);
                ResourceLocation texture = StandSkinsManager.getInstance().getRemappedResPath(manager -> manager
                        .getStandSkin(stand.getStandInstance().get()), getTexture());
                IVertexBuilder vertexBuilder = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(texture), false, false);
                vines.xRot = 0.0F;
                vines.render(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY);
                vinesOuter.xRot = 0.0F;
                vinesOuter.render(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY);
            }
        });
    }


    private ResourceLocation getTexture() {
        return slim ? TEXTURE : TEXTURE_SLIM;
    }
}
