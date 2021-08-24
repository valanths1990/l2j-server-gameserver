package com.l2jserver.gameserver.model.entity.capturetower;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;

public class SinglePlayerBehavior implements ITowerBehavior{
	@Override public L2PcInstance getCapturer(List<L2PcInstance>playersNearTower) {
		if(playersNearTower.size()==1 && playersNearTower.get(0).getParty()==null){
			return playersNearTower.get(0);
		}
		return null;
	}
}
