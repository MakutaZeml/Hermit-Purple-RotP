package com.zeml.rotp_zhp.command;

import com.github.standobyte.jojo.command.JojoCommandsCommand;
import com.github.standobyte.jojo.command.argument.StandArgument;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

public class HermitTargetCommand {

    private static final DynamicCommandExceptionType SINGLE_FAILED_EXCEPTION = new DynamicCommandExceptionType(
            player -> new TranslationTextComponent("commands.hermit_target.failed.single", player));


    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("hermit_target").requires(ctx -> ctx.hasPermission(0))
                .then(Commands.literal("player")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> setPlayerTarget(ctx.getSource(), ImmutableList.of(ctx.getSource().getEntityOrException()), EntityArgument.getPlayer(ctx, "player")))
                        )
                )
                .then(Commands.literal("entity")
                        .then(Commands.argument("type", ResourceLocationArgument.id()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                .executes(ctx -> setEntityTarget(ctx.getSource(), ImmutableList.of(ctx.getSource().getEntityOrException()),ResourceLocationArgument.getId(ctx,"type"))))
                )
                .then(Commands.literal("stand_user")
                        .then(Commands.argument("stand", new StandArgument())
                                .executes(ctx -> setStandTarget(ctx.getSource(),ImmutableList.of(ctx.getSource().getEntityOrException()), StandArgument.getStandType(ctx, "stand"))))
                )
                .then(Commands.literal("biomes")
                        .then(Commands.argument("biome", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_BIOMES)
                                .executes(ctx -> setBiomeTarget(ctx.getSource(),ImmutableList.of(ctx.getSource().getEntityOrException()), ResourceLocationArgument.getId(ctx,"biome")))
                        )
                )


        );

        JojoCommandsCommand.addCommand("hermit_target");
    }

    private static int setPlayerTarget(CommandSource source, Collection<? extends Entity> targets, Entity player) throws CommandSyntaxException {
        int success = 0;
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            success = IStandPower.getStandPowerOptional(livingEntity).map(power -> {
                if (power.getStandManifestation() instanceof HermitPurpleEntity) {
                    HermitPurpleEntity hermitPurple = (HermitPurpleEntity) power.getStandManifestation();
                    hermitPurple.setTarget(player.getName().getString());
                    hermitPurple.setMode(1);
                    return 1;
                }
                return 0;
            }).orElse(0);
        }
        if (success == 0) {
            throw SINGLE_FAILED_EXCEPTION.create(targets.iterator().next().getName());
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.hermit.player.success", player.getName()), true);
        }
        return success;
    }

    private static int setEntityTarget(CommandSource source, Collection<? extends Entity> targets, ResourceLocation resourceLocation) throws CommandSyntaxException {
        int success = 0;
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            success = IStandPower.getStandPowerOptional(livingEntity).map(power -> {
                if (power.getStandManifestation() instanceof HermitPurpleEntity) {
                    HermitPurpleEntity hermitPurple = (HermitPurpleEntity) power.getStandManifestation();
                    hermitPurple.setTarget(resourceLocation.toString());
                    hermitPurple.setMode(2);
                    return 1;
                }
                return 0;
            }).orElse(0);
        }
        if (success == 0) {
            throw SINGLE_FAILED_EXCEPTION.create(targets.iterator().next().getName());
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.hermit.player.success", resourceLocation.toString()), true);
        }
        return success;
    }

    private static int setStandTarget(CommandSource source, Collection<? extends Entity> targets,  StandType<?> standType) throws CommandSyntaxException {
        int success = 0;
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            success = IStandPower.getStandPowerOptional(livingEntity).map(power -> {
                if (power.getStandManifestation() instanceof HermitPurpleEntity) {
                    HermitPurpleEntity hermitPurple = (HermitPurpleEntity) power.getStandManifestation();
                    hermitPurple.setTarget(standType.getRegistryName().toString());
                    hermitPurple.setMode(-1);
                    return 1;
                }
                return 0;
            }).orElse(0);
        }
        if (success == 0) {
            throw SINGLE_FAILED_EXCEPTION.create(targets.iterator().next().getName());
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.hermit.player.success", standType.getName()), true);
        }
        return success;
    }

    private static int setBiomeTarget(CommandSource source, Collection<? extends Entity> targets, ResourceLocation resourceLocation) throws CommandSyntaxException {
        int success = 0;
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            success = IStandPower.getStandPowerOptional(livingEntity).map(power -> {
                if (power.getStandManifestation() instanceof HermitPurpleEntity) {
                    HermitPurpleEntity hermitPurple = (HermitPurpleEntity) power.getStandManifestation();
                    hermitPurple.setTarget(resourceLocation.toString());
                    hermitPurple.setMode(4);
                    return 1;
                }
                return 0;
            }).orElse(0);
        }
        if (success == 0) {
            throw SINGLE_FAILED_EXCEPTION.create(targets.iterator().next().getName());
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.hermit.player.success", resourceLocation.toString()), true);
        }
        return success;
    }

}
