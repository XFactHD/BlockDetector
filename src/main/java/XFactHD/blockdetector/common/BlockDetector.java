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

import XFactHD.blockdetector.client.ClientProxy;
import XFactHD.blockdetector.common.utils.LogHelper;
import XFactHD.blockdetector.common.utils.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class BlockDetector //TODO: add IMC comms to add block mappings for filter
{
    @Mod.Instance
    public static BlockDetector INSTANCE;

    public static SimpleNetworkWrapper BD_NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    @SidedProxy(serverSide = Reference.SERVER_PROXY, clientSide = Reference.CLIENT_PROXY)
    public static CommonProxy proxy;

    static { MinecraftForge.EVENT_BUS.register(CommonProxy.class); MinecraftForge.EVENT_BUS.register(ClientProxy.class); }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit();
        LogHelper.init(event);
    }
}