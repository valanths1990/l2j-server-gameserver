package com.l2jserver.gameserver.model.entity.capturetower;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.enums.TowerMode;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerTowerCapture;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CaptureTower extends L2Npc{
//	private final int TOWER_ID = 40009;
	private int towerRange = 400;
	private  int refreshRate = 400;
	private L2PcInstance capturer;
	private int progress;
	boolean hasBeenReset = true;
	private ITowerBehavior behavior;

	public CaptureTower(L2NpcTemplate template, ITowerBehavior behavior) {
		super(template);
			this.progress = 0;
			this.behavior = behavior;
		 ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::update, 0, refreshRate, TimeUnit.MILLISECONDS);
	}

	private void update() {
		L2PcInstance currentCapturer = anyCapturer();
		if (currentCapturer == null) {
			return;
		}
		if (capturer == null) {
			capturer = currentCapturer;
		}
		if (currentCapturer != capturer) {
			progress = 0;
			hasBeenReset = true;
			capturer = currentCapturer;
		}
		progress++;
		if (progress >= 100) {
			progress = 100;
		}
		if (hasBeenReset && progress == 100) {
			EventDispatcher.getInstance().notifyEvent(new OnPlayerTowerCapture(capturer, this), Containers.Players());
			hasBeenReset = false;
		}
		if (progress > 0) {
			setTitle(capturer.getName() + " " + progress + "%");
			broadcastInfo();
			broadcastStatusUpdate();
		}
	}

	public void resetMe() {
		progress = 0;
		capturer = null;
		setTitle("");
	}

	private L2PcInstance anyCapturer() {
		List<L2PcInstance> playersNearTower = getKnownList().getKnownCharacters().stream().filter(c -> c instanceof L2PcInstance && !c.isAlikeDead() && c.isInsideRadius(getLocation(), towerRange, false, true)).map(c -> (L2PcInstance) c).collect(Collectors.toList());
		if (playersNearTower.size() == 0) {
			return null;
		}
		return behavior.getCapturer(playersNearTower);

	}

	public int getTowerRange() {
		return towerRange;
	}

	public int getRefreshRate() {
		return refreshRate;
	}

	public L2PcInstance getCapturer() {
		return capturer;
	}

	public int getProgress() {
		return progress;
	}

	public boolean isHasBeenReset() {
		return hasBeenReset;
	}

	public ITowerBehavior getBehavior() {
		return behavior;
	}

	public void setTowerRange(int towerRange) {
		this.towerRange = towerRange;
	}

	public void setRefreshRate(int refreshRate) {
		this.refreshRate = refreshRate;
	}

	public void setCapturer(L2PcInstance capturer) {
		this.capturer = capturer;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void setHasBeenReset(boolean hasBeenReset) {
		this.hasBeenReset = hasBeenReset;
	}

	public void setBehavior(ITowerBehavior behavior) {
		this.behavior = behavior;
	}
}



//					Optional<L2Party> findFirstParty = playersNearTower.stream().filter(p -> p.getParty() != null && p.getParty().isLeader(p)).map(L2PcInstance::getParty).findFirst();
//
//					findFirstParty.ifPresent(party -> {
//						if (party.isInCommandChannel()) {
//							List<L2PcInstance> channelledPlayer = party.getCommandChannel().getMembers();
//							List<L2PcInstance> difference = playersNearTower.stream().filter(pnt -> !channelledPlayer.contains(pnt)).collect(Collectors.toList());
//							if (difference.size() > 0) {
//								return;
//							}
//						}
//						if (playersNearTower.stream().map(L2PcInstance::getParty).filter(Objects::nonNull).collect(Collectors.toList()).size() > 1) {
//							return;
//						}
//						capturers.put(m, party.getLeader());
//					});
//					Set<L2Party> allParties = playersNearTower.stream().map(L2PcInstance::getParty).filter(Objects::nonNull).collect(Collectors.toSet());
//					findFirstParty.ifPresent(party -> {
//						if (allParties.size() > 1) {
//							if (!party.isInCommandChannel()) {
//								return;
//							}
//							List<L2Party> firstPtChannels = party.getCommandChannel().getParties();
//
//							List<L2Party> difference = allParties.stream().filter(allPt -> !firstPtChannels.contains(allPt)).collect(Collectors.toList());
//							if (difference.size() > 0) {
//								return;
//							}
//							capturers.put(m, party.getLeader());
//						}
//					});

