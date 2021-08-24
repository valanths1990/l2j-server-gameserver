package com.l2jserver.gameserver.network.serverpackets;

public class ExEventCloseUI extends L2GameServerPacket{
	@Override protected void writeImpl() {
		writeC(0xFE);
		writeH(0x8B);
	}
}
