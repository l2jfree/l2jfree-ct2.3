/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;

import javolution.util.FastList;
import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.datatables.ArmorSetsTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2ItemInstance.ItemLocation;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.templates.L2Armor;
import net.sf.l2j.gameserver.templates.L2EtcItem;
import net.sf.l2j.gameserver.templates.L2EtcItemType;
import net.sf.l2j.gameserver.templates.L2Item;
import net.sf.l2j.gameserver.templates.L2Weapon;
import net.sf.l2j.gameserver.templates.L2WeaponType;

/**
 * This class manages inventory
 * 
 * @version $Revision: 1.13.2.9.2.12 $ $Date: 2005/03/29 23:15:15 $ rewritten 23.2.2006 by Advi
 */
public abstract class Inventory extends ItemContainer
{
	// protected static final Logger _log = Logger.getLogger(Inventory.class.getName());
	
	public interface PaperdollListener
	{
		public void notifyEquiped(int slot, L2ItemInstance inst);
		
		public void notifyUnequiped(int slot, L2ItemInstance inst);
	}
	
	public static final int						PAPERDOLL_UNDER		= 0;
	public static final int						PAPERDOLL_HEAD		= 1;
	public static final int						PAPERDOLL_HAIR		= 2;
	public static final int						PAPERDOLL_HAIR2		= 3;
	public static final int						PAPERDOLL_NECK		= 4;
	public static final int						PAPERDOLL_RHAND		= 5;
	public static final int						PAPERDOLL_CHEST		= 6;
	public static final int						PAPERDOLL_LHAND		= 7;
	public static final int						PAPERDOLL_REAR		= 8;
	public static final int						PAPERDOLL_LEAR		= 9;
	public static final int						PAPERDOLL_GLOVES	= 10;
	public static final int						PAPERDOLL_LEGS		= 11;
	public static final int						PAPERDOLL_FEET		= 12;
	public static final int						PAPERDOLL_RFINGER	= 13;
	public static final int						PAPERDOLL_LFINGER	= 14;
	public static final int						PAPERDOLL_LBRACELET	= 15;
	public static final int						PAPERDOLL_RBRACELET	= 16;
	public static final int						PAPERDOLL_DECO1		= 17;
	public static final int						PAPERDOLL_DECO2		= 18;
	public static final int						PAPERDOLL_DECO3		= 19;
	public static final int						PAPERDOLL_DECO4		= 20;
	public static final int						PAPERDOLL_DECO5		= 21;
	public static final int						PAPERDOLL_DECO6		= 22;
	public static final int						PAPERDOLL_MAX		= 23;
	
	// Speed percentage mods
	public static final double					MAX_ARMOR_WEIGHT	= 12000;
	
	private final L2ItemInstance[]				_paperdoll;
	private final FastList<PaperdollListener>	_paperdollListeners;
	
	// protected to be accessed from child classes only
	protected int								_totalWeight;
	
	// used to quickly check for using of items of special type
	private int									_wearedMask;
	
	/**
	 * Recorder of alterations in inventory
	 */
	public static final class ChangeRecorder implements PaperdollListener
	{
		private final Inventory					_inventory;
		private final FastList<L2ItemInstance>	_changed;
		
		/**
		 * Constructor of the ChangeRecorder
		 * 
		 * @param inventory
		 */
		ChangeRecorder(Inventory inventory)
		{
			_inventory = inventory;
			_changed = new FastList<L2ItemInstance>();
			_inventory.addPaperdollListener(this);
		}
		
		/**
		 * Add alteration in inventory when item equiped
		 */
		@SuppressWarnings("unused")
		public void notifyEquiped(int slot, L2ItemInstance item)
		{
			if (!_changed.contains(item))
				_changed.add(item);
		}
		
		/**
		 * Add alteration in inventory when item unequiped
		 */
		@SuppressWarnings("unused")
		public void notifyUnequiped(int slot, L2ItemInstance item)
		{
			if (!_changed.contains(item))
				_changed.add(item);
		}
		
		/**
		 * Returns alterations in inventory
		 * 
		 * @return L2ItemInstance[] : array of alterated items
		 */
		public L2ItemInstance[] getChangedItems()
		{
			return _changed.toArray(new L2ItemInstance[_changed.size()]);
		}
	}
	
	final class AmmunationListener implements PaperdollListener
	{
		public void notifyUnequiped(int slot, L2ItemInstance item)
		{
			if (slot != PAPERDOLL_RHAND)
				return;
			if (Config.ASSERT)
				assert null == getPaperdollItem(PAPERDOLL_RHAND);
			
			switch (item.getItemType().hashCode())
			{
				case 5: // L2WeaponType.BOW
				case 13: // L2WeaponType.ROD
				case 16: // L2WeaponType.CROSSBOW
				{
					if (getPaperdollItem(PAPERDOLL_LHAND) != null)
						setPaperdollItem(PAPERDOLL_LHAND, null);
					break;
				}
			}
		}
		
		public void notifyEquiped(int slot, L2ItemInstance item)
		{
			if (slot != PAPERDOLL_RHAND)
				return;
			if (Config.ASSERT)
				assert item == getPaperdollItem(PAPERDOLL_RHAND);
			
			switch (item.getItemType().hashCode())
			{
				case 5: // L2WeaponType.BOW
				{
					L2ItemInstance arrow = findArrowForBow(item.getItem());
					if (arrow != null)
						setPaperdollItem(PAPERDOLL_LHAND, arrow);
					break;
				}
				case 16: // L2WeaponType.CROSSBOW
				{
					L2ItemInstance arrow = findBoltForCrossbow(item.getItem());
					if (arrow != null)
						setPaperdollItem(PAPERDOLL_LHAND, arrow);
					break;
				}
					
			}
		}
	}
	
