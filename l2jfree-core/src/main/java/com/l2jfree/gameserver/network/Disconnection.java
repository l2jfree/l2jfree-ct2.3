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
package com.l2jfree.gameserver.network;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * @author NB4L1
 */
public final class Disconnection
{
	private static final Log _log = LogFactory.getLog(Disconnection.class);
	
	public static L2Client getClient(L2Client client, L2Player activeChar)
	{
		if (client != null)
			return client;
		
		if (activeChar != null)
			return activeChar.getClient();
		
		return null;
	}
	
	public static L2Player getActiveChar(L2Client client, L2Player activeChar)
	{
		if (activeChar != null)
			return activeChar;
		
		if (client != null)
			return client.getActiveChar();
		
		return null;
	}
	
	private final L2Client _client;
	private final L2Player _activeChar;
	
	public Disconnection(L2Client client)
	{
		this(client, null);
	}
	
	public Disconnection(L2Player activeChar)
	{
		this(null, activeChar);
	}
	
	public Disconnection(L2Client client, L2Player activeChar)
	{
		_client = getClient(client, activeChar);
		_activeChar = getActiveChar(client, activeChar);
		
		if (_client != null)
			_client.setActiveChar(null);
		
		if (_activeChar != null)
			_activeChar.setClient(null);
	}
	
	public Disconnection store()
	{
		try
		{
			if (_activeChar != null)
				_activeChar.store(true, true);
		}
		catch (RuntimeException e)
		{
			_log.warn("", e);
		}
		
		return this;
	}
	
	public Disconnection deleteMe()
	{
		try
		{
			if (_activeChar != null)
				_activeChar.deleteMe();
		}
		catch (RuntimeException e)
		{
			_log.warn("", e);
		}
		
		return this;
	}
	
	public Disconnection close(boolean toLoginScreen)
	{
		if (_client != null)
			_client.close(toLoginScreen);
		
		return this;
	}
	
	public void defaultSequence(boolean toLoginScreen)
	{
		store();
		deleteMe();
		close(toLoginScreen);
	}
	
	public void onDisconnection()
	{
		if (_activeChar != null)
		{
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run()
				{
					store();
					deleteMe();
				}
			}, _activeChar.canLogout() ? 0 : AttackStanceTaskManager.COMBAT_TIME);
		}
	}
}
