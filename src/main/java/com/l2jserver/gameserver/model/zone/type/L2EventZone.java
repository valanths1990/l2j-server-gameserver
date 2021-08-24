package com.l2jserver.gameserver.model.zone.type;

import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.L2ZoneRespawn;


public class L2EventZone extends L2ZoneRespawn {

	public L2EventZone(int id) {
		super(id);
	}

	@Override protected void onEnter(L2Character character) {
		if(character instanceof L2PcInstance){
			character.sendMessage("You have entered a Event Zone.");
		}
	}

	@Override protected void onExit(L2Character character) {
		if(character instanceof L2PcInstance){
			character.sendMessage("You have left a Event Zone.");
		}
	}

}