	final class StatsListener implements PaperdollListener
	{
		public void notifyUnequiped(int slot, L2ItemInstance item)
		{
			if (slot == PAPERDOLL_RHAND)
				return;
			getOwner().removeStatsOwner(item);
		}
		
		public void notifyEquiped(int slot, L2ItemInstance item)
		{
			if (slot == PAPERDOLL_RHAND)
				return;
			getOwner().addStatFuncs(item.getStatFuncs(getOwner()));
		}
	}
	
	final class ItemPassiveSkillsListener implements PaperdollListener
	{
		public void notifyUnequiped(int slot, L2ItemInstance item)
		{
			L2PcInstance player;
			
			if (getOwner() instanceof L2PcInstance)
			{
				player = (L2PcInstance) getOwner();
			}
			else
				return;
			
			L2Skill passiveSkill = null;
			L2Skill enchant4Skill = null;
			
			L2Item it = item.getItem();
			
			if (it instanceof L2Weapon)
			{
				passiveSkill = ((L2Weapon) it).getSkill();
				enchant4Skill = ((L2Weapon) it).getEnchant4Skill();
				if (it.getItemId() == 9140 || it.getItemId() == 9141)
				{
					player.removeSkill(SkillTable.getInstance().getInfo(3260, 1), false);
					player.removeSkill(SkillTable.getInstance().getInfo(3261, 1), false);
					player.removeSkill(SkillTable.getInstance().getInfo(3262, 1), false);
					player.sendSkillList();
				}
			}
			else if (it instanceof L2Armor)
				passiveSkill = ((L2Armor) it).getSkill();
			
			if (passiveSkill != null)
			{
				player.removeSkill(passiveSkill, false);
				player.sendSkillList();
			}
			if (enchant4Skill != null)
			{
				player.removeSkill(enchant4Skill, false);
				player.sendSkillList();
			}
			if (item.isAugmented())
				item.getAugmentation().removeBoni(player);
		}
		
		public void notifyEquiped(int slot, L2ItemInstance item)
		{
			L2PcInstance player;
			
			if (getOwner() instanceof L2PcInstance)
			{
				player = (L2PcInstance) getOwner();
			}
			else
				return;
			
			L2Skill passiveSkill = null;
			L2Skill enchant4Skill = null;
			
			L2Item it = item.getItem();
			
			if (it instanceof L2Weapon)
			{
				passiveSkill = ((L2Weapon) it).getSkill();
				
				if (item.getEnchantLevel() >= 4)
					enchant4Skill = ((L2Weapon) it).getEnchant4Skill();
				
				if (it.getItemId() == 9140 || it.getItemId() == 9141)
				{
					player.addSkill(SkillTable.getInstance().getInfo(3260, 1), false);
					player.addSkill(SkillTable.getInstance().getInfo(3261, 1), false);
					player.addSkill(SkillTable.getInstance().getInfo(3262, 1), false);
					player.sendSkillList();
				}
			}
			else if (it instanceof L2Armor)
				passiveSkill = ((L2Armor) it).getSkill();
			
			if (passiveSkill != null)
			{
				player.addSkill(passiveSkill, false);
				player.sendSkillList();
			}
			if (enchant4Skill != null)
			{
				player.addSkill(enchant4Skill, false);
				player.sendSkillList();
			}
			if (item.isAugmented())
				item.getAugmentation().applyBoni(player);
		}
	}
	
