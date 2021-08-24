package com.l2jserver.gameserver.model.events.impl.clan;

import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

public class OnClanReputationChanged implements IBaseEvent {

	private L2Clan clan;
	private int oldValue;
	private int newValue;

	public OnClanReputationChanged(L2Clan clan, int oldValue, int newValue) {
		this.clan = clan;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public L2Clan getClan() {
		return clan;
	}

	public int getOldValue() {
		return oldValue;
	}

	public int getNewValue() {
		return newValue;
	}

	@Override public EventType getType() {
		return EventType.ON_CLAN_REPUTATION_CHANGED;
	}
}
