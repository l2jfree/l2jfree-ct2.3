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
package com.l2jfree.gameserver.skills.conditions;

import com.l2jfree.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfree.gameserver.skills.Env;

class ConditionTargetNpcType extends Condition
{
	private final String[] _npcTypes;
	
	public ConditionTargetNpcType(String[] npcTypes)
	{
		_npcTypes = npcTypes;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (!(env.target instanceof L2NpcInstance))
			return false;
		
		String type = ((L2NpcInstance)env.target).getTemplate().getType();
		
		for (String npcType : _npcTypes)
			if (npcType.equalsIgnoreCase(type))
				return true;
		
		return false;
	}
}