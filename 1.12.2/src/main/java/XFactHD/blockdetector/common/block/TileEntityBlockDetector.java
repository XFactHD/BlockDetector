/*  Copyright (C) <2018>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.blockdetector.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

public class TileEntityBlockDetector extends TileEntity implements ITickable
{
    private EnumFacing facing = null;
    private SignalType signalType = SignalType.NORMAL;
    private CheckMode checkMode = CheckMode.BLOCK;
    private boolean blockChanged = false;
    private boolean filterChanged = false;
    private int pulseTicks = 0;
    private IBlockState stateFilter = Blocks.AIR.getDefaultState();
    private IBlockState lastState = null;

    @Override
    public void update()
    {
        if (blockChanged)
        {
            if (signalType == SignalType.PULSE)
            {
                if (pulseTicks == 0 && !world.isRemote) { notifyBlockUpdate(); }
                if (pulseTicks < 2) { pulseTicks++; }
                else
                {
                    blockChanged = false;
                    pulseTicks = 0;
                    if (!world.isRemote) { world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false); }
                }
            }
            else
            {
                blockChanged = false;
                if (!world.isRemote) { notifyBlockUpdate(); }
            }
        }
    }

    public void checkBlockUpdate(boolean forced)
    {
        IBlockState state = world.getBlockState(pos.offset(getFacing()));
        if ((checkMode == CheckMode.STATE && state != lastState) || (checkMode == CheckMode.BLOCK && (lastState == null || state.getBlock() != lastState.getBlock())) || forced)
        {
            lastState = state;
            blockChanged = true;
            world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false);
            notifyBlockUpdate();
        }
    }

    public int getPower()
    {
        if (lastState == null) { checkBlockUpdate(false); }
        if (blockChanged && signalType == SignalType.PULSE) { return 15; }
        else if (signalType == SignalType.NORMAL) { return stateFilter == lastState ? 15 : 0; }
        else if (signalType == SignalType.INVERTED) { return stateFilter == lastState ? 0 : 15; }
        return 0;
    }

    public void setStateFilter(IBlockState block)
    {
        this.stateFilter = block;
        filterChanged = true;
        checkBlockUpdate(true);
        notifyBlockUpdate();
    }

    public void setSignalType(SignalType type)
    {
        this.signalType = type;
        checkBlockUpdate(true);
        notifyBlockUpdate();
    }

    public void setCheckMode(CheckMode checkMode)
    {
        this.checkMode = checkMode;
        checkBlockUpdate(true);
        notifyBlockUpdate();
    }

    public IBlockState getStateFilter()
    {
        return stateFilter;
    }

    public SignalType getSignalType()
    {
        return signalType;
    }

    public CheckMode getCheckMode()
    {
        return checkMode;
    }

    public void findBlock()
    {
        setStateFilter(world.getBlockState(pos.offset(getFacing())));
    }

    public boolean hasFilterChanged()
    {
        if (filterChanged)
        {
            filterChanged = false;
            return true;
        }
        return false;
    }

    private EnumFacing getFacing()
    {
        if (facing == null) { facing = world.getBlockState(pos).getValue(BlockBlockDetector.FACING); }
        return facing;
    }

    public void notifyBlockUpdate()
    {
        markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("lastState", lastState == null ? new NBTTagCompound() : NBTUtil.writeBlockState(new NBTTagCompound(), lastState));
        nbt.setBoolean("changed", blockChanged);
        return writeToNBT(nbt);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound nbt)
    {
        super.handleUpdateTag(nbt);
        NBTTagCompound tag = nbt.getCompoundTag("lastState");
        lastState = tag.getSize() == 0 ? null : NBTUtil.readBlockState(tag);
        blockChanged = nbt.getBoolean("changed");
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, getBlockMetadata(), getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt = super.writeToNBT(nbt);
        nbt.setInteger("signal", signalType.ordinal());
        nbt.setInteger("mode", checkMode.ordinal());
        if (stateFilter == null) { stateFilter = Blocks.AIR.getDefaultState(); }
        nbt.setTag("filter", NBTUtil.writeBlockState(new NBTTagCompound(), stateFilter));
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        signalType = SignalType.values()[nbt.getInteger("signal")];
        checkMode = CheckMode.values()[nbt.getInteger("mode")];
        stateFilter = NBTUtil.readBlockState(nbt.getCompoundTag("filter"));
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    public enum SignalType
    {
        NORMAL,
        INVERTED,
        PULSE;

        @SideOnly(Side.CLIENT)
        public String getLocalizedName()
        {
            return I18n.format("blockdetector:signaltype." + toString().toLowerCase(Locale.ENGLISH) + ".name");
        }

        public ResourceLocation getGuiTexture()
        {
            switch (this)
            {
                case NORMAL: return new ResourceLocation("textures/blocks/redstone_torch_on.png");
                case INVERTED: return new ResourceLocation("textures/blocks/redstone_torch_off.png");
                case PULSE: return new ResourceLocation("textures/items/redstone_dust.png");
            }
            return null;
        }
    }

    public enum CheckMode
    {
        BLOCK,
        STATE;

        @SideOnly(Side.CLIENT)
        public String getLocalizedName()
        {
            return I18n.format("blockdetector:checkmode." + toString().toLowerCase(Locale.ENGLISH) + ".name");
        }
    }
}