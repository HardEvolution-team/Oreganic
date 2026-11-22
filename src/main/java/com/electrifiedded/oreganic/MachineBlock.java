package com.electrifiedded.oreganic;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class MachineBlock extends BlockHorizontal {
    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    public final boolean infuser;

    public MachineBlock(boolean infuser) {
        super(Material.IRON);
        this.infuser = infuser;
        setSoundType(SoundType.METAL);
        setHardness(5.0F);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(OreganicMod.TAB);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVE, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ACTIVE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
            .withProperty(ACTIVE, (meta & 4) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState s) {
        return s.getValue(FACING).getHorizontalIndex() | (s.getValue(ACTIVE) ? 4 : 0);
    }

    @Override
    public IBlockState withRotation(IBlockState s, Rotation r) {
        return s.withProperty(FACING, r.rotate(s.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState s, Mirror m) {
        return s.withRotation(m.toRotation(s.getValue(FACING)));
    }

    @Override
    public IBlockState getStateForPlacement(World w, BlockPos p, EnumFacing f, float x, float y, float z, int meta,
                                            EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public int getLightValue(IBlockState s) {
        return s.getValue(ACTIVE) ? 8 : 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState s) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World w, IBlockState s) {
        return infuser ? new TileInfuser() : new TileExtractor();
    }

    @Override
    public boolean onBlockActivated(World w, BlockPos p, IBlockState s, EntityPlayer pl, EnumHand h, EnumFacing f,
                                    float x, float y, float z) {
        if (!w.isRemote && !pl.isSneaking() && w.getTileEntity(p) instanceof TileMachine) {
            pl.openGui(OreganicMod.INSTANCE, infuser ? 1 : 2, w, p.getX(), p.getY(), p.getZ());
        }
        return !pl.isSneaking();
    }

    @Override
    public void breakBlock(World w, BlockPos p, IBlockState s) {
        TileEntity te = w.getTileEntity(p);
        if (te instanceof TileMachine) {
            IItemHandler inv = ((TileMachine) te).inv;
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack st = inv.getStackInSlot(i);
                if (!st.isEmpty()) InventoryHelper.spawnItemStack(w, p.getX(), p.getY(), p.getZ(), st);
            }
        }
        super.breakBlock(w, p, s);
    }
}
