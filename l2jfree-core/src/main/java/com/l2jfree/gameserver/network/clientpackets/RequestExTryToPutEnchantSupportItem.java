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
package com.l2jfree.gameserver.network.clientpackets;

/** 
 * @author evill33t
 * 
 */
public class RequestExTryToPutEnchantSupportItem extends L2GameClientPacket
{
	private static final String	_C__D0_80_REQUESTEXTRYTOPUTENCHANTSUPPORTITEM	= "[C] D0 50 RequestExTryToPutEnchantSupportItem";

	@SuppressWarnings("unused")
	private int					_unk1;
	@SuppressWarnings("unused")
	private int					_unk2;

	@Override
	protected void readImpl()
	{
		_unk1 = readD();
		_unk2 = readD();
	}

	@Override
	protected void runImpl()
	{
		//TODO: Implementation RequestExTryToPutEnchantSupportItem
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__D0_80_REQUESTEXTRYTOPUTENCHANTSUPPORTITEM;
	}
}
