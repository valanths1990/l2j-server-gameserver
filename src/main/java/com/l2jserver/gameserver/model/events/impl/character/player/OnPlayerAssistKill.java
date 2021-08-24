package com.l2jserver.gameserver.model.events.impl.character.player;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerAssistKill implements IBaseEvent {
	private final L2PcInstance activeChar;
	private final L2PcInstance target;

	public OnPlayerAssistKill(L2PcInstance activeChar, L2PcInstance target) {
		this.activeChar = activeChar;
		this.target = target;
	}

	public L2PcInstance getActiveChar() {
		return activeChar;
	}

	public L2PcInstance getTarget() {
		return target;
	}

	@Override public EventType getType() {
		return EventType.ON_PLAYER_ASSIST_KILL;
	}
}
