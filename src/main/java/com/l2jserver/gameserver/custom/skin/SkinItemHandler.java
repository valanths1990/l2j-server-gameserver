package com.l2jserver.gameserver.custom.skin;

import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

public class SkinItemHandler implements IItemHandler {

	@Override public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {


		return false;
	}
}
