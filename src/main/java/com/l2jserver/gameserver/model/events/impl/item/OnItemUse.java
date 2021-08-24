package com.l2jserver.gameserver.model.events.impl.item;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

public class OnItemUse implements IBaseEvent {

	private L2PcInstance player;
	private L2ItemInstance item;

	public OnItemUse(L2PcInstance player, L2ItemInstance item) {
		this.player = player;
		this.item = item;
	}

	public L2PcInstance getPlayer() {
		return player;
	}

	public L2ItemInstance getItem() {
		return item;
	}

	@Override public EventType getType() {
		return EventType.ON_ITEM_USE;
	}
}
