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

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ClientThread;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.communitybbs.CommunityBoard;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2CharPosition;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.CTF;
import net.sf.l2j.gameserver.model.entity.L2Event;
import net.sf.l2j.gameserver.script.stat.LeaderboardEngine;
import net.sf.l2j.gameserver.model.entity.TvT;
import net.sf.l2j.gameserver.model.entity.VIP;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * 
 * @version $Revision: 1.12.4.5 $ $Date: 2005/04/11 10:06:11 $
 */
public class RequestBypassToServer extends ClientBasePacket
{
    private static final String _C__21_REQUESTBYPASSTOSERVER = "[C] 21 RequestBypassToServer";
    private final static Log _log = LogFactory.getLog(RequestBypassToServer.class.getName());

    // S
    private final String _command;

    /**
     * @param decrypt
     */
    public RequestBypassToServer(ByteBuffer buf, ClientThread client)
    {
        super(buf, client);
        _command = readS();
    }

    void runImpl()
    {
        L2PcInstance activeChar = getClient().getActiveChar();
        
        if (activeChar == null)
            return;
        
        try {
            if (_command.startsWith("admin_")) //&& activeChar.getAccessLevel() >= Config.GM_ACCESSLEVEL)
            {
                if (Config.ALT_PRIVILEGES_ADMIN && !AdminCommandHandler.getInstance().checkPrivileges(activeChar, _command))
                {
                    _log.info("<GM>" + activeChar + " does not have sufficient privileges for command '" + _command + "'.");
                    return;
                }
                
                IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(_command);
                
                if (ach != null)
                    ach.useAdminCommand(_command, activeChar);
                else
                    _log.warn("No handler registered for bypass '"+_command+"'");
            }
            else if (_command.equals("come_here") && activeChar.getAccessLevel() >= Config.GM_ACCESSLEVEL)
            {
                comeHere(activeChar);
            }
            else if (_command.startsWith("player_help "))
            {
                playerHelp(activeChar, _command.substring(12));
            }
            else if (_command.startsWith("npc_"))
            {
                if(!activeChar.validateBypass(_command))
                    return;

                int endOfId = _command.indexOf('_', 5);
                String id;
                if (endOfId > 0)
                    id = _command.substring(4, endOfId);
                else
                    id = _command.substring(4);
                try
                {
                    L2Object object = L2World.getInstance().findObject(Integer.parseInt(id));
                    if (_command.substring(endOfId+1).startsWith("event_participate")) L2Event.inscribePlayer(activeChar);

                    if (_command.substring(endOfId+1).startsWith("vip_joinVIPTeam"))
                        VIP.addPlayerVIP(activeChar);
                   
                    if (_command.substring(endOfId+1).startsWith("vip_joinNotVIPTeam"))
                        VIP.addPlayerNotVIP(activeChar);
                   
                    if (_command.substring(endOfId+1).startsWith("vip_finishVIP"))
                        VIP.vipWin(activeChar);

                    if (_command.substring(endOfId+1).startsWith("tvt_player_join "))
                    {
                        String teamName = _command.substring(endOfId+1).substring(16);

                        if (TvT._joining)
                            TvT.addPlayer(activeChar, teamName);
                        else
                            activeChar.sendMessage("The event is already started. You can not join now!");
                    }
                   
                    if (_command.substring(endOfId+1).startsWith("tvt_player_leave "))
                    {
                        if (TvT._joining)
                            TvT.removePlayer(activeChar);
                        else
                            activeChar.sendMessage("The event is already started. You can not leave now!");
                    }
                               
                    if (_command.substring(endOfId+1).startsWith("ctf_player_join "))
                    {
                        String teamName = _command.substring(endOfId+1).substring(16); 
                        
                        if (CTF._joining)
                            CTF.addPlayer(activeChar, teamName);
                        else
                            activeChar.sendMessage("The event is already started. You can not join now!");
                    }

                    if (_command.substring(endOfId+1).startsWith("ctf_player_leave"))
                    {
                        if (CTF._joining)
                            CTF.removePlayer(activeChar);
                        else
                            activeChar.sendMessage("The event is already started. You can not leave now!");
                    }

                    if (object != null && object instanceof L2NpcInstance && endOfId > 0 && activeChar.isInsideRadius(object, L2NpcInstance.INTERACTION_DISTANCE, false, false))
                    {
                        ((L2NpcInstance) object).onBypassFeedback(activeChar, _command.substring(endOfId+1));
                    }
                } catch (NumberFormatException nfe) {}
            }
            //  Draw a Symbol
            else if (_command.equals("menu_select?ask=-16&reply=1"))
            {
                L2Object object = activeChar.getTarget();
                if (object instanceof L2NpcInstance)
                {
                    ((L2NpcInstance) object).onBypassFeedback(activeChar, _command);
                }
            }
            else if (_command.equals("menu_select?ask=-16&reply=2"))
            {
                L2Object object = activeChar.getTarget();
                if (object instanceof L2NpcInstance)
                {
                    ((L2NpcInstance) object).onBypassFeedback(activeChar, _command);
                }
            }
            else if (_command.startsWith("bbs_"))
            {
                CommunityBoard.getInstance().handleCommands(getClient(), _command);
            }
            else if (_command.startsWith("_bbs"))
            {
                CommunityBoard.getInstance().handleCommands(getClient(), _command);
            }
            else if (_command.startsWith("Quest "))
            {
                if(!activeChar.validateBypass(_command))
                    return;

                L2PcInstance player = getClient().getActiveChar();
                if (player == null) return;
                
                String p = _command.substring(6).trim();
                int idx = p.indexOf(' ');
                if (idx < 0)
                    player.processQuestEvent(p, "");
                else
                    player.processQuestEvent(p.substring(0, idx), p.substring(idx).trim());
            }
            else if (_command.startsWith("leaderboard_")) 
            {
               LeaderboardEngine.getInstance().processBypass(activeChar, _command.substring(12));
            }            
        } catch (Exception e) {
            _log.warn("Bad RequestBypassToServer: ", e);
        }
//      finally
//      {
//          activeChar.clearBypass();
//      }
    }

    /**
     * @param client
     */
    private void comeHere(L2PcInstance activeChar) 
    {
        L2Object obj = activeChar.getTarget();
        if (obj == null) return;
        if (obj instanceof L2NpcInstance)
        {
            L2NpcInstance temp = (L2NpcInstance) obj;
            temp.setTarget(activeChar);
            temp.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO,
                    new L2CharPosition(activeChar.getX(),activeChar.getY(), activeChar.getZ(), 0 ));
//          temp.moveTo(player.getX(),player.getY(), player.getZ(), 0 );
        }
        
    }

    private void playerHelp(L2PcInstance activeChar, String path)
    {
        if (path.indexOf("..") != -1)
            return;

        String filename = "data/html/"+path;
        NpcHtmlMessage html = new NpcHtmlMessage(1);
        html.setFile(filename);
        activeChar.sendPacket(html);
    }

    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
     */
    public String getType()
    {
        return _C__21_REQUESTBYPASSTOSERVER;
    }
}
