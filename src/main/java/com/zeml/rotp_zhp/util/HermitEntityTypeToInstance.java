package com.zeml.rotp_zhp.util;

import com.github.standobyte.jojo.JojoMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

/* Sorry for cloning this Stando, but it looks like you moved this on Gold Experience Version, so I didn't have a option*/
public class HermitEntityTypeToInstance {

    /*
    private static HermitEntityTypeToInstance instance;

    public static void init(World world) {
        if (instance == null) {
            Iterable<EntityType<?>> entityTypes = ForgeRegistries.ENTITIES.getValues();
            instance = new HermitEntityTypeToInstance(entityTypes, world);
        }
    }

    private final Map<EntityType<?>, Entity> entityMap;
    private HermitEntityTypeToInstance(Iterable<EntityType<?>> entityTypes, World world) {
        entityMap = new HashMap<>();
        for (EntityType<?> type : entityTypes) {
            entityMap.put(type, createInstance(type, world));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> T getEntityInstance(EntityType<T> type, World world) {
        if (instance == null) {
            JojoMod.getLogger().error("An operation with {} entity type needed an Entity instance, but the map for them hasn't been created yet!", type.getRegistryName());
            return null;
        }
        return (T) instance.entityMap.computeIfAbsent(type, t -> createInstance(t, world));
    }

    private static <T extends Entity> T createInstance(EntityType<T> type, World world) {
        T entity = type.create(world);
        if (entity instanceof SlimeEntity) {
            entity.refreshDimensions();
        }
        return entity;
    }

     */

}
