package com.l2jserver.gameserver.custom.skin;

public class PlayerSkinConfig {

	private int objectId;
	private Visibility visibility;

	public PlayerSkinConfig(int objectId, Visibility visibility) {
		this.objectId = objectId;
		this.visibility = visibility;
	}

	public int getObjectId() {
		return objectId;
	}

	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
}
