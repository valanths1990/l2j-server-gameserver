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
	public void changeSkin(BodyPart bodyPart, SkinHolder skin){
		wearingParts.remove(BodyPart.ALLDRESS);
		wearingParts.put(bodyPart,skin);
	}

	public Optional<SkinHolder> getBodyPart(BodyPart bodyPart) {
		if (BodyPart.isArmor(bodyPart) && wearingParts.containsKey(BodyPart.ALLDRESS)) {
			bodyPart = BodyPart.ALLDRESS;
		}
		return Optional.ofNullable(wearingParts.get(bodyPart));
	}

	public Map<BodyPart, SkinHolder> getPlayersWearingSkins() {
		return Collections.unmodifiableMap(this.wearingParts);
	}

	public int getObjectId() {
		return objectId;
	}

}