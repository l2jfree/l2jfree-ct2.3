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
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.clientpackets;

import java.nio.ByteBuffer;

import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.serverpackets.PledgeShowMemberListAll;

/**
 * This class ...
 * 
 * @version $Revision: 1.5.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPledgeMemberList extends ClientBasePacket
{
	private static final String _C__3C_REQUESTPLEDGEMEMBERLIST = "[C] 3C RequestPledgeMemberList";
	//private final static Log _log = LogFactory.getLog(RequestPledgeMemberList.class.getName());

	/**
	 * packet type id 0x3c
	 * format:		c
	 * @param rawPacket
	 */
	public RequestPledgeMemberList(ByteBuffer buf, L2GameClient client)
	{
		super(buf, client);
	}

	void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null) return;
		
		L2Clan clan = activeChar.getClan();
		if (clan != null)
		{
			PledgeShowMemberListAll pm = new PledgeShowMemberListAll(clan, activeChar);
			activeChar.sendPacket(pm);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	public String getType()
	{
		return _C__3C_REQUESTPLEDGEMEMBERLIST;
	}
}
