package com.zeml.rotp_zhp.client.render.entity.model.stand;

import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;

public class HermitoUserModel<T extends LivingEntity> extends PlayerModel<T> {

    public HermitoUserModel(float inflate, boolean slim) {
        super(inflate, slim);
    }
}
