package com.l2jserver.gameserver.model.entity.capturetower;

import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;
import java.util.function.Function;

public class CheckForParty implements Function<List<L2PcInstance>,L2PcInstance> {
	@Override public L2PcInstance apply(List<L2PcInstance> l2PcInstances) {
		if(l2PcInstances.stream().anyMatch(p->p.getParty() == null)){
			return null;
		}
		L2Party firstPt = l2PcInstances.stream().filter(p->p.getParty().getLeader() == p).map(L2PcInstance::getParty).findFirst().orElse(null);
		if(firstPt==null){
			return null;
		}

		if(l2PcInstances.stream().anyMatch(p->!firstPt.getMembers().contains(p))){
			return null;
		}
		return firstPt.getLeader();
	}
}
