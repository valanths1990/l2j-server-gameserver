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

import static com.l2jserver.gameserver.config.Configuration.customs;
import static com.l2jserver.gameserver.config.Configuration.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.handler.ChatHandler;
import com.l2jserver.gameserver.handler.IChatHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerChat;
import com.l2jserver.gameserver.model.events.returns.ChatFilterReturn;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.util.Util;

public final class Say2 extends L2GameClientPacket {

	private static final String _C__49_SAY2 = "[C] 49 Say2";

	private static final Logger LOG_CHAT = LoggerFactory.getLogger("chat");

	public static final int ALL = 0;
	public static final int SHOUT = 1; // !
	public static final int TELL = 2;
	public static final int PARTY = 3; // #
	public static final int CLAN = 4; // @
	public static final int GM = 5;
	public static final int PETITION_PLAYER = 6; // used for petition
	public static final int PETITION_GM = 7; // * used for petition
	public static final int TRADE = 8; // +
	public static final int ALLIANCE = 9; // $
	public static final int ANNOUNCEMENT = 10;
	public static final int BOAT = 11;
	public static final int L2FRIEND = 12;
	public static final int MSNCHAT = 13;
	public static final int PARTYMATCH_ROOM = 14;
	public static final int PARTYROOM_COMMANDER = 15; // (Yellow)
	public static final int PARTYROOM_ALL = 16; // (Red)
	public static final int HERO_VOICE = 17;
	public static final int CRITICAL_ANNOUNCE = 18;
	public static final int SCREEN_ANNOUNCE = 19;
	public static final int BATTLEFIELD = 20;
	public static final int MPCC_ROOM = 21;
	public static final int NPC_ALL = 22;
	public static final int NPC_SHOUT = 23;

	private static final String[] CHAT_NAMES = {
		"ALL", "SHOUT", "TELL", "PARTY", "CLAN", "GM", "PETITION_PLAYER", "PETITION_GM", "TRADE", "ALLIANCE", "ANNOUNCEMENT", // 10
		"BOAT", "L2FRIEND", "MSNCHAT", "PARTYMATCH_ROOM", "PARTYROOM_COMMANDER", "PARTYROOM_ALL", "HERO_VOICE", "CRITICAL_ANNOUNCE", "SCREEN_ANNOUNCE", "BATTLEFIELD", "MPCC_ROOM"
	};

	private static final String[] WALKER_COMMAND_LIST = {
		"USESKILL",
		"USEITEM",
		"BUYITEM",
		"SELLITEM",
		"SAVEITEM",
		"LOADITEM",
		"MSG",
		"DELAY",
		"LABEL",
		"JMP",
		"CALL",
		"RETURN",
		"MOVETO",
		"NPCSEL",
		"NPCDLG",
		"DLGSEL",
		"CHARSTATUS",
		"POSOUTRANGE",
		"POSINRANGE",
		"GOHOME",
		"SAY",
		"EXIT",
		"PAUSE",
		"STRINDLG",
		"STRNOTINDLG",
		"CHANGEWAITTYPE",
		"FORCEATTACK",
		"ISMEMBER",
		"REQUESTJOINPARTY",
		"REQUESTOUTPARTY",
		"QUITPARTY",
		"MEMBERSTATUS",
		"CHARBUFFS",
		"ITEMCOUNT",
		"FOLLOWTELEPORT"
	};

	private String _text;
	private int _type;
	private String _target;

	@Override protected void readImpl() {
		_text = readS();
		_type = readD();
		_target = (_type == TELL) ? readS() : null;
	}

	@Override protected void runImpl() {
		if (general().debug()) {
			_log.info("Say2: Msg Type = '" + _type + "' Text = '" + _text + "'.");
		}

		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null) {
			return;
		}

		if ((_type < 0) || (_type >= CHAT_NAMES.length)) {
			_log.warning("Say2: Invalid type: " + _type + " Player : " + activeChar.getName() + " text: " + _text);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			activeChar.logout();
			return;
		}

		if (_text.isEmpty()) {
			_log.warning(activeChar.getName() + ": sending empty text. Possible packet hack!");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			activeChar.logout();
			return;
		}

		// Even though the client can handle more characters than it's current limit allows, an overflow (critical error) happens if you pass a huge (1000+) message.
		// July 11, 2011 - Verified on High Five 4 official client as 105.
		// Allow higher limit if player shift some item (text is longer then).
		if (!activeChar.isGM() && (((_text.indexOf(8) >= 0) && (_text.length() > 500)) || ((_text.indexOf(8) < 0) && (_text.length() > 105)))) {
			activeChar.sendPacket(SystemMessageId.DONT_SPAM);
			return;
		}

