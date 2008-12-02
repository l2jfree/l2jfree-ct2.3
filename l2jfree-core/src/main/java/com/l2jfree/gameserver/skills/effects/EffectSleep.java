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
package com.l2jfree.gameserver.skills.effects;

import com.l2jfree.gameserver.model.L2Effect;
import com.l2jfree.gameserver.skills.Env;
import com.l2jfree.gameserver.templates.L2EffectType;

/**
 * @author mkizub
 */
public final class EffectSleep extends L2Effect
{

	public EffectSleep(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.SLEEP;
	}

	/** Notify started */
	@Override
	public boolean onStart()
	{
		getEffected().startSleeping();
		return true;
	}

	/** Notify exited */
	@Override
	public void onExit()
	{
		getEffected().stopSleeping(this);
	}

	@Override
	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
}
