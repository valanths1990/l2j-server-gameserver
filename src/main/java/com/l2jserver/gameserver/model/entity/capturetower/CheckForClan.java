package com.l2jserver.gameserver.model.entity.capturetower;

import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CheckForClan implements Function<List<L2PcInstance>, L2PcInstance> {
	@Override public L2PcInstance apply(List<L2PcInstance> l2PcInstances) {
		if (l2PcInstances.stream().anyMatch(p -> p.getClan() == null)) {
			return null;
		}
		L2Clan firstClan = l2PcInstances.stream()
			.filter(p -> p.getClan() != null && p.getClan().getLeaderId() == p.getObjectId())
			.findFirst().map(L2PcInstance::getClan)
			.orElse(null);

		if (firstClan == null) {
			return null;
		}

		List<L2PcInstance> clanMembers = l2PcInstances.stream().filter(p -> p.getClan() != null && p.getClan().getId() == firstClan.getId()).collect(Collectors.toList());
		if (l2PcInstances.stream().anyMatch(p -> !clanMembers.contains(p))) {
			return null;
		}
		return firstClan.getLeader().getPlayerInstance();
	}
}
