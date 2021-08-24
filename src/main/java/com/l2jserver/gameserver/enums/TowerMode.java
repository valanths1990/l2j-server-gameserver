package com.l2jserver.gameserver.enums;

import java.util.Comparator;

public enum TowerMode implements Comparator<TowerMode> {
	ONE_PARTY(2),
	MULTIPLE_PARTIES(3),
	SINGLE(3),
	LEADER(999);
	private final int order;
	TowerMode(int order){
		this.order=order;
	}
	public int getOrder(){
		return order;
	}

	@Override public int compare(TowerMode o1, TowerMode o2) {
		return Integer.compare(o1.getOrder(),o2.getOrder());
	}
}
