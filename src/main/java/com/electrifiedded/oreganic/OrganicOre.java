package com.electrifiedded.oreganic;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class OrganicOre extends Block {
    public OrganicOre() {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);
        setHardness(3.0F);
        setResistance(5.0F);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(OreganicMod.TAB);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return Math.min(9, 1 + random.nextInt(1 + fortune));
    }
}
