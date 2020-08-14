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

package XFactHD.blockdetector.client;

import XFactHD.blockdetector.client.gui.GuiBlockDetector;
import XFactHD.blockdetector.common.CommonProxy;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy
{
    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public static void registerModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(CommonProxy.itemBlockDetector, 0, new ModelResourceLocation(CommonProxy.itemBlockDetector.getRegistryName(), "inventory"));
    }

    @Override
    public void preInit()
    {
        super.preInit();

        GuiBlockDetector.addSpecialBlockMapping(Blocks.FIRE, Items.FLINT_AND_STEEL);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.WHEAT, Items.WHEAT_SEEDS);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.POTATOES, Items.POTATO);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.CARROTS, Items.CARROT);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.PUMPKIN_STEM, Items.PUMPKIN_SEEDS);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.MELON_STEM, Items.MELON_SEEDS);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.BEETROOTS, Items.BEETROOT_SEEDS);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.REEDS, Items.REEDS);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.COCOA, new ItemStack(Items.DYE, 1, 3));
        GuiBlockDetector.addSpecialBlockMapping(Blocks.REDSTONE_WIRE, Items.REDSTONE);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.POWERED_REPEATER, Items.REPEATER);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.UNPOWERED_REPEATER, Items.REPEATER);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.POWERED_COMPARATOR, Items.COMPARATOR);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.UNPOWERED_COMPARATOR, Items.COMPARATOR);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.CAULDRON, Items.CAULDRON);
        GuiBlockDetector.addSpecialBlockMapping(Blocks.BREWING_STAND, Items.BREWING_STAND);

        GuiBlockDetector.addStateStringMapping("minecraft:wheat[age=0]", "state.blockdetector:minecraft:wheat[age_0].name");
        GuiBlockDetector.addStateStringMapping("minecraft:wheat[age=1]", "state.blockdetector:minecraft:wheat[age_1].name");
        GuiBlockDetector.addStateStringMapping("minecraft:wheat[age=2]", "state.blockdetector:minecraft:wheat[age_2].name");
        GuiBlockDetector.addStateStringMapping("minecraft:wheat[age=3]", "state.blockdetector:minecraft:wheat[age_3].name");
        GuiBlockDetector.addStateStringMapping("minecraft:wheat[age=4]", "state.blockdetector:minecraft:wheat[age_4].name");
        GuiBlockDetector.addStateStringMapping("minecraft:wheat[age=5]", "state.blockdetector:minecraft:wheat[age_5].name");
        GuiBlockDetector.addStateStringMapping("minecraft:wheat[age=6]", "state.blockdetector:minecraft:wheat[age_6].name");
        GuiBlockDetector.addStateStringMapping("minecraft:wheat[age=7]", "state.blockdetector:minecraft:wheat[age_7].name");

        GuiBlockDetector.addStateStringMapping("minecraft:carrots[age=0]", "state.blockdetector:minecraft:carrots[age_0].name");
        GuiBlockDetector.addStateStringMapping("minecraft:carrots[age=1]", "state.blockdetector:minecraft:carrots[age_1].name");
        GuiBlockDetector.addStateStringMapping("minecraft:carrots[age=2]", "state.blockdetector:minecraft:carrots[age_2].name");
        GuiBlockDetector.addStateStringMapping("minecraft:carrots[age=3]", "state.blockdetector:minecraft:carrots[age_3].name");
        GuiBlockDetector.addStateStringMapping("minecraft:carrots[age=4]", "state.blockdetector:minecraft:carrots[age_4].name");
        GuiBlockDetector.addStateStringMapping("minecraft:carrots[age=5]", "state.blockdetector:minecraft:carrots[age_5].name");
        GuiBlockDetector.addStateStringMapping("minecraft:carrots[age=6]", "state.blockdetector:minecraft:carrots[age_6].name");
        GuiBlockDetector.addStateStringMapping("minecraft:carrots[age=7]", "state.blockdetector:minecraft:carrots[age_7].name");

        GuiBlockDetector.addStateStringMapping("minecraft:potatoes[age=0]", "state.blockdetector:minecraft:potatoes[age_0].name");
        GuiBlockDetector.addStateStringMapping("minecraft:potatoes[age=1]", "state.blockdetector:minecraft:potatoes[age_1].name");
        GuiBlockDetector.addStateStringMapping("minecraft:potatoes[age=2]", "state.blockdetector:minecraft:potatoes[age_2].name");
        GuiBlockDetector.addStateStringMapping("minecraft:potatoes[age=3]", "state.blockdetector:minecraft:potatoes[age_3].name");
        GuiBlockDetector.addStateStringMapping("minecraft:potatoes[age=4]", "state.blockdetector:minecraft:potatoes[age_4].name");
        GuiBlockDetector.addStateStringMapping("minecraft:potatoes[age=5]", "state.blockdetector:minecraft:potatoes[age_5].name");
        GuiBlockDetector.addStateStringMapping("minecraft:potatoes[age=6]", "state.blockdetector:minecraft:potatoes[age_6].name");
        GuiBlockDetector.addStateStringMapping("minecraft:potatoes[age=7]", "state.blockdetector:minecraft:potatoes[age_7].name");
    }
}