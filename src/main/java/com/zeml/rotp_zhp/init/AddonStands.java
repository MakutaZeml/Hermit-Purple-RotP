package com.zeml.rotp_zhp.init;

import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.power.stand.EntityStandRegistryObject.EntityStandSupplier;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;

public class AddonStands {

    public static final EntityStandSupplier<EntityStandType<StandStats>, StandEntityType<HermitPurpleEntity>>
            HERMITOPURLE = new EntityStandSupplier<>(InitStands.STAND_HERMITO_PURPLE);
}