package com.zeml.rotp_zhp.init;

import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class InitTags {
    public static final Tags.IOptionalNamedTag<Item> CAMERA = ItemTags.createOptional(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID,"camera"));

    public static void iniTags(){}

}
