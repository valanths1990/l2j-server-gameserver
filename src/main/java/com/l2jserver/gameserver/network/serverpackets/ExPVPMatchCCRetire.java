package com.l2jserver.gameserver.network.serverpackets;

public class ExPVPMatchCCRetire extends L2GameServerPacket {
	@Override protected void writeImpl() {
		writeC(0xFE);
		writeD(0x8B);
	}
}
