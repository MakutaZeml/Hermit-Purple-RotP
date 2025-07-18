package com.zeml.rotp_zhp.client.render.entity.renderer;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.playeranim.PlayerAnimationHandler;
import com.github.standobyte.jojo.client.render.entity.layerrenderer.GlovesLayer;
import com.github.standobyte.jojo.client.render.entity.layerrenderer.IFirstPersonHandLayer;
import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.ModStatusEffects;
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
import net.minecraft.potion.Effects;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class HermitoUserLayer<T extends LivingEntity, M extends BipedModel<T>> extends LayerRenderer<T, M> implements IFirstPersonHandLayer {
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

        if(entity.isInvisible()){
            return;
        }

        if (!playerAnimHandled) {
            PlayerAnimationHandler.getPlayerAnimator().onArmorLayerInit(this);
            playerAnimHandled = true;
        }

        IStandPower.getStandPowerOptional(entity).ifPresent((stand) -> {
            if (stand.getType() == InitStands.STAND_HERMITO_PURPLE.getStandType() && stand.getStandManifestation() instanceof StandEntity) {
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


    @Override
    public void renderHandFirstPerson(HandSide side, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light,
                                      AbstractClientPlayerEntity player, PlayerRenderer playerRenderer) {
        if (!ClientUtil.canSeeStands()) {
            return;
        }

        if(!(player.hasEffect(Effects.INVISIBILITY ) || player.hasEffect(ModStatusEffects.FULL_INVISIBILITY.get()))){
            IStandPower.getStandPowerOptional(player).ifPresent((stand)->{
                StandType<?>  hm = InitStands.STAND_HERMITO_PURPLE.getStandType();
                if(stand.getType() == hm && stand.getStandManifestation()instanceof StandEntity){
                    PlayerModel<AbstractClientPlayerEntity> model = (PlayerModel<AbstractClientPlayerEntity>) glovesModel;
                    ResourceLocation texture = getTexture();
                    texture = StandSkinsManager.getInstance().getRemappedResPath(manager -> manager
                            .getStandSkin(stand.getStandInstance().get()), texture);
                    ClientUtil.setupForFirstPersonRender(model, player);
                    IVertexBuilder vertexBuilder = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(texture), false, false);
                    ModelRenderer glove = ClientUtil.getArm(model, side);
                    ModelRenderer gloveOuter = ClientUtil.getArmOuter(model, side);
                    glove.xRot = 0.0F;
                    glove.render(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY);
                    gloveOuter.xRot = 0.0F;
                    gloveOuter.render(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY);
                }
            });
        }
    }


    private ResourceLocation getTexture() {
        return slim ? TEXTURE : TEXTURE_SLIM;
    }
}