		if (customs().l2WalkerProtection() && (_type == TELL) && checkBot(_text)) {
			Util.handleIllegalPlayerAction(activeChar, "Client Emulator Detect: Player " + activeChar.getName() + " using l2walker.");
			return;
		}

		if (activeChar.isCursedWeaponEquipped() && ((_type == TRADE) || (_type == SHOUT))) {
			activeChar.sendPacket(SystemMessageId.SHOUT_AND_TRADE_CHAT_CANNOT_BE_USED_WHILE_POSSESSING_CURSED_WEAPON);
			return;
		}

		if (activeChar.isChatBanned() && (_text.charAt(0) != '.')) {
			if (activeChar.getEffectList().getFirstEffect(L2EffectType.CHAT_BLOCK) != null) {
				activeChar.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_SO_CHATTING_NOT_ALLOWED);
			} else {
				if (general().getBanChatChannels().contains(_type)) {
					activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
				}
			}
			return;
		}

		if (activeChar.isJailed() && general().jailDisableChat()) {
			if ((_type == TELL) || (_type == SHOUT) || (_type == TRADE) || (_type == HERO_VOICE)) {
				activeChar.sendMessage("You can not chat with players outside of the jail.");
				return;
			}
		}

		if ((_type == PETITION_PLAYER) && activeChar.isGM()) {
			_type = PETITION_GM;
		}

		if (general().logChat()) {
			if (_type == TELL) {
				LOG_CHAT.info("{} {} says [{}] to {}.", CHAT_NAMES[_type], activeChar.getName(), _text, _target);
			} else {
				LOG_CHAT.info("{} {} says [{}].", CHAT_NAMES[_type], activeChar.getName(), _text);
			}
		}

		if (_text.indexOf(8) >= 0) {
			if (!parseAndPublishItem(activeChar)) {
				return;
			}
		}

		final ChatFilterReturn filter = EventDispatcher.getInstance().notifyEvent(new OnPlayerChat(activeChar, L2World.getInstance().getPlayer(_target), _text, _type), ChatFilterReturn.class);

		if (filter != null && filter.abort()) {
			return;
		}

		if (filter != null) {
			_text = filter.getFilteredText();
		}

		// Say Filter implementation
		if (general().useChatFilter()) {
			checkText();
		}

		final IChatHandler handler = ChatHandler.getInstance().getHandler(_type);
		if (handler != null) {
			handler.handleChat(_type, activeChar, _target, _text);
		} else {
			_log.info("No handler registered for ChatType: " + _type + " Player: " + getClient());
		}
	}

	private boolean checkBot(String text) {
		for (String botCommand : WALKER_COMMAND_LIST) {
			if (text.startsWith(botCommand)) {
				return true;
			}
		}
		return false;
	}

	private void checkText() {
		String filteredText = _text;
		for (String pattern : general().getChatFilter()) {
			filteredText = filteredText.replaceAll("(?i)" + pattern, general().getChatFilterChars());
		}
		_text = filteredText;
	}

	private boolean parseAndPublishItem(L2PcInstance owner) {
		int pos1 = -1;
		while ((pos1 = _text.indexOf(8, pos1)) > -1) {
			int pos = _text.indexOf("ID=", pos1);
			if (pos == -1) {
				return false;
			}
			StringBuilder result = new StringBuilder(9);
			pos += 3;
			while (Character.isDigit(_text.charAt(pos))) {
				result.append(_text.charAt(pos++));
			}
			int id = Integer.parseInt(result.toString());
			L2Object item = L2World.getInstance().findObject(id);
			if (item instanceof L2ItemInstance) {
				if (owner.getInventory().getItemByObjectId(id) == null) {
					_log.info(getClient() + " trying publish item which doesnt own! ID:" + id);
					return false;
				}
				((L2ItemInstance) item).publish();
			} else {
				_log.info(getClient() + " trying publish object which is not item! Object:" + item);
				return false;
			}
			pos1 = _text.indexOf(8, pos) + 1;
			// missing ending tag
			if (pos1 == 0) {
				_log.info(getClient() + " sent invalid publish item msg! ID:" + id);
				return false;
			}
		}
		return true;
	}

	@Override public String getType() {
		return _C__49_SAY2;
	}

	@Override protected boolean triggersOnActionRequest() {
		return false;
	}
}
