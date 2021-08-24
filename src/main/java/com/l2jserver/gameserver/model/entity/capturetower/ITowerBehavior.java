package com.l2jserver.gameserver.model.entity.capturetower;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;

public interface ITowerBehavior {
	 L2PcInstance getCapturer(List<L2PcInstance> playersNearTower);
}
