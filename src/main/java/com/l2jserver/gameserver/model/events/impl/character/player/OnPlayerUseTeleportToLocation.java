package com.l2jserver.gameserver.model.events.impl.character.player;

import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerUseTeleportToLocation implements IBaseEvent {

	L2PcInstance player;
	Location location;

	public OnPlayerUseTeleportToLocation(L2PcInstance player, Location location) {
		this.player = player;
		this.location = location;
	}

	public L2PcInstance getPlayer() {
		return player;
	}

	public Location getLocation() {
		return location;
	}


	@Override public EventType getType() {
		return EventType.ON_PLAYER_USE_TELEPORT_TO_LOCATION;
	}
}
