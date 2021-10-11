package com.l2jserver.gameserver.model.zone.type;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneExit;
import com.l2jserver.gameserver.model.zone.L2ZoneRespawn;

public class L2FarmZone extends L2ZoneRespawn {
	public L2FarmZone(int id) {
		super(id);
	}

	@Override protected void onEnter(L2Character character) {
		if (!(character instanceof L2PcInstance)) {
			return;
		}
		character.sendMessage("You have entered Farming zone.");
//		EventDispatcher.getInstance().notifyEventAsync(new OnCreatureZoneEnter(character, this),Containers.Players());
	}

	@Override protected void onExit(L2Character character) {
		if (!(character instanceof L2PcInstance)) {
			return;
		}
		character.sendMessage("You have left Farming zone.");
//		EventDispatcher.getInstance().notifyEventAsync(new OnCreatureZoneExit(character, this),Containers.Players());
	}

	@Override public void onDieInside(L2Character character) {
		super.onDieInside(character);
	}

	@Override public void onReviveInside(L2Character character) {
		super.onReviveInside(character);
	}
}
