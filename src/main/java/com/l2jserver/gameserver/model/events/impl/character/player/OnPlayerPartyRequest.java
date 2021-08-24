package com.l2jserver.gameserver.model.events.impl.character.player;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerPartyRequest implements IBaseEvent {
	private L2PcInstance player;
	private L2PcInstance requestor;

	public OnPlayerPartyRequest(L2PcInstance requestor, L2PcInstance player) {
		this.player = player;
		this.requestor = requestor;

	}

	@Override public EventType getType() {
		return EventType.ON_PLAYER_PARTY_REQUEST;
	}

	public L2PcInstance getRequestor() {
		return this.requestor;
	}

	public L2PcInstance getPlayer() {
		return this.player;
	}

}
