package com.l2jserver.gameserver.model.entity.capturetower;

import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CheckForAlliance implements Function<List<L2PcInstance>,L2PcInstance> {
	@Override public L2PcInstance apply(List<L2PcInstance> l2PcInstances) {
			if(l2PcInstances.stream().anyMatch(p->p.getClan()==null || p.getClan().getAllyId()<=0) ){
				return null;
			}
		List<L2Clan> alliances = l2PcInstances.stream()
				.filter(p->ClanTable.getInstance().getClanAllies(p.getClan().getAllyId())!=null)
				.findFirst().map(p->ClanTable.getInstance().getClanAllies(p.getClan().getAllyId()))
				.orElse(Collections.emptyList());
			if(alliances.isEmpty()){
				return null;
			}

			if(l2PcInstances.stream().anyMatch(p->!alliances.contains(p.getClan()))){
				return null;
			}
			List<L2PcInstance> allianceMembers = l2PcInstances.stream().filter(p-> alliances.contains(p.getClan())).collect(Collectors.toList());
		return allianceMembers.stream().filter(p->p.getClan().getLeader().getObjectId() == p.getObjectId()).findFirst().orElse(null);
	}
}
