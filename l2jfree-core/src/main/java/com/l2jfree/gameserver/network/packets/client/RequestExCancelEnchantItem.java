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

import com.l2jfree.gameserver.gameobjects.instance.L2PcInstance;
import com.l2jfree.gameserver.network.packets.L2ClientPacket;
import com.l2jfree.gameserver.network.packets.server.ActionFailed;
import com.l2jfree.gameserver.network.packets.server.EnchantResult;

/**
 * @author evill33t
 * 
 */
public class RequestExCancelEnchantItem extends L2ClientPacket
{
	private static final String _C__D0_81_REQUESTEXCANCELENCHANTITEM = "[C] D0 51 RequestExCancelEnchantItem";
	
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar != null)
		{
			sendPacket(new EnchantResult(2, 0, 0));
			sendPacket(ActionFailed.STATIC_PACKET);
			activeChar.setActiveEnchantItem(null);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__D0_81_REQUESTEXCANCELENCHANTITEM;
	}
}
