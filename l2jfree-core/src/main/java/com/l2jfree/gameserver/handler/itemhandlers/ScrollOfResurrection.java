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
package com.l2jfree.gameserver.handler.itemhandlers;

import com.l2jfree.gameserver.datatables.SkillTable;
import com.l2jfree.gameserver.handler.IItemHandler;
import com.l2jfree.gameserver.instancemanager.SiegeManager;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PetInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfree.gameserver.model.entity.Siege;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;

/** 
 * This class ... 
 * 
 * @version $Revision: 1.1.2.2.2.7 $ $Date: 2005/04/05 19:41:13 $ 
 */

public class ScrollOfResurrection implements IItemHandler
{
	// all the items ids that this handler knows 
	private static final int[]	ITEM_IDS	=
											{ 737, 3936, 3959, 6387 };

	/* (non-Javadoc) 
	 * @see com.l2jfree.gameserver.handler.IItemHandler#useItem(com.l2jfree.gameserver.model.L2PcInstance, com.l2jfree.gameserver.model.L2ItemInstance) 
	 */
	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance) playable;
		if (activeChar.isSitting())
		{
			activeChar.sendPacket(SystemMessageId.CANT_MOVE_SITTING);
			return;
		}
		if (activeChar.isMovementDisabled())
			return;

		int itemId = item.getItemId();
		//boolean blessedScroll = (itemId != 737);
		boolean humanScroll = (itemId == 3936 || itemId == 3959 || itemId == 737);
		boolean petScroll = (itemId == 6387 || itemId == 737);

		// SoR Animation section 
		L2Object object = activeChar.getTarget();
		if (object != null && object instanceof L2Character)
		{
			L2Character target = (L2Character) object;

			if (target.isDead())
			{
				L2PcInstance targetPlayer = null;

				if (target instanceof L2PcInstance)
					targetPlayer = (L2PcInstance) target;

				L2PetInstance targetPet = null;

				if (target instanceof L2PetInstance)
					targetPet = (L2PetInstance) target;

				if (targetPlayer != null || targetPet != null)
				{
					boolean condGood = true;

					//check target is not in a active siege zone
					Siege siege = null;

					if (targetPlayer != null)
						siege = SiegeManager.getInstance().getSiege(targetPlayer);
					else
						siege = SiegeManager.getInstance().getSiege(targetPet);

					if (siege != null && siege.getIsInProgress())
					{
						condGood = false;
						activeChar.sendPacket(SystemMessageId.CANNOT_BE_RESURRECTED_DURING_SIEGE);
					}

					siege = null;

					if (targetPet != null)
					{
						if (targetPet.getOwner().isPetReviveRequested())
						{
							activeChar.sendPacket(SystemMessageId.RES_HAS_ALREADY_BEEN_PROPOSED); // Resurrection is already been proposed.
							condGood = false;
						}
						else if (!petScroll && targetPet.getOwner() != activeChar)
						{
							condGood = false;
							activeChar.sendMessage("You do not have the correct scroll.");
						}
					}
					else
					{
						if (targetPlayer != null && targetPlayer.isFestivalParticipant()) // Check to see if the current player target is in a festival.
						{
							condGood = false;
							activeChar.sendMessage("You may not resurrect participants in a festival.");
						}
						else if (targetPlayer != null && targetPlayer.isReviveRequested())
						{
							activeChar.sendPacket(SystemMessageId.RES_HAS_ALREADY_BEEN_PROPOSED); // Resurrection is already been proposed.
							condGood = false;
						}
						else if (!humanScroll)
						{
							condGood = false;
							activeChar.sendMessage("You do not have the correct scroll.");
						}
					}

					if (condGood)
					{
						if (!activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false))
							return;

						int skillId = 0;
						int skillLevel = 1;

						switch (itemId)
						{
						case 737:
							skillId = 2014;
							break; // Scroll of Resurrection
						case 3936:
							skillId = 2049;
							break; // Blessed Scroll of Resurrection
						case 3959:
							skillId = 2062;
							break; // L2Day - Blessed Scroll of Resurrection
						case 6387:
							skillId = 2179;
							break; // Blessed Scroll of Resurrection: For Pets
						}

						if (skillId != 0)
						{
							L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
							activeChar.useMagic(skill, true, true);

							SystemMessage sm = new SystemMessage(SystemMessageId.S1_DISAPPEARED);
							sm.addItemName(item);
							activeChar.sendPacket(sm);
						}
					}
				}
			}
			else
			{
				activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			}
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
		}
	}

	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
