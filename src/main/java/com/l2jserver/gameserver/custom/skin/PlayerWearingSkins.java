package com.l2jserver.gameserver.custom.skin;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PlayerWearingSkins {

	private final int objectId;
	private final Map<BodyPart, SkinHolder> wearingParts;
	private final Map<BodyPart, SkinHolder> tempSkinHolder = new ConcurrentHashMap<>();
	private final Map<BodyPart, ScheduledFuture<?>> scheduledRestoreSkinTask = new ConcurrentHashMap<>();
	private static final int TRYON_TIME = 15;

	public PlayerWearingSkins(int objectId, Map<BodyPart, SkinHolder> wearingParts) {
		this.objectId = objectId;
		this.wearingParts = wearingParts;
	}

	public PlayerWearingSkins(int objectId) {
		this.objectId = objectId;
		this.wearingParts = new ConcurrentHashMap<>();
	}

	public void changeSkin(BodyPart bodyPart, SkinHolder skin) {
		wearingParts.remove(BodyPart.ALLDRESS);
		wearingParts.put(bodyPart, skin);
	}

	public void tryOnSkin(BodyPart bodyPart, SkinHolder skin) {
//		Optional.ofNullable(scheduledRestoreSkinTask.remove(bodyPart)).ifPresent(s->s.cancel(true));
//
//		if(tempSkinHolder.containsKey(bodyPart)){
//			tempSkinHolder.remove(bodyPart).ifPresent(s->wearingParts.put(bodyPart,s));
//		}
//		wearingParts.remove(bodyPart);
//		tempSkinHolder.put(BodyPart.ALLDRESS,wearingParts.get(BodyPart.ALLDRESS));
		tempSkinHolder.put(bodyPart, skin);
//		wearingParts.put(bodyPart, skin);
		scheduledRestoreSkinTask.put(bodyPart,ThreadPoolManager.getInstance().scheduleGeneral(new RestoreSkinTask(bodyPart),TRYON_TIME,TimeUnit.SECONDS));

	}

	private final class RestoreSkinTask implements Runnable {
		private final BodyPart bodyPart;

		public RestoreSkinTask(BodyPart bodyPart) {
			this.bodyPart = bodyPart;
		}

		@Override public void run() {
			tempSkinHolder.remove(bodyPart);
			L2PcInstance pc = L2World.getInstance().getPlayer(objectId);
			if(pc==null){
				return;
			}
			pc.broadcastUserInfo();
			pc.broadcastUserInfo();
		}
	}

	public Optional<SkinHolder> getBodyPart(BodyPart bodyPart) {
		if(tempSkinHolder.size()>0 ){
			if(BodyPart.isArmor(bodyPart) && tempSkinHolder.containsKey(BodyPart.ALLDRESS)){
				return Optional.ofNullable(tempSkinHolder.get(BodyPart.ALLDRESS));
			}
			if(tempSkinHolder.containsKey(bodyPart)){
			return Optional.ofNullable(tempSkinHolder.get(bodyPart));
			}
		}
		if (BodyPart.isArmor(bodyPart) && wearingParts.containsKey(BodyPart.ALLDRESS)) {
			bodyPart = BodyPart.ALLDRESS;
		}
		return Optional.ofNullable(wearingParts.get(bodyPart));
	}

	public Map<BodyPart, SkinHolder> getPlayersWearingSkins() {
		tempSkinHolder.forEach(((bodyPart, skinHolder) -> {
		}));

		return Collections.unmodifiableMap(this.wearingParts);
	}

	public int getObjectId() {
		return objectId;
	}

}