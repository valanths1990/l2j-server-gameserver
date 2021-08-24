package com.l2jserver.gameserver.model.actor.instance;

import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;

public class L2CustomInstance extends L2Npc {
	public L2CustomInstance(L2NpcTemplate template) {
		super(template);
	}

	public L2CustomInstance(int npcId) {
		super(npcId);
	}

	@Override public void showChatWindow(L2PcInstance player) {
		String handler = this.getTemplate().getParameters().getString("type");
		if (handler == null) {
			return;
		}
		IBypassHandler bypassHandler = BypassHandler.getInstance().getHandler(handler);
		if (bypassHandler == null) {
			return;
		}
		bypassHandler.useBypass(handler, player, this);
	}

}
