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
package com.l2jfree.gameserver.handler.skillhandlers;

import com.l2jfree.gameserver.ai.CtrlIntention;
import com.l2jfree.gameserver.handler.ISkillHandler;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.L2Skill.SkillType;
import com.l2jfree.gameserver.model.actor.instance.L2ChestInstance;
import com.l2jfree.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.ActionFailed;
import com.l2jfree.gameserver.network.serverpackets.SocialAction;
import com.l2jfree.gameserver.skills.Formulas;
import com.l2jfree.tools.random.Rnd;


public class Unlock implements ISkillHandler
{
	private static final SkillType[]	SKILL_IDS	=
													{ SkillType.UNLOCK };

	public void useSkill(L2Character activeChar, L2Skill skill, @SuppressWarnings("unused")
	L2Object... targets)
	{
		L2Object[] targetList = skill.getTargetList(activeChar);

		if (targetList == null)
			return;

		for (L2Object element : targetList)
		{
			L2Object target = element;

			if (target instanceof L2DoorInstance)
			{
				L2DoorInstance door = (L2DoorInstance) target;
				if (!door.isUnlockable() || door.getFort() != null)
				{
					activeChar.sendPacket(SystemMessageId.UNABLE_TO_UNLOCK_DOOR);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}

				if (!door.getOpen() && Formulas.getInstance().calculateUnlockChance(skill))
				{
					door.openMe();
					door.onOpen();
					activeChar.sendMessage("Unlock the door!");
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.FAILED_TO_UNLOCK_DOOR);
				}
			}
			else if (target instanceof L2ChestInstance)
			{
				L2ChestInstance chest = (L2ChestInstance) element;
				if (chest.getStatus().getCurrentHp() <= 0 || chest.isInteracted())
				{
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				else
				{
					int chestChance = 0;
					int chestGroup = 0;
					int chestTrapLimit = 0;

					if (chest.getLevel() > 60)
						chestGroup = 4;
					else if (chest.getLevel() > 40)
						chestGroup = 3;
					else if (chest.getLevel() > 30)
						chestGroup = 2;
					else
						chestGroup = 1;

					switch (chestGroup)
					{
					case 1:
						if (skill.getLevel() > 10)
							chestChance = 100;
						else if (skill.getLevel() >= 3)
							chestChance = 50;
						else if (skill.getLevel() == 2)
							chestChance = 45;
						else if (skill.getLevel() == 1)
							chestChance = 40;
						chestTrapLimit = 10;
						break;
					case 2:
						if (skill.getLevel() > 12)
							chestChance = 100;
						else if (skill.getLevel() >= 7)
							chestChance = 50;
						else if (skill.getLevel() == 6)
							chestChance = 45;
						else if (skill.getLevel() == 5)
							chestChance = 40;
						else if (skill.getLevel() == 4)
							chestChance = 35;
						else if (skill.getLevel() == 3)
							chestChance = 30;
						chestTrapLimit = 30;
						break;
					case 3:
						if (skill.getLevel() >= 14)
							chestChance = 50;
						else if (skill.getLevel() == 13)
							chestChance = 45;
						else if (skill.getLevel() == 12)
							chestChance = 40;
						else if (skill.getLevel() == 11)
							chestChance = 35;
						else if (skill.getLevel() == 10)
							chestChance = 30;
						else if (skill.getLevel() == 9)
							chestChance = 25;
						else if (skill.getLevel() == 8)
							chestChance = 20;
						else if (skill.getLevel() == 7)
							chestChance = 15;
						else if (skill.getLevel() == 6)
							chestChance = 10;
						chestTrapLimit = 50;
						break;
					case 4:
						if (skill.getLevel() >= 14)
							chestChance = 50;
						else if (skill.getLevel() == 13)
							chestChance = 45;
						else if (skill.getLevel() == 12)
							chestChance = 40;
						else if (skill.getLevel() == 11)
							chestChance = 35;
						chestTrapLimit = 80;
						break;
					}
					if (Rnd.get(100) <= chestChance)
					{
						activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 3));
						chest.setSpecialDrop();
						chest.setMustRewardExpSp(false);
						chest.setInteracted();
						chest.reduceCurrentHp(99999999, activeChar);
					}
					else
					{
						activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 13));
						if (Rnd.get(100) < chestTrapLimit)
							chest.chestTrap(activeChar);
						chest.setInteracted();
						chest.addDamageHate(activeChar, 0, 1);
						chest.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, activeChar);
					}
				}
			}
		}
	}

	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
