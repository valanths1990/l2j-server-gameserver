package com.l2jserver.gameserver.model.events.impl.character.player;

import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerPartyJoin implements IBaseEvent {
	private L2PcInstance player;
	private L2Party party;
	public OnPlayerPartyJoin(L2PcInstance player,L2Party party){
		this.player = player;
		this.party=party;
	}

	public L2PcInstance getActiveChar(){
		return this.player;
	}
	public L2Party getParty(){
		return this.party;
	}
	@Override public EventType getType() {
		return EventType.ON_PLAYER_PARTY_JOIN;
	}
}
