package com.l2jserver.gameserver.model.events.impl.character.player;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

public class OnPlayerUnequipItem implements IBaseEvent {
	private L2PcInstance player;
	private L2ItemInstance item;

	public OnPlayerUnequipItem(L2PcInstance player, L2ItemInstance item) {
		this.player = player;
		this.item = item;
	}

	public L2PcInstance getActiveChar() {
		return player;
	}

	public L2ItemInstance getItem() {
		return item;
	}

	@Override public EventType getType() {
		return EventType.ON_PLAYER_UNEQUIP_ITEM;
	}
}
