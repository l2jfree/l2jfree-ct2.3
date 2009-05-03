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
package com.l2jfree.gameserver.model.restriction.global;

import com.l2jfree.Config;
import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.entity.events.DM;
import com.l2jfree.gameserver.network.SystemMessageId;

/**
 * @author NB4L1
 */
final class DMRestriction extends AbstractRestriction
{
	@Override
	public boolean canInviteToParty(L2PcInstance activeChar, L2PcInstance target)
	{
		if (DM._started && !Config.DM_ALLOW_INTERFERENCE && !activeChar.isGM())
		{
			if (target._inEventDM != activeChar._inEventDM)
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean isInvul(L2Character activeChar, L2Character target, boolean isOffensive)
	{
		L2PcInstance attacker_ = L2Object.getActingPlayer(activeChar);
		L2PcInstance target_ = L2Object.getActingPlayer(target);
		
		if (attacker_ == null || target_ == null || attacker_ == target_)
			return false;
		
		if (attacker_._inEventDM != target_._inEventDM && !Config.DM_ALLOW_INTERFERENCE)
			return true;
		
		return false;
	}
	
	@Override
	public void levelChanged(L2PcInstance activeChar)
	{
		if (activeChar._inEventDM && DM._maxlvl == activeChar.getLevel() && !DM._started)
		{
			DM.removePlayer(activeChar);
			
			activeChar.sendMessage("Your event sign up was canceled.");
		}
	}
	
	@Override
	public void playerLoggedIn(L2PcInstance activeChar)
	{
		if (DM._savePlayers.contains(activeChar.getName()))
			DM.addDisconnectedPlayer(activeChar);
	}
	
	@Override
	public boolean playerKilled(L2Character activeChar, final L2PcInstance target)
	{
		if (!target._inEventDM)
			return false;
		
		if (DM._teleport || DM._started)
		{
			if (activeChar instanceof L2PcInstance && ((L2PcInstance)activeChar)._inEventDM)
				((L2PcInstance)activeChar)._countDMkills++;
			
			target.sendMessage("You will be revived and teleported to spot in " + Config.DM_REVIVE_DELAY / 1000
				+ " seconds!");
			
			ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
				public void run()
				{
					target.teleToLocation(DM._playerX, DM._playerY, DM._playerZ, false);
					target.doRevive();
				}
			}, Config.DM_REVIVE_DELAY);
		}
		
		return true;
	}
}
