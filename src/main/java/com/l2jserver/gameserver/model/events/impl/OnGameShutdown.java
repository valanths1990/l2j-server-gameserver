package com.l2jserver.gameserver.model.events.impl;

import com.l2jserver.gameserver.model.events.EventType;

public class OnGameShutdown implements IBaseEvent {
	private int mode;

	public OnGameShutdown(int mode) {
		this.mode = mode;
	}

	@Override public EventType getType() {
		return EventType.ON_GAME_SHUTDOWN;
	}
}

