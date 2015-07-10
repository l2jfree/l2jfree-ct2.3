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
package com.l2jfree.gameserver.gameobjects.knownlist;

import com.l2jfree.gameserver.gameobjects.L2Creature;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.model.L2Object;

/**
 * @author Kerberos
 *
 */
public class AirShipKnownList extends CreatureKnownList
{
	
	/**
	 * @param activeChar
	 */
	public AirShipKnownList(L2Creature activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		if (!(object instanceof L2Player))
			return 0;
		return 8000;
	}
	
	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		if (!(object instanceof L2Player))
			return 0;
		return 4000;
	}
}
