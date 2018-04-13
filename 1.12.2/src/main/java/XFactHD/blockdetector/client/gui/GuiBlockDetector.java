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

package XFactHD.blockdetector.client.gui;

import XFactHD.blockdetector.common.BlockDetector;
import XFactHD.blockdetector.common.block.TileEntityBlockDetector;
import XFactHD.blockdetector.common.gui.ContainerBlockDetector;
import XFactHD.blockdetector.common.net.PacketSetValues;
import XFactHD.blockdetector.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Arrays;

public class GuiBlockDetector extends GuiContainer
{
    private TileEntityBlockDetector te;
    private EntityPlayer player;
    private ItemStack filter;
    private TileEntityBlockDetector.SignalType type;
    private int fireIndex = 0;

    public GuiBlockDetector(TileEntityBlockDetector te, EntityPlayer player)
    {
        super(new ContainerBlockDetector(te, player));
        this.te = te;
        this.player = player;
        filter = te.getBlockFilter() == Blocks.AIR ? ItemStack.EMPTY : (te.getBlockFilter() == Blocks.FIRE ? new ItemStack(Items.FLINT_AND_STEEL) : new ItemStack(te.getBlockFilter()));
        type = te.getSignalType();
        setGuiSize(176, 190);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        guiTop += 16;
        buttonList.add(new GuiButton(0, guiLeft + 70, guiTop + 16, 80, 20, "     " + type.getLocalizedName()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        mc.getTextureManager().bindTexture(new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_block_detector.png"));
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 176, 190);
        if (!filter.isEmpty())
        {
            if (filter.getItem() == Items.FLINT_AND_STEEL)
            {
                mc.getTextureManager().bindTexture(new ResourceLocation("textures/blocks/fire_layer_0.png"));
                drawModalRectWithCustomSizedTexture(guiLeft + 26, guiTop + 18, 0, 16 * fireIndex, 16, 16, 16, 512);
            }
            else
            {
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(filter, guiLeft + 26, guiTop + 18);
                RenderHelper.disableStandardItemLighting();
            }
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        mc.getTextureManager().bindTexture(type.getGuiTexture());
        drawModalRectWithCustomSizedTexture(74, type == TileEntityBlockDetector.SignalType.PULSE ? 17 : 15, 0, 0, 16, 16, 16, 16);
        if (mouseX >= guiLeft + 70 && mouseX < guiLeft + 150 && mouseY >= guiTop + 16 && mouseY < guiTop + 36)
        {
            String[] text = new String[]{};
            switch (type)
            {

                case NORMAL: text = I18n.format("desc.blockdetector:normal.name").split("\\|"); break;
                case INVERTED: text = I18n.format("desc.blockdetector:inverted.name").split("\\|"); break;
                case PULSE: text = I18n.format("desc.blockdetector:pulse.name").split("\\|"); break;
            }
            drawHoveringText(Arrays.asList(text), mouseX - guiLeft - 3, mouseY - guiTop + 23);
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        if (filter.getItem() == Items.FLINT_AND_STEEL)
        {
            fireIndex++;
            if (fireIndex > 31)
            {
                fireIndex = 0;
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            int index = type.ordinal();
            index++;
            if (index >= TileEntityBlockDetector.SignalType.values().length) { index = 0; }
            type = TileEntityBlockDetector.SignalType.values()[index];
            button.displayString = "     " + type.getLocalizedName();
            BlockDetector.BD_NET_WRAPPER.sendToServer(new PacketSetValues(te.getPos(), null, type));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX >= guiLeft + 26 && mouseX <= guiLeft + 42 && mouseY >= guiTop + 18 && mouseY <= guiTop + 34)
        {
            ItemStack stack = player.inventory.getItemStack().copy();
            if ((stack.isEmpty() || stack.getItem() == Items.FLINT_AND_STEEL || stack.getItem() instanceof ItemBlock) && stack.getItem() != filter.getItem())
            {
                filter = stack;

                Block block;
                if (stack.isEmpty()) { block = Blocks.AIR; }
                else if (stack.getItem() == Items.FLINT_AND_STEEL) { block = Blocks.FIRE; }
                else { block = ((ItemBlock)stack.getItem()).getBlock(); }

                BlockDetector.BD_NET_WRAPPER.sendToServer(new PacketSetValues(te.getPos(), block, null));
            }
            fireIndex = 0;
        }
    }
}