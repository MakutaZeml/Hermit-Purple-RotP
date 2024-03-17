package com.zeml.rotp_zhp.init;

import com.zeml.rotp_zhp.RotpHermitPurpleAddon;

import com.zeml.rotp_zhp.entity.damaging.projectile.HPGrapplingVineEntity;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, RotpHermitPurpleAddon.MOD_ID);

    public static final RegistryObject<EntityType<HPVineEntity>> HP_VINE = ENTITIES.register("hp_vine",
            () -> EntityType.Builder.<HPVineEntity>of(HPVineEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).noSummon().noSave().setUpdateInterval(20)
                    .build(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "hp_vine").toString()));

    public static final RegistryObject<EntityType<HPGrapplingVineEntity>> HP_GRAPPLING_VINE = ENTITIES.register("hp_grappling_vine",
            () -> EntityType.Builder.<HPGrapplingVineEntity>of(HPGrapplingVineEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).noSummon().noSave().setUpdateInterval(20)
                    .build(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "hp_grappling_vine").toString()));


}
