package com.l2jserver.gameserver.custom.skin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerWearingSkins {


	private final int objectId;
	private final Map<BodyPart, SkinHolder> wearingParts;

	public PlayerWearingSkins(int objectId, Map<BodyPart, SkinHolder> wearingParts) {
		this.objectId = objectId;
		this.wearingParts = wearingParts;
	}

	public PlayerWearingSkins(int objectId) {
		this.objectId = objectId;
		this.wearingParts = new ConcurrentHashMap<>();
	}

	public Optional<SkinHolder> getBodyPart(BodyPart bodyPart) {

		//		BodyPart bodyPart = null;
		//		if (item.getItem().getType2() == ItemType2.SHIELD_ARMOR) {
		//			ArmorType type = (ArmorType) item.getItemType();
		//			bodyPart = BodyPart.valueOf(type.name().toLowerCase());
		//			if (type != ArmorType.SHIELD && type != ArmorType.SIGIL) {
		//				int allDress = wearingBodyParts.get(BodyPart.alldress);
		//				if (allDress > 0) {
		//					return allDress;
		//				}
		//				bodyPart = BodyPart.valueOf(type.name() + "_" + types.get(item.getItem().getBodyPart()));
		//			}
		//
		//		}
		//		if (item.getItem().getType2() == ItemType2.WEAPON) {
		//			WeaponType type = (WeaponType) item.getItemType();
		//			bodyPart = BodyPart.valueOf(type.name() + types.get(item.getItem().getBodyPart()));
		//		}
		//		if (bodyPart == null)
		//			return item.getDisplayId();
		//		Integer id = wearingBodyParts.get(bodyPart);
		//		return id == null || id == -1 ? item.getDisplayId() : id;
		return null;
	}

	public Map<BodyPart, SkinHolder> getPlayersWearingSkins() {
		return Collections.unmodifiableMap(this.wearingParts);
	}
	public int getObjectId() {
		return objectId;
	}

}