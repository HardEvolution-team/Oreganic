package com.electrifiedded.oreganic;

import net.minecraft.item.ItemStack;

/** slots: 0 organic ore, 1 fuel, 2 output */
public class TileExtractor extends TileMachine {
    public static final int SPEED = 200;

    public TileExtractor() {
        super(3, 2, 1, new int[] {2, 1}, new int[] {0}, new int[] {1});
    }

    @Override
    public boolean isValid(int i, ItemStack s) {
        return i == 0 ? MachineRecipes.isOrganicBlock(s) : i == 1 && MachineRecipes.extractorBurn(s) > 0;
    }

    @Override
    public boolean isActive() {
        return energy > 0;
    }

    @Override
    public String getName() {
        return "container.mineralExtractor";
    }

    @Override
    public int cookTime() {
        return SPEED;
    }

    public int burnScaled(int px) {
        return energy * px / (energyMax == 0 ? SPEED : energyMax);
    }

    private boolean canSmelt() {
        ItemStack r = MachineRecipes.extract(inv.getStackInSlot(0));
        if (r.isEmpty()) return false;
        ItemStack out = inv.getStackInSlot(2);
        return out.isEmpty() || (out.isItemEqual(r) && out.getCount() + r.getCount() <= out.getMaxStackSize());
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        boolean was = energy > 0;
        boolean dirty = false;
        if (energy > 0) energy--;
        if (energy == 0 && canSmelt()) {
            ItemStack fuel = inv.getStackInSlot(1);
            energyMax = energy = MachineRecipes.extractorBurn(fuel);
            if (energy > 0) {
                dirty = true;
                ItemStack left = fuel.getItem().hasContainerItem(fuel) ? fuel.getItem().getContainerItem(fuel) : ItemStack.EMPTY;
                fuel.shrink(1);
                inv.setStackInSlot(1, fuel.isEmpty() ? left : fuel);
            }
        }
        if (energy > 0 && canSmelt()) {
            if (++progress >= SPEED) {
                progress = 0;
                ItemStack r = MachineRecipes.extract(inv.getStackInSlot(0));
                ItemStack out = inv.getStackInSlot(2);
                if (out.isEmpty()) inv.setStackInSlot(2, r.copy()); else out.grow(r.getCount());
                inv.getStackInSlot(0).shrink(1);
                dirty = true;
            }
        } else {
            progress = 0;
        }
        if (was != (energy > 0)) {
            dirty = true;
            setActive(energy > 0);
        }
        if (dirty) markDirty();
    }
}
