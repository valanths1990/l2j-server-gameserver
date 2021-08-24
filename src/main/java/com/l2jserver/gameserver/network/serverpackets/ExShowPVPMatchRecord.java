package com.l2jserver.gameserver.network.serverpackets;

import com.l2jserver.gameserver.model.holders.Participant;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ExShowPVPMatchRecord extends L2GameServerPacket {
	private final List<Participant> participants;

	public ExShowPVPMatchRecord(List<Participant> participants) {
		this.participants=participants;
	}

	@Override protected void writeImpl() {
		writeC(0xfe);
		writeH(0x89);
		writeD(0x00);
		writeD(participants.size());
		participants.forEach(p -> {
			writeS(p.getPlayerName());
			writeD(p.getPoints());
		});
	}
}
