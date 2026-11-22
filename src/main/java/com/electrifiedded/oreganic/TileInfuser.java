package com.electrifiedded.oreganic;

import net.minecraft.item.ItemStack;

/** slots: 0 plant seeds, 1 ore, 2 fuel, 3 output */
public class TileInfuser extends TileMachine {
    public static final int MAX_POWER = 2080;
    public static final int SPEED = 200;

    public TileInfuser() {
        super(4, 3, 2, new int[] {3}, new int[] {0, 1}, new int[] {2});
    }

    @Override
    public boolean isValid(int i, ItemStack s) {
        return i == 0 ? MachineRecipes.isPlant(s)
            : i == 1 ? MachineRecipes.isMineral(s) : i == 2 && MachineRecipes.infuserPower(s) > 0;
    }

    @Override
    public boolean isActive() {
        return progress > 0;
    }

    @Override
    public String getName() {
        return "container.organicInfuser";
    }

    @Override
    public int cookTime() {
        return SPEED;
    }

    public int powerScaled(int px) {
        return energy * px / MAX_POWER;
    }

    private boolean canInfuse() {
        ItemStack r = MachineRecipes.infuse(inv.getStackInSlot(0), inv.getStackInSlot(1));
        if (r.isEmpty()) return false;
        ItemStack out = inv.getStackInSlot(3);
        return out.isEmpty() || (out.isItemEqual(r) && out.getCount() + r.getCount() <= out.getMaxStackSize());
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        boolean was = isActive();
        boolean dirty = false;
        if (energy > 0 && progress > 0) energy--;
        ItemStack fuel = inv.getStackInSlot(2);
        int p = MachineRecipes.infuserPower(fuel);
        if (p > 0 && energy < MAX_POWER - p) {
            energy += p;
            dirty = true;
            ItemStack left = fuel.getItem().hasContainerItem(fuel) ? fuel.getItem().getContainerItem(fuel) : ItemStack.EMPTY;
            fuel.shrink(1);
            inv.setStackInSlot(2, fuel.isEmpty() ? left : fuel);
        }
        if (energy > 0 && canInfuse()) {
            if (++progress >= SPEED) {
                progress = 0;
                ItemStack r = MachineRecipes.infuse(inv.getStackInSlot(0), inv.getStackInSlot(1));
                ItemStack out = inv.getStackInSlot(3);
                if (out.isEmpty()) inv.setStackInSlot(3, r.copy()); else out.grow(r.getCount());
                inv.getStackInSlot(0).shrink(1);
                inv.getStackInSlot(1).shrink(1);
                dirty = true;
            }
        } else {
            progress = 0;
        }
        if (was != isActive()) {
            dirty = true;
            setActive(isActive());
        }
        if (dirty) markDirty();
    }
}
