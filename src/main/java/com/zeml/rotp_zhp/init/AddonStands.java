package com.zeml.rotp_zhp.init;

import com.zeml.rotp_zhp.entity.stand.stands.EmperorEntity;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.power.stand.EntityStandRegistryObject.EntityStandSupplier;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;
import com.zeml.rotp_zhp.power.impl.stand.type.HermitPurpleStandType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;

public class AddonStands {

    public static final EntityStandSupplier<HermitPurpleStandType<StandStats>, StandEntityType<HermitPurpleEntity>>
            HERMITOPURLE = new EntityStandSupplier<>(InitStands.STAND_HERMITO_PURPLE);

    public static final EntityStandSupplier<EntityStandType<StandStats>, StandEntityType<EmperorEntity>>
            EMPEROR_STAND = new EntityStandSupplier<>(InitStands.STAND_EMPEROR);
}