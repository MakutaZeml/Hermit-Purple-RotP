package com.zeml.rotp_zhp.command;

import com.github.standobyte.jojo.command.JojoCommandsCommand;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Collection;
import java.util.Map;

public class HermitStructureCommand {
    private static final DynamicCommandExceptionType SINGLE_FAILED_EXCEPTION = new DynamicCommandExceptionType(
            player -> new TranslationTextComponent("commands.hermit_target.failed.single", player));

    public static void register(CommandDispatcher<CommandSource> dispatcher){
        LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("hermit_structure").requires((p_198533_0_) -> {
            return p_198533_0_.hasPermission(0);
        });

        if (false)
            for(Map.Entry<String, Structure<?>> entry : Structure.STRUCTURES_REGISTRY.entrySet()) {
                literalargumentbuilder = literalargumentbuilder.then(Commands.literal(entry.getKey()).executes((ctx) -> {
                    return setStructureTarget(ctx.getSource(), ImmutableList.of(ctx.getSource().getEntityOrException()) ,entry.getValue());
                }));
            }
        else {
            for (Structure<?> structureFeature : net.minecraftforge.registries.ForgeRegistries.STRUCTURE_FEATURES) {
                String name = structureFeature.getRegistryName().toString().replace("minecraft:", "");
                literalargumentbuilder = literalargumentbuilder.then(Commands.literal(name)
                        .executes(ctx -> setStructureTarget(ctx.getSource(), ImmutableList.of(ctx.getSource().getEntityOrException()),structureFeature)));
            }}



        JojoCommandsCommand.addCommand("hermit_structure");
        dispatcher.register(literalargumentbuilder);
    }
    private static int setStructureTarget(CommandSource source, Collection<? extends Entity> targets,Structure<?> structure)throws CommandSyntaxException {
        int success = 0;
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            success = IStandPower.getStandPowerOptional(livingEntity).map(power -> {
                if (power.getStandManifestation() instanceof HermitPurpleEntity) {
                    HermitPurpleEntity hermitPurple = (HermitPurpleEntity) power.getStandManifestation();
                    hermitPurple.setTarget(structure.getRegistryName().toString());
                    hermitPurple.setMode(3);
                    return 1;
                }
                return 0;
            }).orElse(0);
        }
        if (success == 0) {
            throw SINGLE_FAILED_EXCEPTION.create(targets.iterator().next().getName());
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.hermit.player.success", structure.getFeatureName()), true);
        }
        return success;
    }

}
