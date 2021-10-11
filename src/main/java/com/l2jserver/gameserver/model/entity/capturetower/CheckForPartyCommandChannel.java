package com.l2jserver.gameserver.model.entity.capturetower;

import com.l2jserver.gameserver.model.L2CommandChannel;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;
import java.util.function.Function;

public class CheckForPartyCommandChannel implements Function<List<L2PcInstance>,L2PcInstance> {
	@Override public L2PcInstance apply(List<L2PcInstance> l2PcInstances) {
		if(l2PcInstances.stream().anyMatch(p->p.getParty() == null || !p.getParty().isInCommandChannel() )){
			return null;
		}

		L2CommandChannel firstChannel = l2PcInstances.stream()
			.filter(p->p.getParty().getCommandChannel().getLeader() == p)
			.map(p->p.getParty().getCommandChannel()).findFirst().orElse(null);

		if(firstChannel==null){
			return null;
		}
			if(l2PcInstances.stream().anyMatch(p->!firstChannel.containsPlayer(p))){
				return null;
			}

		return firstChannel.getLeader();
	}
}