//}
//				case CLAN -> {
//
//				}
//		capturers.clear();
//		modes.forEach(m -> {
//
//			switch (m) {
//
//				case SINGLE -> {
//					if (playersNearTower.size() > 1) {
//						return;
//					}
//					Optional<L2PcInstance> singlePlayer = playersNearTower.stream().filter(p -> p.getParty() == null).reduce((a, b) -> null);
//					singlePlayer.ifPresent(p -> capturers.put(m, singlePlayer.get()));
//				}
//				case ONE_PARTY -> {
//					Set<L2Party> parties = playersNearTower.stream().map(L2PcInstance::getParty).filter(Objects::nonNull).collect(Collectors.toSet());
//					if (parties.size() == 1) {
//
//						L2Party firstPt = parties.iterator().next();
//						if (firstPt.isInCommandChannel()) {
//							return;
//						}
//						if (onlyLeaders) {
//							if (!playersNearTower.contains(firstPt.getLeader())) {
//								return;
//							}
//						}
//
//
//					}
//					//					if (parties.size() > 1) {
//					//
//					//						L2Party firstPt = parties.iterator().next();
//					//
//					//						if (!firstPt.isInCommandChannel()) {
//					//							return;
//					//						}
//					//						if (!playersNearTower.contains(firstPt.getCommandChannel().getLeader())) {
//					//							return;
//					//						}
//					//						List<L2PcInstance> allChanneledPlayer = firstPt.getCommandChannel().getMembers();
//					//						List<L2PcInstance> difference = playersNearTower.stream().filter(pnt -> !allChanneledPlayer.contains(pnt)).collect(Collectors.toList());
//					//						if (difference.size() > 0) {
//					//							return;
//					//						}
//					//						capturers.put(m, firstPt.getCommandChannel().getLeader());
//					//					} else if (parties.size() == 1) {
//					//						L2Party firstPt = parties.iterator().next();
//					//						if (firstPt.isInCommandChannel() && !playersNearTower.contains(firstPt.getCommandChannel().getLeader())) {
//					//							return;
//					//						}
//					//						if (!playersNearTower.contains(firstPt.getLeader())) {
//					//							return;
//					//						}
//					//						capturers.put(m, firstPt.getLeader());
//					//					}
//
//				}
//
//			}
//
//		});
//		Optional<TowerMode> highestOrder = capturers.keySet().stream().sorted().findFirst();
//		return highestOrder.map(capturers::get).orElse(null);
//
//		//		if (playersNearTower.isEmpty()) {
//		//			return null;
//		//		}
//		//
//		//		if (playersNearTower.size() == 1 && playersNearTower.get(0).getParty() == null) {
//		//			return playersNearTower.get(0);
//		//		}
//		//
//		//		if (playersNearTower.size() > 1 && playersNearTower.stream().allMatch(p -> p.getParty() == null)) {
//		//			return null;
//		//		}
//		//
//		//		Optional<L2PcInstance> ptLeader = playersNearTower.stream().filter(p -> p.getParty() != null && p.getParty().isLeader(p)).findAny();
//		//		if (ptLeader.isPresent()) {
//		//
//		//			L2Party pt = ptLeader.get().getParty();
//		//			List<L2PcInstance> restOfPlayer = playersNearTower.stream().filter(p -> !pt.getMembers().contains(p)).collect(Collectors.toList());
//		//
//		//			if (!restOfPlayer.isEmpty()) {
//		//				if (ptLeader.get().getClan() == null) {
//		//					return null;
//		//				}
//		//				if (restOfPlayer.stream().anyMatch(p -> p.getClan() == null || !p.getClan().isMember(ptLeader.get().getObjectId()))) {
//		//					return null;
//		//				}
//		//
//		//			}
//		//			return ptLeader.get();
//		//		}
//
//		return null;
