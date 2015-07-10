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
package com.l2jfree.gameserver.network.packets.server;

import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.network.packets.L2ServerPacket;

/**
 * This class ...
 * 
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class ObservationReturn extends L2ServerPacket
{
	// ddSS
	private static final String _S__E0_OBSERVRETURN = "[S] E0 ObservationReturn";
	private final L2Player _activeChar;
	
	/**
	 * @param _characters
	 */
	public ObservationReturn(L2Player observer)
	{
		_activeChar = observer;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xec);
		writeD(_activeChar.getObsX());
		writeD(_activeChar.getObsY());
		writeD(_activeChar.getObsZ());
	}
	
	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__E0_OBSERVRETURN;
	}
}
