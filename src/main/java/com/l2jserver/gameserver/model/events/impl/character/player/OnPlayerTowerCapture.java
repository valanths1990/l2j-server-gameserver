package com.l2jserver.gameserver.model.events.impl.character.player;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.capturetower.CaptureTower;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerTowerCapture implements IBaseEvent {
	private L2PcInstance capturer;
	private CaptureTower tower;

	public OnPlayerTowerCapture(L2PcInstance capturer, CaptureTower tower) {
		this.capturer = capturer;
		this.tower = tower;
	}

	public L2PcInstance getActiveChar() {
		return this.capturer;
	}

	public CaptureTower getTower() {
		return this.tower;
	}

	@Override public EventType getType() {
		return EventType.ON_PLAYER_TOWER_CAPTURE;
	}
}
