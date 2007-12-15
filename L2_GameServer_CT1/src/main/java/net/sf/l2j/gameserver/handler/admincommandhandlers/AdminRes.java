/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * [URL]http://www.gnu.org/copyleft/gpl.html[/URL]
 */
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2ControllableMobInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.taskmanager.DecayTaskManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class handles following admin commands:
 * - res = resurrects target L2Character
 * 
 * @version $Revision: 1.2.4.5 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminRes implements IAdminCommandHandler 
{
	private final static Log _log = LogFactory.getLog(AdminRes.class.getName());
	private static final String[] ADMIN_COMMANDS = {"admin_res", "admin_res_monster"};
	private static final int REQUIRED_LEVEL = Config.GM_RES;
	
	public boolean useAdminCommand(String command, L2PcInstance activeChar) 
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM())) 
				return false;
		
		if (command.startsWith("admin_res "))
			handleRes(activeChar, command.split(" ")[1]);
		else if (command.equals("admin_res"))
			handleRes(activeChar);
		else if (command.startsWith("admin_res_monster "))
			handleNonPlayerRes(activeChar, command.split(" ")[1]);
		else if (command.equals("admin_res_monster"))
			handleNonPlayerRes(activeChar);
		
		return true;
	}
	
	public String[] getAdminCommandList() 
	{
		return ADMIN_COMMANDS;
	}
	
	private boolean checkLevel(int level) 
	{
		return (level >= REQUIRED_LEVEL);
	}
	
	private void handleRes(L2PcInstance activeChar)
	{
		handleRes(activeChar, null);
	}
	
	private void handleRes(L2PcInstance activeChar, String resParam) 
	{
		L2Object obj = activeChar.getTarget();
		
		if (resParam != null)
		{
			// Check if a player name was specified as a param.
			L2PcInstance plyr = L2World.getInstance().getPlayer(resParam);
			
			if (plyr != null)
			{
				obj = plyr;
			}
			else
			{
				// Otherwise, check if the param was a radius.
				try {
					int radius = Integer.parseInt(resParam);
					
					for (L2PcInstance knownPlayer : activeChar.getKnownList().getKnownPlayersInRadius(radius))
						doResurrect(knownPlayer);
					
					activeChar.sendMessage("Resurrected all players within a " + radius + " unit radius.");
					return;
				}
				catch (NumberFormatException e) {
					activeChar.sendMessage("Enter a valid player name or radius.");
					return;
				}
			}
		}
		
		if (obj == null)
			obj = activeChar;
		
		if (obj instanceof L2ControllableMobInstance)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}

		doResurrect((L2Character)obj);
		
		if (_log.isDebugEnabled()) 
			_log.debug("GM: "+activeChar.getName()+"("+activeChar.getObjectId()+") resurrected character "+ obj.getObjectId());
	}
	
	private void handleNonPlayerRes(L2PcInstance activeChar)
	{
		handleNonPlayerRes(activeChar, "");
	}
	
	private void handleNonPlayerRes(L2PcInstance activeChar, String radiusStr)
	{
		L2Object obj = activeChar.getTarget();
		
		try
		{
			int radius = 0;
			
			if (!radiusStr.equals(""))
			{
				radius = Integer.parseInt(radiusStr);
				
				for (L2Character knownChar : activeChar.getKnownList().getKnownCharactersInRadius(radius))
					if (!(knownChar instanceof L2PcInstance)
							&& !(knownChar instanceof L2ControllableMobInstance))
						doResurrect(knownChar);
				
				activeChar.sendMessage("Resurrected all non-players within a " + radius + " unit radius.");
			}
		}
		catch (NumberFormatException e) {
			activeChar.sendMessage("Enter a valid radius.");
			return;
		}
		
		if (obj == null || obj instanceof L2PcInstance || obj instanceof L2ControllableMobInstance)
        {
		    activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
		    return;
        }

		doResurrect((L2Character)obj);
	}

	private void doResurrect(L2Character targetChar)
	{
		if(!targetChar.isDead()) return;

		// If the target is a player, then restore the XP lost on death.
		if (targetChar instanceof L2PcInstance)
			((L2PcInstance)targetChar).restoreExp(100.0);
		
		// If the target is an NPC, then abort it's auto decay and respawn.
		else
			DecayTaskManager.getInstance().cancelDecayTask(targetChar);

		targetChar.doRevive();
	}
}
