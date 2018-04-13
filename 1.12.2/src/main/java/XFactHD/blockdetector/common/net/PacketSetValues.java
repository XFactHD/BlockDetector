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

package XFactHD.blockdetector.common.net;

import XFactHD.blockdetector.common.block.TileEntityBlockDetector;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetValues implements IMessage
{
    private BlockPos pos;
    private Block filter;
    private TileEntityBlockDetector.SignalType type;

    @SuppressWarnings("unused")
    public PacketSetValues() {}

    public PacketSetValues(BlockPos pos, Block filter, TileEntityBlockDetector.SignalType type)
    {
        this.pos = pos;
        this.filter = filter;
        this.type = type;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        buf.writeInt(type == null ? -1 : type.ordinal());
        ByteBufUtils.writeUTF8String(buf, filter == null ? "null" : filter.getRegistryName().toString());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        int index = buf.readInt();
        type = index == -1 ? null : TileEntityBlockDetector.SignalType.values()[index];
        String regName = ByteBufUtils.readUTF8String(buf);
        filter = regName.equals("null") ? null : Block.getBlockFromName(regName);
    }

    public static class Handler implements IMessageHandler<PacketSetValues, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSetValues message, MessageContext ctx)
        {
            World world = ctx.getServerHandler().player.world;
            ((WorldServer)world).addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    TileEntity te = world.getTileEntity(message.pos);
                    if (te instanceof TileEntityBlockDetector)
                    {
                        if (message.filter != null)
                        {
                            ((TileEntityBlockDetector)te).setBlockFilter(message.filter);
                        }
                        if (message.type != null)
                        {
                            ((TileEntityBlockDetector)te).setSignalType(message.type);
                        }
                    }
                }
            });
            return null;
        }
    }
}