package com.l2jserver.gameserver.model.entity.capturetower;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;
import java.util.function.Function;

public class CheckForSinglePlayer  implements Function<List<L2PcInstance>,L2PcInstance> {
	@Override public L2PcInstance apply(List<L2PcInstance> l2PcInstances) {
		if(l2PcInstances.size()==1 && l2PcInstances.get(0).getParty()==null){
			return l2PcInstances.get(0);
		}
		return null;
	}
}
