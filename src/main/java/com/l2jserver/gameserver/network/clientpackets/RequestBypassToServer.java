/*
 * Copyright © 2004-2021 L2J Server
 *
 * This file is part of L2J Server.
 *
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.network.clientpackets;

import static com.l2jserver.gameserver.config.Configuration.general;

import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;

import com.l2jserver.gameserver.BypassHasher;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.enums.PlayerAction;
import com.l2jserver.gameserver.handler.AdminCommandHandler;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Hero;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.npc.OnNpcManorBypass;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerBypass;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.ConfirmDlg;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.GMAudit;
import com.l2jserver.gameserver.util.Util;

/**
 * RequestBypassToServer client packet implementation.
 *
 * @author HorridoJoho
 */
public final class RequestBypassToServer extends L2GameClientPacket {
	private static final String _C__23_REQUESTBYPASSTOSERVER = "[C] 23 RequestBypassToServer";
	// FIXME: This is for compatibility, will be changed when bypass functionality got an overhaul by NosBit
	private static final String[] _possibleNonHtmlCommands = {
		"home", "service", "action", "ranking", "store", "account", "skin", "clan"
	};

	// S
	private String _command;

	@Override protected void readImpl() {
		_command = readS();
		if (_command.matches("^-?[0-9]+")) {
			_command = BypassHasher.getInstance().decodeHash(Integer.valueOf(_command));
		}
		if(_command.contains("-h ")){
			_command = _command.replace("-h","").trim();
		}
		switch (_command) {
			case "_bbshome" -> _command = "home;homepage";
			case "_bbsgetfav" -> _command = "service;homepage";
			case "_bbslink" -> _command = "action;homepage";
			case "_bbsloc" -> _command = "ranking;homepage";
			case "_bbsclan" -> _command = "clan;homepage";
			case "_bbsmemo" -> _command = "skin;homepage";
			case "_maillist" -> _command = "store;homepage";
			case "_friendlist" -> _command = "account;homepage";
		}
	}

	@Override
	protected void runImpl() {
		System.out.println(_command);
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null) {
			return;
		}

		if (_command.isEmpty()) {
			_log.warning("Player " + activeChar.getName() + " sent empty bypass!");
			activeChar.logout();
			return;
		}

		boolean requiresBypassValidation = true;
		for (String possibleNonHtmlCommand : _possibleNonHtmlCommands) {
			if (_command.startsWith(possibleNonHtmlCommand)) {
				requiresBypassValidation = false;
				break;
			}
		}

		int bypassOriginId = 0;
		if (requiresBypassValidation) {
			bypassOriginId = activeChar.validateHtmlAction(_command);
			if (bypassOriginId == -1) {
				_log.warning("Player " + activeChar.getName() + " sent non cached bypass: '" + _command + "'");
				return;
			}
			if ((bypassOriginId > 0) && !Util.isInsideRangeOfObjectId(activeChar, bypassOriginId, L2Npc.INTERACTION_DISTANCE)) {
				// No logging here, this could be a common case where the player has the html still open and run too far away and then clicks a html action
				return;
			}
		}

		if (!getClient().getFloodProtectors().getServerBypass().tryPerformAction(_command)) {
			return;
		}

		try {
			if (CommunityBoardHandler.getInstance().isCommunityBoardCommand(_command)) {
				CommunityBoardHandler.getInstance().handleParseCommand(_command, activeChar);
			} else if (_command.startsWith("admin_")) {
				String command = _command.split(" ")[0];

				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler(command);

				if (ach == null) {
					if (activeChar.isGM()) {
						activeChar.sendMessage("The command " + command.substring(6) + " does not exist!");
					}
					_log.warning(activeChar + " requested not registered admin command '" + command + "'");
					return;
				}

				if (!AdminData.getInstance().hasAccess(command, activeChar.getAccessLevel())) {
					activeChar.sendMessage("You don't have the access rights to use this command!");
					_log.warning("Character " + activeChar.getName() + " tried to use admin command " + command + ", without proper access level!");
					return;
				}

				if (AdminData.getInstance().requireConfirm(command)) {
					activeChar.setAdminConfirmCmd(_command);
					ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.S1);
					dlg.addString("Are you sure you want execute command " + _command.substring(6) + " ?");
					activeChar.addAction(PlayerAction.ADMIN_COMMAND);
					activeChar.sendPacket(dlg);
				} else {
					if (general().gmAudit()) {
						GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", _command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"));
					}

