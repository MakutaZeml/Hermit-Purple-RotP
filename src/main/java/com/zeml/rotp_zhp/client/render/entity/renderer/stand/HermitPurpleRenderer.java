package com.zeml.rotp_zhp.client.render.entity.renderer.stand;

import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.client.render.entity.model.stand.HermitPurpleModel;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.github.standobyte.jojo.client.render.entity.renderer.stand.StandEntityRenderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public class HermitPurpleRenderer  extends StandEntityRenderer<HermitPurpleEntity, HermitPurpleModel> {

    public HermitPurpleRenderer(EntityRendererManager renderManager) {
        super(renderManager, new HermitPurpleModel(), new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "textures/entity/stand/hermito_purple_stand.png"), 0);
    }

}
