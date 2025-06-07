package com.zeml.rotp_zhp.init;

import com.github.standobyte.jojo.JojoMod;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;

import com.zeml.rotp_zhp.entity.damaging.projectile.*;
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

    public static final RegistryObject<EntityType<HPVineGrabEntity>> HP_GRAB_ENTITY = ENTITIES.register("hp_grab_vine",
            () -> EntityType.Builder.<HPVineGrabEntity>of(HPVineGrabEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).noSummon().noSave().setUpdateInterval(20)
                    .build(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "hp_grab_vine").toString()));

    public static final RegistryObject<EntityType<HPVineBarrierEntity>> HP_BARRIER = ENTITIES.register("hp_barrier",
            () -> EntityType.Builder.<HPVineBarrierEntity>of(HPVineBarrierEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).noSummon().noSave().setShouldReceiveVelocityUpdates(false).setUpdateInterval(Integer.MAX_VALUE)
                    .build(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "hp_barrier").toString()));

    public static final RegistryObject<EntityType<EmperorBullet>> EMPEROR_BULLET = ENTITIES.register("emperor_bullet",
            () -> EntityType.Builder.<EmperorBullet>of(EmperorBullet::new, EntityClassification.MISC).sized(0.0625F, 0.0625F).clientTrackingRange(4).setUpdateInterval(20).fireImmune()
                    .build(new ResourceLocation(JojoMod.MOD_ID, "emperor_bullet").toString()));

}
