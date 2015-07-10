/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfree.gameserver.network.packets.client;

import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.network.packets.L2ClientPacket;
import com.l2jfree.gameserver.network.packets.server.HennaEquipList;

/**
 * RequestHennaDrawList - 0xba
 *
 * @author Tempy
 */
public final class RequestHennaDrawList extends L2ClientPacket
{
	private static final String _C__BA_RequestHennaDrawList = "[C] ba RequestHennaDrawList";
	
	//private int _unknown;
	
	@Override
	protected void readImpl()
	{
		/*_unknown = */readD(); // ?? just a trigger packet
	}
	
	@Override
	protected void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		sendPacket(new HennaEquipList(activeChar));
		
		sendAF();
	}
	
	@Override
	public String getType()
	{
		return _C__BA_RequestHennaDrawList;
	}
}
