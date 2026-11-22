package com.electrifiedded.oreganic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class MachineContainer extends Container {
    public final TileMachine tile;
    private final int machineSlots;
    private final int outputSlot;
    private int lastProgress, lastEnergy, lastMax;

    public MachineContainer(InventoryPlayer pl, TileMachine tile) {
        this.tile = tile;
        this.machineSlots = tile.inv.getSlots();
        if (tile instanceof TileInfuser) {
            outputSlot = 3;
            addSlot(tile, 0, 48, 15);
            addSlot(tile, 1, 48, 54);
            addSlot(tile, 2, 17, 54);
            addSlot(tile, 3, 104, 34);
        } else {
            outputSlot = 2;
            addSlot(tile, 0, 48, 34);
            addSlot(tile, 1, 17, 54);
            addSlot(tile, 2, 104, 34);
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) addSlotToContainer(new Slot(pl, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        }
        for (int i = 0; i < 9; i++) addSlotToContainer(new Slot(pl, i, 8 + i * 18, 142));
    }

    private void addSlot(final TileMachine t, final int idx, int x, int y) {
        addSlotToContainer(new SlotItemHandler(t.inv, idx, x, y) {
            @Override
            public boolean isItemValid(ItemStack s) {
                return idx != outputSlot && t.isValid(idx, s);
            }
        });
    }

    @Override
    public void addListener(IContainerListener l) {
        super.addListener(l);
        l.sendWindowProperty(this, 0, tile.progress);
        l.sendWindowProperty(this, 1, tile.energy);
        l.sendWindowProperty(this, 2, tile.energyMax);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener l : listeners) {
            if (lastProgress != tile.progress) l.sendWindowProperty(this, 0, tile.progress);
            if (lastEnergy != tile.energy) l.sendWindowProperty(this, 1, tile.energy);
            if (lastMax != tile.energyMax) l.sendWindowProperty(this, 2, tile.energyMax);
        }
        lastProgress = tile.progress;
        lastEnergy = tile.energy;
        lastMax = tile.energyMax;
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) tile.progress = data;
        if (id == 1) tile.energy = data;
        if (id == 2) tile.energyMax = data;
    }

    @Override
    public boolean canInteractWith(EntityPlayer p) {
        return tile.isUsableByPlayer(p);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack st = slot.getStack();
            copy = st.copy();
            if (index < machineSlots) {
                if (!mergeItemStack(st, machineSlots, machineSlots + 36, true)) return ItemStack.EMPTY;
            } else {
                boolean moved = false;
                for (int i = 0; i < machineSlots && !moved; i++) {
                    if (i != outputSlot && tile.isValid(i, st)) moved = mergeItemStack(st, i, i + 1, false);
                }
                if (!moved) {
                    if (index < machineSlots + 27) {
                        if (!mergeItemStack(st, machineSlots + 27, machineSlots + 36, false)) return ItemStack.EMPTY;
                    } else if (!mergeItemStack(st, machineSlots, machineSlots + 27, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            if (st.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
            if (st.getCount() == copy.getCount()) return ItemStack.EMPTY;
            slot.onTake(p, st);
        }
        return copy;
    }
}
