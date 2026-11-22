package com.electrifiedded.oreganic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Stem growing an organic ore block as its fruit (reimplementation of the 1.7.10 behaviour). */
public class PlantStem extends BlockStem {
    public Item seeds;

    public PlantStem(Block fruit) {
        super(fruit);
        setCreativeTab(null);
    }

    @Override
    protected Item getSeedItem() {
        return seeds == null ? Items.AIR : seeds;
    }

    @Override
    public ItemStack getItem(World w, BlockPos pos, IBlockState state) {
        return seeds == null ? ItemStack.EMPTY : new ItemStack(seeds);
    }
}