	final class ArmorSetListener implements PaperdollListener
	{
		public void notifyEquiped(int slot, L2ItemInstance item)
		{
			if (!(getOwner() instanceof L2PcInstance))
				return;
			
			L2PcInstance player = (L2PcInstance) getOwner();
			
			// checks if player worns chest item
			L2ItemInstance chestItem = getPaperdollItem(PAPERDOLL_CHEST);
			if (chestItem == null)
				return;
			
			// checks if there is armorset for chest item that player worns
			L2ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(chestItem.getItemId());
			if (armorSet == null)
				return;
			
			// checks if equiped item is part of set
			if (armorSet.containItem(slot, item.getItemId()))
			{
				if (armorSet.containAll(player))
				{
					L2Skill skill = SkillTable.getInstance().getInfo(armorSet.getSkillId(), 1);
					if (skill != null)
					{
						player.addSkill(skill, false);
						player.sendSkillList();
					}
					else
						_log.warn("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getSkillId() + ".");
					
					if (armorSet.containShield(player)) // has shield from set
					{
						L2Skill skills = SkillTable.getInstance().getInfo(armorSet.getShieldSkillId(), 1);
						if (skills != null)
						{
							player.addSkill(skills, false);
							player.sendSkillList();
						}
						else
							_log.warn("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getShieldSkillId() + ".");
					}
					if (armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
					{
						int skillId = armorSet.getEnchant6skillId();
						if (skillId > 0)
						{
							L2Skill skille = SkillTable.getInstance().getInfo(skillId, 1);
							if (skille != null)
							{
								player.addSkill(skille, false);
								player.sendSkillList();
							}
							else
								_log.warn("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getEnchant6skillId() + ".");
						}
					}
				}
			}
			else if (armorSet.containShield(item.getItemId()))
			{
				if (armorSet.containAll(player))
				{
					L2Skill skills = SkillTable.getInstance().getInfo(armorSet.getShieldSkillId(), 1);
					if (skills != null)
					{
						player.addSkill(skills, false);
						player.sendSkillList();
					}
					else
						_log.warn("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getShieldSkillId() + ".");
				}
			}
		}
		
		public void notifyUnequiped(int slot, L2ItemInstance item)
		{
			if (!(getOwner() instanceof L2PcInstance))
				return;
			
			L2PcInstance player = (L2PcInstance) getOwner();
			
			boolean remove = false;
			int removeSkillId1 = 0; // set skill
			int removeSkillId2 = 0; // shield skill
			int removeSkillId3 = 0; // enchant +6 skill
			
			if (slot == PAPERDOLL_CHEST)
			{
				L2ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(item.getItemId());
				if (armorSet == null)
					return;
				
				remove = true;
				removeSkillId1 = armorSet.getSkillId();
				removeSkillId2 = armorSet.getShieldSkillId();
				removeSkillId3 = armorSet.getEnchant6skillId();
			}
			else
			{
				L2ItemInstance chestItem = getPaperdollItem(PAPERDOLL_CHEST);
				if (chestItem == null)
					return;
				
				L2ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(chestItem.getItemId());
				if (armorSet == null)
					return;
				
				if (armorSet.containItem(slot, item.getItemId())) // removed part of set
				{
					remove = true;
					removeSkillId1 = armorSet.getSkillId();
					removeSkillId2 = armorSet.getShieldSkillId();
					removeSkillId3 = armorSet.getEnchant6skillId();
				}
				else if (armorSet.containShield(item.getItemId())) // removed shield
				{
					remove = true;
					removeSkillId2 = armorSet.getShieldSkillId();
				}
			}
			
			if (remove)
			{
				if (removeSkillId1 != 0)
				{
					L2Skill skill = SkillTable.getInstance().getInfo(removeSkillId1, 1);
					if (skill != null)
						player.removeSkill(skill);
					else
						_log.warn("Inventory.ArmorSetListener: Incorrect skill: " + removeSkillId1 + ".");
				}
				if (removeSkillId2 != 0)
				{
					L2Skill skill = SkillTable.getInstance().getInfo(removeSkillId2, 1);
					if (skill != null)
						player.removeSkill(skill);
					else
						_log.warn("Inventory.ArmorSetListener: Incorrect skill: " + removeSkillId2 + ".");
				}
				if (removeSkillId3 != 0)
				{
					L2Skill skill = SkillTable.getInstance().getInfo(removeSkillId3, 1);
					if (skill != null)
						player.removeSkill(skill);
					else
						_log.warn("Inventory.ArmorSetListener: Incorrect skill: " + removeSkillId3 + ".");
				}
				player.sendSkillList();
			}
		}
	}
	
	final class FormalWearListener implements PaperdollListener
	{
		public void notifyUnequiped(int slot, L2ItemInstance item)
		{
			if (!(getOwner() != null && getOwner() instanceof L2PcInstance))
				return;
			
			L2PcInstance owner = (L2PcInstance) getOwner();
			
			if (item.getItemId() == 6408)
				owner.setIsWearingFormalWear(false);
		}
		
		public void notifyEquiped(int slot, L2ItemInstance item)
		{
			if (!(getOwner() != null && getOwner() instanceof L2PcInstance))
				return;
			
			L2PcInstance owner = (L2PcInstance) getOwner();
			
			// If player equip Formal Wear unequip weapons and abort cast/attack
			if (item.getItemId() == 6408)
			{
				owner.setIsWearingFormalWear(true);
			}
			else
			{
				if (!owner.isWearingFormalWear())
					return;
			}
		}
	}
	
	/**
	 * Constructor of the inventory
	 */
	protected Inventory()
	{
		_paperdoll = new L2ItemInstance[0x13];
		_paperdollListeners = new FastList<PaperdollListener>();
		addPaperdollListener(new AmmunationListener());
		addPaperdollListener(new StatsListener());
		addPaperdollListener(new ItemPassiveSkillsListener());
		addPaperdollListener(new ArmorSetListener());
		addPaperdollListener(new FormalWearListener());
	}
	
	protected abstract ItemLocation getEquipLocation();
	
	/**
	 * Update inventory and notify the view
	 * 
	 * @param item
	 *            the item added in inventory
	 * @param count
	 *            the amount of item added
	 * @param a
	 *            status update packed
	 */
	public abstract void updateInventory(L2ItemInstance item);
	
	/**
	 * Returns the instance of new ChangeRecorder
	 * 
	 * @return ChangeRecorder
	 */
	public ChangeRecorder newRecorder()
	{
		return new ChangeRecorder(this);
	}
	
	/**
	 * Drop item from inventory and updates database
	 * 
	 * @param process :
	 *            String Identifier of process triggering this action
	 * @param item :
	 *            L2ItemInstance to be dropped
	 * @param actor :
	 *            L2PcInstance Player requesting the item drop
	 * @param reference :
	 *            L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public L2ItemInstance dropItem(String process, L2ItemInstance item, L2PcInstance actor, L2Object reference)
	{
		if (!_items.contains(item))
			return null;
		
		// If we drop the whole stack we have to log it here
		if (actor.isGM())
		{
			// DaDummy: this way we log _every_ gmdrop with all related info
			String command = process;
			String params = "(" + String.valueOf(actor.getX()) + "," + String.valueOf(actor.getY()) + "," + String.valueOf(actor.getZ()) + ") - "
					+ String.valueOf(item.getCount()) + " - " + String.valueOf(item.getEnchantLevel()) + " - " + String.valueOf(item.getItemId()) + " - "
					+ item.getItemName();
			
			GMAudit.auditGMAction(actor, "dropitem", command, params);
		}
		
		synchronized (item)
		{
			if (!_items.contains(item)) { return null; }
			
			removeItem(item);
			item.setOwnerId(process, 0, actor, reference);
			item.setLocation(ItemLocation.VOID);
			item.setLastChange(L2ItemInstance.REMOVED);
			
			item.updateDatabase();
			refreshWeight();
		}
		return item;
	}
	
	/**
	 * Drop item from inventory by using its <B>objectID</B> and updates database
	 * 
	 * @param process :
	 *            String Identifier of process triggering this action
	 * @param objectId :
	 *            int Item Instance identifier of the item to be dropped
	 * @param count :
	 *            int Quantity of items to be dropped
	 * @param actor :
	 *            L2PcInstance Player requesting the item drop
	 * @param reference :
	 *            L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public L2ItemInstance dropItem(String process, int objectId, int count, L2PcInstance actor, L2Object reference)
	{
		L2ItemInstance item = getItemByObjectId(objectId);
		if (item == null)
			return null;
		
		// Adjust item quantity and create new instance to drop
		if (item.getCount() > count)
		{
			// If we do a partial drop we have to log it here
			if (actor.isGM())
			{
				// DaDummy: this way we log _every_ gmdrop with all related info
				String command = process;
				String params = "(" + String.valueOf(actor.getX()) + "," + String.valueOf(actor.getY()) + "," + String.valueOf(actor.getZ()) + ") - "
						+ String.valueOf(count) + " - " + String.valueOf(item.getEnchantLevel()) + " - " + String.valueOf(item.getItemId()) + " - "
						+ item.getItemName();
				
				GMAudit.auditGMAction(actor, "dropitem", command, params);
			}
			
			item.changeCount(process, -count, actor, reference);
			item.setLastChange(L2ItemInstance.MODIFIED);
			item.updateDatabase();
			
			item = ItemTable.getInstance().createItem(process, item.getItemId(), count, actor, reference);
			
			item.updateDatabase();
			refreshWeight();
			return item;
		}
		// Directly drop entire item
		else
			return dropItem(process, item, actor, reference);
	}
	
	/**
	 * Adds item to inventory for further adjustments and Equip it if necessary (itemlocation defined)<BR>
	 * <BR>
	 * 
	 * @param item :
	 *            L2ItemInstance to be added from inventory
	 */
	@Override
	protected void addItem(L2ItemInstance item)
	{
		super.addItem(item);
		if (item.isEquipped())
			equipItem(item);
	}
	
	/**
	 * Removes item from inventory for further adjustments.
	 * 
	 * @param item :
	 *            L2ItemInstance to be removed from inventory
	 */
	@Override
	protected void removeItem(L2ItemInstance item)
	{
		// Unequip item if equiped
		// if (item.isEquipped()) unEquipItemInSlotAndRecord(item.getEquipSlot());
		for (int i = 0; i < _paperdoll.length; i++)
			if (_paperdoll[i] == item)
				unEquipItemInSlot(i);
		
		super.removeItem(item);
	}
	
	/**
	 * Returns the item in the paperdoll slot
	 * 
	 * @return L2ItemInstance
	 */
	public L2ItemInstance getPaperdollItem(int slot)
	{
		return _paperdoll[slot];
	}
	
	/**
	 * Returns the item in the paperdoll L2Item slot
	 * 
	 * @param L2Item
	 *            slot identifier
	 * @return L2ItemInstance
	 */
	public L2ItemInstance getPaperdollItemByL2ItemId(int slot)
	{
		switch (slot)
		{
			case 0x01:
				return _paperdoll[0];
			case 0x04:
				return _paperdoll[1];
			case 0x02:
				return _paperdoll[2];
			case 0x08:
				return _paperdoll[3];
			case 0x20:
				return _paperdoll[4];
			case 0x10:
				return _paperdoll[5];
			case 0x40:
				return _paperdoll[6];
			case 0x80:
				return _paperdoll[7];
			case 0x0100:
				return _paperdoll[8];
			case 0x0200:
				return _paperdoll[9];
			case 0x0400:
				return _paperdoll[10];
			case 0x0800:
				return _paperdoll[11];
			case 0x1000:
				return _paperdoll[12];
			case 0x2000:
				return _paperdoll[13];
			case 0x4000:
				return _paperdoll[14];
			case 0x040000:
				return _paperdoll[15];
			case 0x010000:
				return _paperdoll[16];
			case 0x080000:
				return _paperdoll[17];
		}
		return null;
	}
	
	/**
	 * Returns the ID of the item in the paperdol slot
	 * 
	 * @param slot :
	 *            int designating the slot
	 * @return int designating the ID of the item
	 */
	public int getPaperdollItemId(int slot)
	{
		L2ItemInstance item = _paperdoll[slot];
		if (item != null)
			return item.getItemId();
		else if (slot == PAPERDOLL_HAIR)
		{
			item = _paperdoll[PAPERDOLL_HAIR2];
			if (item != null)
				return item.getItemId();
		}
		return 0;
	}
	
	/**
	 * Returns the ID of the item in the paperdol slot
	 * 
	 * @param slot :
	 *            int designating the slot
	 * @return int designating the ID of the item
	 */
	public int getPaperdollItemDisplayId(int slot)
	{
		L2ItemInstance item = _paperdoll[slot];
		if (item != null)
			return item.getItemDisplayId();
		else if (slot == PAPERDOLL_HAIR)
		{
			item = _paperdoll[PAPERDOLL_HAIR2];
			if (item != null)
				return item.getItemDisplayId();
		}
		return 0;
	}
	
	public int getPaperdollAugmentationId(int slot)
	{
		L2ItemInstance item = _paperdoll[slot];
		if (item != null)
		{
			if (item.getAugmentation() != null)
			{
				return item.getAugmentation().getAugmentationId();
			}
			else
			{
				return 0;
			}
		}
		return 0;
	}
	
	/**
	 * Returns the objectID associated to the item in the paperdoll slot
	 * 
	 * @param slot :
	 *            int pointing out the slot
	 * @return int designating the objectID
	 */
	public int getPaperdollObjectId(int slot)
	{
		L2ItemInstance item = _paperdoll[slot];
		if (item != null)
			return item.getObjectId();
		else if (slot == PAPERDOLL_HAIR)
		{
			item = _paperdoll[PAPERDOLL_HAIR2];
			if (item != null)
				return item.getObjectId();
		}
		return 0;
	}
	
	/**
	 * Adds new inventory's paperdoll listener
	 * 
	 * @param PaperdollListener
	 *            pointing out the listener
	 */
	public synchronized void addPaperdollListener(PaperdollListener listener)
	{
		if (Config.ASSERT)
			assert !_paperdollListeners.contains(listener);
		_paperdollListeners.add(listener);
	}
	
	/**
	 * Removes a paperdoll listener
	 * 
	 * @param PaperdollListener
	 *            pointing out the listener to be deleted
	 */
	public synchronized void removePaperdollListener(PaperdollListener listener)
	{
		_paperdollListeners.remove(listener);
	}
	
	/**
	 * Equips an item in the given slot of the paperdoll. <U><I>Remark :</I></U> The item <B>HAS TO BE</B> already in the inventory
	 * 
	 * @param slot :
	 *            int pointing out the slot of the paperdoll
	 * @param item :
	 *            L2ItemInstance pointing out the item to add in slot
	 * @return L2ItemInstance designating the item placed in the slot before
	 */
	public L2ItemInstance setPaperdollItem(int slot, L2ItemInstance item)
	{
		L2ItemInstance old = _paperdoll[slot];
		if (old != item)
		{
			if (old != null)
			{
				_paperdoll[slot] = null;
				// Put old item from paperdoll slot to base location
				old.setLocation(getBaseLocation());
				old.setLastChange(L2ItemInstance.MODIFIED);
				for (Iterator iterator = _paperdollListeners.iterator(); iterator.hasNext();)
				{
					PaperdollListener listener = (PaperdollListener) iterator.next();
					if (listener != null)
						listener.notifyUnequiped(slot, old);
				}
				old.updateDatabase();
			}
			// Add new item in slot of paperdoll
			if (item != null)
			{
				_paperdoll[slot] = item;
				item.setLocation(getEquipLocation(), slot);
				item.setLastChange(L2ItemInstance.MODIFIED);
				PaperdollListener listener;
				for (Iterator iterator1 = _paperdollListeners.iterator(); iterator1.hasNext(); listener.notifyEquiped(slot, item))
					listener = (PaperdollListener) iterator1.next();
				
				item.updateDatabase();
			}
		}
		return old;
	}
	
	/**
	 * Return the mask of weared item
	 * 
	 * @return int
	 */
	public int getWearedMask()
	{
		return _wearedMask;
	}
	
	/**
	 * Unequips item in body slot and returns alterations.
	 * 
	 * @param slot :
	 *            int designating the slot of the paperdoll
	 * @return L2ItemInstance[] : list of changes
	 */
	public synchronized L2ItemInstance[] unEquipItemInBodySlotAndRecord(int slot)
	{
		Inventory.ChangeRecorder recorder = newRecorder();
		try
		{
			unEquipItemInBodySlot(slot);
			if (getOwner() instanceof L2PcInstance)
				((L2PcInstance) getOwner()).refreshExpertisePenalty();
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Sets item in slot of the paperdoll to null value
	 * 
	 * @param pdollSlot :
	 *            int designating the slot
	 * @return L2ItemInstance designating the item in slot before change
	 */
	public synchronized L2ItemInstance unEquipItemInSlot(int pdollSlot)
	{
		return setPaperdollItem(pdollSlot, null);
	}
	
	/**
	 * Unepquips item in slot and returns alterations
	 * 
	 * @param slot :
	 *            int designating the slot
	 * @return L2ItemInstance[] : list of items altered
	 */
	public synchronized L2ItemInstance[] unEquipItemInSlotAndRecord(int slot)
	{
		Inventory.ChangeRecorder recorder = newRecorder();
		try
		{
			unEquipItemInSlot(slot);
			if (getOwner() instanceof L2PcInstance)
				((L2PcInstance) getOwner()).refreshExpertisePenalty();
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Unequips item in slot (i.e. equips with default value)
	 * 
	 * @param slot :
	 *            int designating the slot
	 */
	private void unEquipItemInBodySlot(int slot)
	{
		if (_log.isDebugEnabled())
			_log.debug("--- unequip body slot:" + slot);
		int pdollSlot = -1;
		
		switch (slot)
		{
			case L2Item.SLOT_L_EAR:
				pdollSlot = PAPERDOLL_LEAR;
				break;
			case L2Item.SLOT_R_EAR:
				pdollSlot = PAPERDOLL_REAR;
				break;
			case L2Item.SLOT_NECK:
				pdollSlot = PAPERDOLL_NECK;
				break;
			case L2Item.SLOT_R_FINGER:
				pdollSlot = PAPERDOLL_RFINGER;
				break;
			case L2Item.SLOT_L_FINGER:
				pdollSlot = PAPERDOLL_LFINGER;
				break;
			case L2Item.SLOT_HAIR:
				pdollSlot = PAPERDOLL_HAIR;
				break;
			case L2Item.SLOT_HAIR2:
				pdollSlot = PAPERDOLL_HAIR2;
				break;
			case L2Item.SLOT_HAIRALL:
				setPaperdollItem(PAPERDOLL_HAIR, null);
				setPaperdollItem(PAPERDOLL_HAIR2, null);
				pdollSlot = PAPERDOLL_HAIR;
				break;
			case L2Item.SLOT_HEAD:
				pdollSlot = PAPERDOLL_HEAD;
				break;
			case L2Item.SLOT_R_HAND:
				pdollSlot = PAPERDOLL_RHAND;
				break;
			case L2Item.SLOT_L_HAND:
				pdollSlot = PAPERDOLL_LHAND;
				break;
			case L2Item.SLOT_GLOVES:
				pdollSlot = PAPERDOLL_GLOVES;
				break;
			case L2Item.SLOT_LEGS:
				pdollSlot = PAPERDOLL_LEGS;
				break;
			
			case L2Item.SLOT_FULL_ARMOR:
			case L2Item.SLOT_CHEST:
			case L2Item.SLOT_ALLDRESS:
				pdollSlot = PAPERDOLL_CHEST;
				break;
			
			case L2Item.SLOT_BACK:
				pdollSlot = PAPERDOLL_UNDER;
				break;
			
			case L2Item.SLOT_FEET:
				pdollSlot = PAPERDOLL_FEET;
				break;
			case L2Item.SLOT_UNDERWEAR:
				pdollSlot = PAPERDOLL_UNDER;
				break;
			
			case L2Item.SLOT_LR_HAND:
				setPaperdollItem(PAPERDOLL_LHAND, null);
				setPaperdollItem(PAPERDOLL_RHAND, null);// this should be the same as in LRHAND
				pdollSlot = PAPERDOLL_RHAND;
				break;
		}
		if (pdollSlot >= 0)
			setPaperdollItem(pdollSlot, null);
	}
	
	/**
	 * Equips item and returns list of alterations
	 * 
	 * @param item :
	 *            L2ItemInstance corresponding to the item
	 * @return L2ItemInstance[] : list of alterations
	 */
	public L2ItemInstance[] equipItemAndRecord(L2ItemInstance item)
	{
		Inventory.ChangeRecorder recorder = newRecorder();
		
		try
		{
			equipItem(item);
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
		
	}
	
	/**
	 * Equips item in slot of paperdoll.
	 * 
	 * @param item :
	 *            L2ItemInstance designating the item and slot used.
	 */
	public synchronized void equipItem(L2ItemInstance item)
	{
		if ((getOwner() instanceof L2PcInstance) && ((L2PcInstance) getOwner()).getPrivateStoreType() != 0)
			return;
		
		if (getOwner() instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) getOwner();
			
			if (Config.ALT_STRICT_HERO_SYSTEM && !player.isGM() && !player.isHero())
			{
				if (item.isHeroitem()) { return; }
			}
		}
		
		int targetSlot = item.getItem().getBodyPart();
		switch (targetSlot)
		{
			case L2Item.SLOT_LR_HAND:
			{
				L2ItemInstance slot = getPaperdollItem(7);
				if (slot != null)
					setPaperdollItem(PAPERDOLL_LHAND, null);
				setPaperdollItem(PAPERDOLL_RHAND, item);
				if (item.getItem().getItemType() == L2WeaponType.BOW)
				{
					L2ItemInstance arrow = findArrowForBow(item.getItem());
					if (arrow != null)
						setPaperdollItem(PAPERDOLL_LHAND, arrow);
				}
				if (item.getItem().getItemType() == L2WeaponType.CROSSBOW)
				{
					L2ItemInstance bold = findBoltForCrossbow(item.getItem());
					if (bold != null)
						setPaperdollItem(PAPERDOLL_LHAND, bold);
				}
				break;
			}
			case L2Item.SLOT_L_HAND:
			{
				if (!(item.getItem() instanceof L2EtcItem) || item.getItem().getItemType() != L2EtcItemType.ARROW
						|| item.getItem().getItemType() != L2EtcItemType.BOLT)
				{
					L2ItemInstance slot = getPaperdollItem(PAPERDOLL_RHAND);
					if (slot != null && slot.getItem().getBodyPart() == L2Item.SLOT_LR_HAND)
					{
						setPaperdollItem(PAPERDOLL_RHAND, null);
						setPaperdollItem(PAPERDOLL_LHAND, null);
					}
					else
					{
						setPaperdollItem(PAPERDOLL_LHAND, null);
					}
				}
				setPaperdollItem(PAPERDOLL_LHAND, item);
				break;
			}
			case L2Item.SLOT_R_HAND:
			{
				L2ItemInstance slot = getPaperdollItem(5);
				if (slot != null && slot.getItem().getBodyPart() == L2Item.SLOT_LR_HAND)
				{
					setPaperdollItem(PAPERDOLL_RHAND, null);
					setPaperdollItem(PAPERDOLL_LHAND, null);
				}
				else
				{
					setPaperdollItem(PAPERDOLL_RHAND, null);
				}
				setPaperdollItem(PAPERDOLL_RHAND, item);
				break;
			}
				
			case L2Item.SLOT_L_EAR | L2Item.SLOT_R_EAR | L2Item.SLOT_LR_EAR:
			{
				if (_paperdoll[PAPERDOLL_LEAR] == null)
				{
					setPaperdollItem(PAPERDOLL_LEAR, item);
				}
				else if (_paperdoll[PAPERDOLL_REAR] == null)
				{
					setPaperdollItem(PAPERDOLL_REAR, item);
				}
				else
				{
					setPaperdollItem(PAPERDOLL_LEAR, null);
					setPaperdollItem(PAPERDOLL_LEAR, item);
				}
				
				break;
			}
			case L2Item.SLOT_L_FINGER | L2Item.SLOT_R_FINGER | L2Item.SLOT_LR_FINGER:
			{
				if (_paperdoll[PAPERDOLL_LFINGER] == null)
				{
					setPaperdollItem(PAPERDOLL_LFINGER, item);
				}
				else if (_paperdoll[PAPERDOLL_RFINGER] == null)
				{
					setPaperdollItem(PAPERDOLL_RFINGER, item);
				}
				else
				{
					setPaperdollItem(PAPERDOLL_LFINGER, null);
					setPaperdollItem(PAPERDOLL_LFINGER, item);
				}
				break;
			}
			case L2Item.SLOT_NECK:
				setPaperdollItem(PAPERDOLL_NECK, item);
				break;
			case L2Item.SLOT_FULL_ARMOR:
				setPaperdollItem(PAPERDOLL_CHEST, null);
				setPaperdollItem(PAPERDOLL_LEGS, null);
				setPaperdollItem(PAPERDOLL_CHEST, item);
				break;
			case L2Item.SLOT_CHEST:
				setPaperdollItem(PAPERDOLL_CHEST, item);
				break;
			case L2Item.SLOT_LEGS:
			{
				// handle full armor
				L2ItemInstance chest = getPaperdollItem(PAPERDOLL_CHEST);
				if (chest != null && chest.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR)
				{
					setPaperdollItem(PAPERDOLL_CHEST, null);
				}
				
				setPaperdollItem(PAPERDOLL_LEGS, null);
				setPaperdollItem(PAPERDOLL_LEGS, item);
				break;
			}
			case L2Item.SLOT_FEET:
				setPaperdollItem(PAPERDOLL_FEET, item);
				break;
			case L2Item.SLOT_GLOVES:
				setPaperdollItem(PAPERDOLL_GLOVES, item);
				break;
			case L2Item.SLOT_HEAD:
				setPaperdollItem(PAPERDOLL_HEAD, item);
				break;
			case L2Item.SLOT_HAIR:
				L2ItemInstance slot = getPaperdollItem(PAPERDOLL_HAIR2);
				if (slot != null && slot.getItem().getBodyPart() == L2Item.SLOT_HAIRALL)
				{
					setPaperdollItem(PAPERDOLL_HAIR, null);
					setPaperdollItem(PAPERDOLL_HAIR2, null);
				}
				setPaperdollItem(PAPERDOLL_HAIR, item);
				break;
			case L2Item.SLOT_HAIR2:
				L2ItemInstance slot2 = getPaperdollItem(PAPERDOLL_HAIR2);
				if (slot2 != null && slot2.getItem().getBodyPart() == L2Item.SLOT_HAIRALL)
				{
					setPaperdollItem(PAPERDOLL_HAIR, null);
					setPaperdollItem(PAPERDOLL_HAIR2, null);
				}
				setPaperdollItem(PAPERDOLL_HAIR2, item);
				break;
			case L2Item.SLOT_HAIRALL:
				setPaperdollItem(PAPERDOLL_HAIR, null);
				setPaperdollItem(PAPERDOLL_HAIR2, null);
				setPaperdollItem(PAPERDOLL_HAIR2, item);
				break;
			case L2Item.SLOT_R_BRACELET:
				setPaperdollItem(PAPERDOLL_RBRACELET, null);
				setPaperdollItem(PAPERDOLL_RBRACELET, item);
				break;
			case L2Item.SLOT_L_BRACELET:
				setPaperdollItem(PAPERDOLL_LBRACELET, null);
				setPaperdollItem(PAPERDOLL_LBRACELET, item);
				break;
			case L2Item.SLOT_UNDERWEAR:
			case L2Item.SLOT_BACK:
				setPaperdollItem(PAPERDOLL_UNDER, item);
				break;
			case L2Item.SLOT_DECO:
				if (_paperdoll[PAPERDOLL_DECO1] == null)
				{
					setPaperdollItem(PAPERDOLL_DECO1, item);
					break;
				}
				if (_paperdoll[PAPERDOLL_DECO2] == null)
				{
					setPaperdollItem(PAPERDOLL_DECO2, item);
					break;
				}
				if (_paperdoll[PAPERDOLL_DECO3] == null)
				{
					setPaperdollItem(PAPERDOLL_DECO3, item);
					break;
				}
				if (_paperdoll[PAPERDOLL_DECO4] == null)
				{
					setPaperdollItem(PAPERDOLL_DECO4, item);
					break;
				}
				if (_paperdoll[PAPERDOLL_DECO5] == null)
				{
					setPaperdollItem(PAPERDOLL_DECO5, item);
					break;
				}
				if (_paperdoll[PAPERDOLL_DECO6] == null)
					setPaperdollItem(PAPERDOLL_DECO6, item);
				else
					setPaperdollItem(PAPERDOLL_DECO1, item);
				break;
			default:
				_log.warn("unknown body slot:" + targetSlot);
		}
	}
	
	/**
	 * Refresh the weight of equipment loaded
	 */
	@Override
	protected void refreshWeight()
	{
		int weight = 0;
		for (L2ItemInstance item : _items)
		{
			if (item != null && item.getItem() != null)
				weight += item.getItem().getWeight() * item.getCount();
		}
		
		_totalWeight = weight;
	}
	
	/**
	 * Returns the totalWeight.
	 * 
	 * @return int
	 */
	public int getTotalWeight()
	{
		return _totalWeight;
	}
	
	/**
	 * Return the L2ItemInstance of the arrows needed for this bow.<BR>
	 * <BR>
	 * 
	 * @param bow :
	 *            L2Item designating the bow
	 * @return L2ItemInstance pointing out arrows for bow
	 */
	public L2ItemInstance findArrowForBow(L2Item bow)
	{
		int arrowsId = 0;
		
		switch (bow.getCrystalType())
		{
			default: // broken weapon.csv ??
			case L2Item.CRYSTAL_NONE:
				arrowsId = 17;
				break; // Wooden arrow
			case L2Item.CRYSTAL_D:
				arrowsId = 1341;
				break; // Bone arrow
			case L2Item.CRYSTAL_C:
				arrowsId = 1342;
				break; // Fine steel arrow
			case L2Item.CRYSTAL_B:
				arrowsId = 1343;
				break; // Silver arrow
			case L2Item.CRYSTAL_A:
				arrowsId = 1344;
				break; // Mithril arrow
			case L2Item.CRYSTAL_S:
				arrowsId = 1345;
				break; // Shining arrow
		}
		
		// Get the L2ItemInstance corresponding to the item identifier and return it
		return getItemByItemId(arrowsId);
	}
	
	public L2ItemInstance findBoltForCrossbow(L2Item crossbow)
	{
		int arrowsId = 0;
		
		switch (crossbow.getCrystalType())
		{
			default:
			case L2Item.CRYSTAL_NONE:
				arrowsId = 9632;
				break;
			case L2Item.CRYSTAL_D:
				arrowsId = 9633;
				break;
			case L2Item.CRYSTAL_C:
				arrowsId = 9634;
				break;
			case L2Item.CRYSTAL_B:
				arrowsId = 9635;
				break;
			case L2Item.CRYSTAL_A:
				arrowsId = 9636;
				break;
			case L2Item.CRYSTAL_S:
				arrowsId = 9637;
				break;
		}
		return getItemByItemId(arrowsId);
	}
	
	public void restoreArmorSetPassiveSkill()
	{
		if (!(getOwner() instanceof L2PcInstance))
			return;
		L2PcInstance player = (L2PcInstance) getOwner();
		
		L2ItemInstance chestItem = getPaperdollItem(PAPERDOLL_CHEST);
		if (chestItem == null)
			return;
		
		L2ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(chestItem.getItemId());
		if (armorSet == null)
			return;
		
		if (armorSet.containAll(player))
		{
			L2Skill skill = SkillTable.getInstance().getInfo(armorSet.getSkillId(), 1);
			if (skill != null)
				((L2PcInstance) getOwner()).addSkill(skill, false);
			else
				_log.warn("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getSkillId() + ".");
			
			if (armorSet.containShield(player))
			{
				L2Skill skills = SkillTable.getInstance().getInfo(armorSet.getShieldSkillId(), 1);
				if (skills != null)
					player.addSkill(skills, false);
				else
					_log.warn("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getShieldSkillId() + ".");
			}
			if (armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
			{
				int skillId = armorSet.getEnchant6skillId();
				if (skillId > 0)
				{
					L2Skill skille = SkillTable.getInstance().getInfo(skillId, 1);
					if (skille != null)
						player.addSkill(skille, false);
					else
						_log.warn("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getEnchant6skillId() + ".");
				}
			}
		}
	}
	
	public void restoreEquipedItemsPassiveSkill()
	{
		if (!(getOwner() instanceof L2PcInstance))
			return;
		for (int i = 0; i < 19; i++)
		{
			if (getPaperdollItem(i) == null)
				continue;
			_paperdollListeners.get(2).notifyEquiped(i, getPaperdollItem(i)); // hardrcoded for ItemPassiveSkillsListener
		}
	}
	
	/**
	 * Get back items in inventory from database
	 */
	@Override
	public void restore()
	{
		java.sql.Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);
			PreparedStatement statement = con
					.prepareStatement("SELECT object_id FROM items WHERE owner_id=? AND (loc=? OR loc=?) " + "ORDER BY object_id DESC");
			statement.setInt(1, getOwner().getObjectId());
			statement.setString(2, getBaseLocation().name());
			statement.setString(3, getEquipLocation().name());
			ResultSet inv = statement.executeQuery();
			
			L2ItemInstance item;
			
			int itemCount = 0, maxItems = 100;
			boolean warnMessage = true;
			
			if (getOwner() instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) getOwner();
				maxItems = (player.getAccessLevel() > 0) ? Config.INVENTORY_MAXIMUM_GM : (player.getRace() == Race.dwarf) ? Config.INVENTORY_MAXIMUM_DWARF
						: Config.INVENTORY_MAXIMUM_NO_DWARF;
			}
			
			while (inv.next())
			{
				int objectId = inv.getInt(1);
				item = L2ItemInstance.restoreFromDb(objectId);
				if (item == null)
					continue;
				
				itemCount++;
				
				if (getOwner() instanceof L2PcInstance)
				{
					L2PcInstance player = (L2PcInstance) getOwner();
					
					if (!player.isGM() && Config.ALT_STRICT_HERO_SYSTEM && !player.isHero() && item.isHeroitem())
						item.setLocation(ItemLocation.INVENTORY);
					
					if (maxItems < itemCount)
					{
						
						if (!item.isStackable())
						{
							if (warnMessage)
								player.sendMessage("Could not restore all items! Items will be restored when you do not exceed inventory quantity.");
							warnMessage = false;
							continue;
						}
						
						else if (getItemByItemId(item.getItemId()) == null)
						{
							if (warnMessage)
								player.sendMessage("Could not restore all items! Items will be restored when you do not exceed inventory quantity.");
							warnMessage = false;
							continue;
						}
						
					}
				}
				
				L2World.getInstance().storeObject(item);
				
				// If stackable item is found in inventory just add to current quantity
				if (item.isStackable() && getItemByItemId(item.getItemId()) != null)
				{
					addItem("Restore", item, null, getOwner());
					itemCount--;
				}
				else
					addItem(item);
			}
			
			inv.close();
			statement.close();
			refreshWeight();
			restoreEquipedItemsPassiveSkill();
			restoreArmorSetPassiveSkill();
		}
		catch (Exception e)
		{
			_log.warn("Could not restore inventory : " + e);
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (Exception e)
			{
			}
		}
	}
}
