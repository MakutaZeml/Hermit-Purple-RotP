package com.zeml.rotp_zhp.client.render.entity.renderer.damaging.projectile;

import com.zeml.rotp_zhp.client.render.entity.model.projectile.HPVineModel;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineBarrierEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class HPVineBarrierRrnderer extends HPVineAbstractRenderer<HPVineBarrierEntity>{
    public HPVineBarrierRrnderer(EntityRendererManager renderManager) {
        super(renderManager, new HPVineModel<HPVineBarrierEntity>());
    }

}
