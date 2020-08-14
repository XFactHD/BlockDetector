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

package XFactHD.blockdetector.common;

import XFactHD.blockdetector.common.block.BlockBlockDetector;
import XFactHD.blockdetector.common.block.TileEntityBlockDetector;
import XFactHD.blockdetector.common.net.PacketSetValues;
import XFactHD.blockdetector.common.utils.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Objects;

public class CommonProxy
{
    public void preInit()
    {
        BlockDetector.BD_NET_WRAPPER.registerMessage(PacketSetValues.Handler.class, PacketSetValues.class, 0, Side.SERVER);
        NetworkRegistry.INSTANCE.registerGuiHandler(BlockDetector.INSTANCE, new GuiHandler());
    }

    public static BlockBlockDetector blockDetector;
    public static ItemBlock itemBlockDetector;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        blockDetector = new BlockBlockDetector();
        event.getRegistry().register(blockDetector);
        GameRegistry.registerTileEntity(TileEntityBlockDetector.class, "detector");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        itemBlockDetector = new ItemBlock(blockDetector);
        itemBlockDetector.setRegistryName(Objects.requireNonNull(blockDetector.getRegistryName()));
        itemBlockDetector.setCreativeTab(CreativeTabs.REDSTONE);
        event.getRegistry().register(itemBlockDetector);
    }
}