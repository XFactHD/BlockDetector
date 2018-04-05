/*  Copyright (C) <2017>  <XFactHD>

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

package XFactHD.blockdetector.common.gui;

import XFactHD.blockdetector.common.block.TileEntityBlockDetector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;

public class ContainerBlockDetector extends Container
{
    private TileEntityBlockDetector te;
    private TileEntityBlockDetector.SignalType lastType = TileEntityBlockDetector.SignalType.NORMAL;

    public ContainerBlockDetector(TileEntityBlockDetector te, EntityPlayer player)
    {
        this.te = te;

        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 9; ++x)
            {
                addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 52 + y * 18));
            }
        }
        for (int x = 0; x < 9; ++x)
        {
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 110));
        }
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        if (lastType != te.getSignalType())
        {
            for (IContainerListener listener : listeners)
            {
                lastType = te.getSignalType();
                listener.sendWindowProperty(this, 0, lastType.ordinal());
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data)
    {
        if (id == 0)
        {
            lastType = TileEntityBlockDetector.SignalType.values()[data];
            te.setSignalType(lastType);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }
}