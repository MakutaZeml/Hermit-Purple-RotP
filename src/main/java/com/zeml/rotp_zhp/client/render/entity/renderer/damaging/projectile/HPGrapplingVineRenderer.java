package com.zeml.rotp_zhp.client.render.entity.renderer.damaging.projectile;

import com.zeml.rotp_zhp.client.render.entity.model.projectile.HPVineModel;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPGrapplingVineEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class HPGrapplingVineRenderer extends HPVineAbstractRenderer<HPGrapplingVineEntity> {

    public HPGrapplingVineRenderer(EntityRendererManager renderManager) {
        super(renderManager, new HPVineModel<HPGrapplingVineEntity>());
    }
}