package com.l2jserver.gameserver.custom.skin;

public class SkinHolder {

	private int objectId;
	private int skinId;
	private BodyPart skinPart;
	private String icon;



	public SkinHolder(int objectId,int skinId ,BodyPart skinPart,String icon){
		this.objectId = objectId;
		this.skinId = skinId;
		this.skinPart=skinPart;
		this.icon = icon;
	}

	public int getObjectId() {
		return objectId;
	}

	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}

	public int getSkinId() {
		return skinId;
	}

	public void setSkinId(int skinId) {
		this.skinId = skinId;
	}

	public BodyPart getSkinPart() {
		return skinPart;
	}
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	public void setSkinPart(BodyPart skinPart) {
		this.skinPart = skinPart;
	}
}
