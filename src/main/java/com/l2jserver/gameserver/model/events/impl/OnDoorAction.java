package com.l2jserver.gameserver.model.events.impl;

import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;

public class OnDoorAction implements IBaseEvent {

	private L2PcInstance player;
	private L2DoorInstance door;

	public OnDoorAction(L2PcInstance player, L2DoorInstance door) {
		this.player = player;
		this.door = door;
	}

	public L2PcInstance getPlayer() {
		return player;
	}

	public L2DoorInstance getDoor() {
		return door;
	}

	@Override public EventType getType() {
		return EventType.ON_DOOR_ACTION;
	}
}
