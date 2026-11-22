package com.electrifiedded.oreganic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int id, EntityPlayer p, World w, int x, int y, int z) {
        TileEntity te = w.getTileEntity(new BlockPos(x, y, z));
        return te instanceof TileMachine ? new MachineContainer(p.inventory, (TileMachine) te) : null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer p, World w, int x, int y, int z) {
        TileEntity te = w.getTileEntity(new BlockPos(x, y, z));
        return te instanceof TileMachine ? new MachineGui(p.inventory, (TileMachine) te) : null;
    }
}
