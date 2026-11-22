package com.electrifiedded.oreganic;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public final class MachineRecipes {
    private MachineRecipes() {}

    /** Mineral Extractor: organic ore block to vanilla ore block */
    public static ItemStack extract(ItemStack in) {
        if (in.isEmpty()) return ItemStack.EMPTY;
        for (int i = 0; i < OreganicMod.NAMES.length; i++) {
            if (in.getItem() == Item.getItemFromBlock(OreganicMod.ORGANIC[i])) return new ItemStack(OreganicMod.ORES[i]);
        }
        return ItemStack.EMPTY;
    }

    public static boolean isOrganicBlock(ItemStack s) {
        return !extract(s).isEmpty();
    }

    /** Organic Infuser: (pumpkin or melon seeds) + vanilla ore block to ore seeds */
    public static ItemStack infuse(ItemStack plant, ItemStack mineral) {
        if (!isPlant(plant)) return ItemStack.EMPTY;
        for (int i = 0; i < OreganicMod.NAMES.length; i++) {
            if (mineral.getItem() == Item.getItemFromBlock(OreganicMod.ORES[i])) return new ItemStack(OreganicMod.SEEDS[i]);
        }
        return ItemStack.EMPTY;
    }

    public static boolean isPlant(ItemStack s) {
        return s.getItem() == Items.PUMPKIN_SEEDS || s.getItem() == Items.MELON_SEEDS;
    }

    public static boolean isMineral(ItemStack s) {
        for (int i = 0; i < OreganicMod.NAMES.length; i++) {
            if (s.getItem() == Item.getItemFromBlock(OreganicMod.ORES[i])) return true;
        }
        return false;
    }

    /** infuser power: lava bucket 204, blaze powder 32 */
    public static int infuserPower(ItemStack s) {
        if (s.getItem() == Items.LAVA_BUCKET) return 204;
        if (s.getItem() == Items.BLAZE_POWDER) return 32;
        return 0;
    }

    /** extractor fuel: gravel 800 (as in the original) or any vanilla fuel */
    public static int extractorBurn(ItemStack s) {
        if (s.isEmpty()) return 0;
        if (s.getItem() == Item.getItemFromBlock(Blocks.GRAVEL)) return 800;
        return TileEntityFurnace.getItemBurnTime(s);
    }
}
