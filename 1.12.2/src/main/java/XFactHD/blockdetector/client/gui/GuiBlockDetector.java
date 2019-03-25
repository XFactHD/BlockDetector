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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class GuiBlockDetector extends GuiContainer
{
    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
    private static final HashMap<Block, ItemStack> specialBlockMap = new HashMap<>();
    private static final HashMap<ItemStack, Block> specialBlockMapReverse = new HashMap<>();
    private static final HashMap<String, String> stateStringMap = new HashMap<>();

    private TileEntityBlockDetector te;
    private EntityPlayer player;
    private ItemStack filter;
    private TileEntityBlockDetector.SignalType type;
    private TileEntityBlockDetector.CheckMode mode;
    private int fireIndex = 0;

    public GuiBlockDetector(TileEntityBlockDetector te, EntityPlayer player)
    {
        super(new ContainerBlockDetector(te, player));
        this.te = te;
        this.player = player;

        IBlockState stateFilter = te.getStateFilter();
        if (stateFilter.getBlock() == Blocks.AIR) { filter = ItemStack.EMPTY; }
        else if (specialBlockMap.containsKey(stateFilter.getBlock())) { filter = specialBlockMap.get(stateFilter.getBlock()); }
        else { filter = new ItemStack(stateFilter.getBlock()); }

        type = te.getSignalType();
        mode = te.getCheckMode();
        setGuiSize(176, 190);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        guiTop -= 16;
        buttonList.add(new GuiButton(0, guiLeft + 70, guiTop + 16, 100, 20, "     " + type.getLocalizedName()));
        buttonList.add(new GuiButton(1, guiLeft + 70, guiTop + 40, 100, 20, "     " + I18n.format("button.blockdetector:state_aware.name")));
        buttonList.add(new GuiButton(2, guiLeft + 70, guiTop + 64, 100, 20, "     " + I18n.format("button.blockdetector:copy_state.name")));
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
        GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, 77, 44, 0, 46, 11, 11, 200, 20, 2, 3, 2, 2, this.zLevel);
        if(mode == TileEntityBlockDetector.CheckMode.STATE) { fontRenderer.drawString("x", 80, 45, 0xFFFFFF); }
        if (mouseX >= guiLeft + 70 && mouseX < guiLeft + 170 && mouseY >= guiTop + 16 && mouseY < guiTop + 36)
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
        else if (mouseX >= guiLeft + 70 && mouseX < guiLeft + 170 && mouseY >= guiTop + 40 && mouseY < guiTop + 60)
        {
            String[] text = I18n.format("desc.blockdetector:state_aware.name").split("\\|");
            drawHoveringText(Arrays.asList(text), mouseX - guiLeft - 3, mouseY - guiTop + 23);
        }
        else if (mouseX >= guiLeft + 70 && mouseX < guiLeft + 170 && mouseY >= guiTop + 64 && mouseY < guiTop + 84)
        {
            String[] text = I18n.format("desc.blockdetector:copy_state.name").split("\\|");
            drawHoveringText(Arrays.asList(text), mouseX - guiLeft - 3, mouseY - guiTop + 23);
        }
        else if (mouseX >= guiLeft + 25 && mouseX < guiLeft + 43 && mouseY >= guiTop + 17 && mouseY < guiTop + 35)
        {
            IBlockState filterState = te.getStateFilter();

            String blockString = filterState.getBlock().getLocalizedName();

            String stateString = mode == TileEntityBlockDetector.CheckMode.STATE ? filterState.toString() : I18n.format("desc.blockdetector:default.name");
            if (stateStringMap.containsKey(stateString))
            {
                stateString = I18n.format(stateStringMap.get(stateString));
            }
            else if (stateString.indexOf('[') != -1)
            {
                stateString = stateString.substring(stateString.indexOf('['));
            }
            else
            {
                stateString = I18n.format("desc.blockdetector:default.name");
            }

            String[] text =
                    {
                            I18n.format("desc.blockdetector:block.name") + " " + blockString,
                            I18n.format("desc.blockdetector:state.name") + " " + stateString
                    };

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
        if (((ContainerBlockDetector)inventorySlots).filterChanged)
        {
            ((ContainerBlockDetector)inventorySlots).filterChanged = false;
            IBlockState stateFilter = te.getStateFilter();
            if (stateFilter.getBlock() == Blocks.AIR) { filter = ItemStack.EMPTY; }
            else if (specialBlockMap.containsKey(stateFilter.getBlock())) { filter = specialBlockMap.get(stateFilter.getBlock()); }
            else { filter = new ItemStack(stateFilter.getBlock()); }
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
            BlockDetector.BD_NET_WRAPPER.sendToServer(new PacketSetValues(te.getPos(), null, false, type, null));
        }
        else if (button.id == 1)
        {
            mode = (mode == TileEntityBlockDetector.CheckMode.BLOCK) ? TileEntityBlockDetector.CheckMode.STATE : TileEntityBlockDetector.CheckMode.BLOCK;
            BlockDetector.BD_NET_WRAPPER.sendToServer(new PacketSetValues(te.getPos(), null, false, null, mode));
        }
        else if (button.id == 2)
        {
            BlockDetector.BD_NET_WRAPPER.sendToServer(new PacketSetValues(te.getPos(), null, true, null, null));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX >= guiLeft + 26 && mouseX <= guiLeft + 42 && mouseY >= guiTop + 18 && mouseY <= guiTop + 34)
        {
            ItemStack stack = player.inventory.getItemStack().copy();
            if ((stack.isEmpty() || reverseMapContainsStack(stack) || stack.getItem() instanceof ItemBlock) && stack.getItem() != filter.getItem())
            {
                filter = stack;

                IBlockState block;
                if (stack.getItem() instanceof ItemBlock) { block = ((ItemBlock)stack.getItem()).getBlock().getDefaultState(); }
                else if (reverseMapContainsStack(stack)) { block = reverseMapGetBlock(stack).getDefaultState(); }
                else { block = Blocks.AIR.getDefaultState(); }

                BlockDetector.BD_NET_WRAPPER.sendToServer(new PacketSetValues(te.getPos(), block, false, null, null));
            }
            fireIndex = 0;
        }
    }

    public static void addSpecialBlockMapping(Block block, ItemStack stack)
    {
        specialBlockMap.put(block, stack);
        specialBlockMapReverse.put(stack, block);
    }

    public static void addSpecialBlockMapping(Block block, Item item)
    {
        addSpecialBlockMapping(block, new ItemStack(item));
    }

    //Map a translation key to a certain block states string representation
    public static void addStateStringMapping(String state, String alternative)
    {
        stateStringMap.put(state, alternative);
    }

    //Probably not the most efficient way but hash keys stop me from using Map#containsKey()
    private boolean reverseMapContainsStack(ItemStack testStack)
    {
        for (ItemStack stack : specialBlockMapReverse.keySet())
        {
            if (ItemStack.areItemStacksEqual(stack, testStack))
            {
                return true;
            }
        }
        return true;
    }

    private Block reverseMapGetBlock(ItemStack keyStack)
    {
        for (ItemStack stack : specialBlockMapReverse.keySet())
        {
            if (ItemStack.areItemStacksEqual(stack, keyStack))
            {
                return specialBlockMapReverse.get(stack);
            }
        }
        return specialBlockMapReverse.get(keyStack);
    }
}