					ach.useAdminCommand(_command, activeChar);
				}
			} else if (_command.equals("come_here") && activeChar.isGM()) {
				comeHere(activeChar);
			} else if (_command.startsWith("npc_")) {
				int endOfId = _command.indexOf('_', 5);
				String id;
				if (endOfId > 0) {
					id = _command.substring(4, endOfId);
				} else {
					id = _command.substring(4);
				}
				if (Util.isDigit(id)) {
					L2Object object = L2World.getInstance().findObject(Integer.parseInt(id));

					if ((object != null) && object.isNpc() && (endOfId > 0) && activeChar.isInsideRadius(object, L2Npc.INTERACTION_DISTANCE, false, false)) {
						((L2Npc) object).onBypassFeedback(activeChar, _command.substring(endOfId + 1));
					}
				}

				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			} else if (_command.startsWith("item_")) {
				int endOfId = _command.indexOf('_', 5);
				String id;
				if (endOfId > 0) {
					id = _command.substring(5, endOfId);
				} else {
					id = _command.substring(5);
				}
				try {
					final L2ItemInstance item = activeChar.getInventory().getItemByObjectId(Integer.parseInt(id));
					if ((item != null) && (endOfId > 0)) {
						item.onBypassFeedback(activeChar, _command.substring(endOfId + 1));
					}

					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				} catch (NumberFormatException nfe) {
					_log.log(Level.WARNING, "NFE for command [" + _command + "]", nfe);
				}
			} else if (_command.startsWith("_match")) {
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0) {
					Hero.getInstance().showHeroFights(activeChar, heroclass, heroid, heropage);
				}
			} else if (_command.startsWith("_diary")) {
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0) {
					Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
				}
			} else if (_command.startsWith("_olympiad?command")) {
				int arenaId = Integer.parseInt(_command.split("=")[2]);
				final IBypassHandler handler = BypassHandler.getInstance().getHandler("arenachange");
				if (handler != null) {
					handler.useBypass("arenachange " + (arenaId - 1), activeChar, null);
				}
			} else if (_command.startsWith("manor_menu_select")) {
				final L2Npc lastNpc = activeChar.getLastFolkNPC();
				if (general().allowManor() && (lastNpc != null) && lastNpc.canInteract(activeChar)) {
					final String[] split = _command.substring(_command.indexOf("?") + 1).split("&");
					final int ask = Integer.parseInt(split[0].split("=")[1]);
					final int state = Integer.parseInt(split[1].split("=")[1]);
					final boolean time = split[2].split("=")[1].equals("1");
					EventDispatcher.getInstance().notifyEventAsync(new OnNpcManorBypass(activeChar, lastNpc, ask, state, time), lastNpc);
				}
			} else {
				final IBypassHandler handler = BypassHandler.getInstance().getHandler(_command);
				if (handler != null) {
					if (bypassOriginId > 0) {
						L2Object bypassOrigin = activeChar.getKnownList().getKnownObjects().get(bypassOriginId);
						if ((bypassOrigin != null) && bypassOrigin.isInstanceTypes(InstanceType.L2Character)) {
							handler.useBypass(_command, activeChar, (L2Character) bypassOrigin);
						} else {
							handler.useBypass(_command, activeChar, null);
						}
					} else {
						handler.useBypass(_command, activeChar, null);
					}
				} else {
					_log.warning(getClient() + " sent not handled RequestBypassToServer: [" + _command + "]");
				}
			}
		} catch (Exception e) {
			_log.log(Level.WARNING, "Exception processing bypass from player " + activeChar.getName() + ": " + _command, e);

			if (activeChar.isGM()) {
				StringBuilder sb = new StringBuilder(200);
				sb.append("<html><body>");
				sb.append("Bypass error: ").append(e).append("<br1>");
				sb.append("Bypass command: ").append(_command).append("<br1>");
				sb.append("StackTrace:<br1>");
				for (StackTraceElement ste : e.getStackTrace()) {
					sb.append(ste.toString()).append("<br1>");
				}
				sb.append("</body></html>");
				// item html
				final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1, sb.toString());
				msg.disableValidation();
				activeChar.sendPacket(msg);
			}
		}

		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerBypass(activeChar, _command), activeChar);
	}

	/**
	 * @param activeChar
	 */
	private static void comeHere(L2PcInstance activeChar) {
		L2Object obj = activeChar.getTarget();
		if (obj == null) {
			return;
		}
		if (obj instanceof L2Npc) {
			L2Npc temp = (L2Npc) obj;
			temp.setTarget(activeChar);
			temp.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, activeChar.getLocation());
		}
	}

	@Override public String getType() {
		return _C__23_REQUESTBYPASSTOSERVER;
	}
}
