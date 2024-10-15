package com.zeml.rotp_zhp.action.stand;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class HPHelperDox {


    public static LivingEntity HPGeneralObjectives(LivingEntity user, HermitPurpleEntity hermitPurple){
        if(hermitPurple.getMode() == 0){
            return hpObj(user);
        } else if (hermitPurple.getMode() == 1) {
            return hpObjPlayers(user,hermitPurple.getTarget());
        } else if (hermitPurple.getMode()==-1) {
            return hpStandUser(user,hermitPurple.getTarget());
        }
        return hpObj(user,hermitPurple.getTarget());
    }

    public static Structure<?> HPStructure(LivingEntity user, HermitPurpleEntity hermitPurple){
        Structure<?> out = null;
        for(Structure<?> structure: ForgeRegistries.STRUCTURE_FEATURES){
            if(structure.getRegistryName().toString().equals(hermitPurple.getTarget())){
                out = structure;
            }
        }
        return out;
    }

    public static Biome HPBiomes(LivingEntity user, HermitPurpleEntity hermitPurple){
        Biome out = null;
        for(Biome biome: ForgeRegistries.BIOMES){
            if(biome.getRegistryName().toString().equals(hermitPurple.getTarget())){
                out = biome;
            }
        }
        return out;
    }


    public static LivingEntity HPojectives(LivingEntity user){
        World world =user.level;
        List<LivingEntity> lista =  world.getEntitiesOfClass(LivingEntity.class,user.getBoundingBox().inflate(1000), EntityPredicates.ENTITY_STILL_ALIVE).stream()
                .filter(entity -> entity != user)
                .filter(entity -> entity instanceof PlayerEntity || entity.getMaxHealth()>100).collect(Collectors.toList());
        LivingEntity fin = null;
        if(!lista.isEmpty()){
            int n = lista.size();
            int i = MathHelper.floor(n*Math.random());
            fin = lista.get(i);
        }
        return fin;
    }

    private static LivingEntity hpObj(LivingEntity user){
        if(user instanceof ServerPlayerEntity){
            ServerPlayerEntity player = Objects.requireNonNull(user.getServer()).getPlayerList().getPlayer(user.getUUID());
            ServerWorld world= player.getLevel();

            List<Entity> lista =  world.getEntities().filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> entity != user)
                    .filter(entity -> entity instanceof  PlayerEntity || ((LivingEntity)entity).getMaxHealth()>100).collect(Collectors.toList());
            LivingEntity fin = null;
            if(!lista.isEmpty()){
                int n = lista.size();
                int i = MathHelper.floor(n*Math.random());
                fin = (LivingEntity) lista.get(i);
            }
            return fin;

        }
        return HPojectives(user);
    }


    private static LivingEntity HPojectives(LivingEntity user,String ent){
        World world =user.level;
        List<LivingEntity> lista =  world.getEntitiesOfClass(LivingEntity.class,user.getBoundingBox().inflate(1000), EntityPredicates.ENTITY_STILL_ALIVE).stream()
                .filter(entity -> entity.getType().getRegistryName().toString().equals(ent)).collect(Collectors.toList());
        LivingEntity fin = null;
        if(!lista.isEmpty()){
            int n = lista.size();
            int i = MathHelper.floor(n*Math.random());
            fin = lista.get(i);
        }
        return fin;
    }

    private static LivingEntity hpObj(LivingEntity user, String ent){
        if(user instanceof ServerPlayerEntity){
            ServerPlayerEntity player = Objects.requireNonNull(user.getServer()).getPlayerList().getPlayer(user.getUUID());
            ServerWorld world= player.getLevel();

            List<Entity> lista =  world.getEntities().filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> entity.getType().getRegistryName().toString().equals(ent))
                    .collect(Collectors.toList());
            LivingEntity fin = null;
            if(!lista.isEmpty()){
                int n = lista.size();
                int i = MathHelper.floor(n*Math.random());
                fin = (LivingEntity) lista.get(i);
            }
            return fin;

        }
        return HPojectives(user,ent);
    }

    @Nullable
    private static PlayerEntity hpObjPlayers(LivingEntity user, String ent){
        return user.level.players().stream()
                .filter(entity -> entity.getName().getString().equals(ent))
                .collect(Collectors.toList()).stream().findFirst().orElse(null);
    }


    private static PlayerEntity hpStandUser(LivingEntity user,String standType){
        if(user instanceof ServerPlayerEntity){
            ServerPlayerEntity player = Objects.requireNonNull(user.getServer()).getPlayerList().getPlayer(user.getUUID());
            ServerWorld world= player.getLevel();
            return world.players().stream().filter(player1 -> IStandPower.getPlayerStandPower(player1).getType().getRegistryName().toString().equals(standType)).findFirst().orElse(null);
        }
        return null;
    }

    public static BlockPos biomesPos(LivingEntity pos , String biome, ServerWorld world){
        return world.getChunkSource().getGenerator().getBiomeSource().findBiomeHorizontal(pos.blockPosition().getX(), pos.blockPosition().getY(), pos.blockPosition().getZ(), 3000, 8, (p_242102_1_) -> {
            return p_242102_1_.getRegistryName().toString().equals(biome);
        }, world.random, true);
    }


}
