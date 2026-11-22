package com.electrifiedded.oreganic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/** shared base: item handler, sided access, NBT and the active block state */
public abstract class TileMachine extends TileEntity implements ITickable {
    public final ItemStackHandler inv;
    public int progress;
    /** infuser: power, extractor: burn time left */
    public int energy;
    /** extractor: burn time of the current fuel */
    public int energyMax;

    private final int outputSlot;
    private final int fuelSlot;
    private final int[][] faces; // DOWN, UP, sides
    private final IItemHandler[] views = new IItemHandler[3];

    protected TileMachine(int slots, int outputSlot, int fuelSlot, int[] bottom, int[] top, int[] side) {
        this.outputSlot = outputSlot;
        this.fuelSlot = fuelSlot;
        this.faces = new int[][] {bottom, top, side};
        this.inv = new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        };
    }

    public abstract boolean isValid(int slot, ItemStack stack);

    public abstract boolean isActive();

    public abstract String getName();

    public abstract int cookTime();

    public boolean isUsableByPlayer(EntityPlayer p) {
        return world.getTileEntity(pos) == this
            && p.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    public int progressScaled(int px) {
        return progress * px / cookTime();
    }

    @Override
    public boolean shouldRefresh(World w, BlockPos p, IBlockState o, IBlockState n) {
        return o.getBlock() != n.getBlock();
    }

    protected void setActive(boolean on) {
        IBlockState s = world.getBlockState(pos);
        if (s.getBlock() instanceof MachineBlock && s.getValue(MachineBlock.ACTIVE) != on) {
            world.setBlockState(pos, s.withProperty(MachineBlock.ACTIVE, on), 3);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("Items", inv.serializeNBT());
        nbt.setShort("CookTime", (short) progress);
        nbt.setShort("Energy", (short) energy);
        nbt.setShort("EnergyMax", (short) energyMax);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        inv.deserializeNBT(nbt.getCompoundTag("Items"));
        progress = nbt.getShort("CookTime");
        energy = nbt.getShort("Energy");
        energyMax = nbt.getShort("EnergyMax");
    }

    private IItemHandler view(EnumFacing f) {
        int i = f == EnumFacing.DOWN ? 0 : f == EnumFacing.UP ? 1 : 2;
        if (views[i] == null) {
            final int[] slots = faces[i];
            views[i] = new IItemHandler() {
                public int getSlots() { return slots.length; }
                public ItemStack getStackInSlot(int s) { return inv.getStackInSlot(slots[s]); }
                public ItemStack insertItem(int s, ItemStack st, boolean sim) {
                    int real = slots[s];
                    if (real == outputSlot || !isValid(real, st)) return st;
                    return inv.insertItem(real, st, sim);
                }
                public ItemStack extractItem(int s, int amt, boolean sim) {
                    int real = slots[s];
                    ItemStack cur = inv.getStackInSlot(real);
                    if (real == outputSlot || (real == fuelSlot && cur.getItem() == Items.BUCKET)) {
                        return inv.extractItem(real, amt, sim);
                    }
                    return ItemStack.EMPTY;
                }
                public int getSlotLimit(int s) { return 64; }
            };
        }
        return views[i];
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> c, @Nullable EnumFacing f) {
        return c == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(c, f);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> c, @Nullable EnumFacing f) {
        if (c == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) (f == null ? inv : view(f));
        }
        return super.getCapability(c, f);
    }
}
