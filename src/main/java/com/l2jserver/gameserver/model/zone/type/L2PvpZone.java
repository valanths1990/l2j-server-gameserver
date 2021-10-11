package com.l2jserver.gameserver.model.zone.type;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneExit;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.zone.L2ZoneRespawn;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExSendUIEvent;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class L2PvpZone extends L2ZoneRespawn {
	private final Random rand = new Random();
	private final Skill noblessSkill = SkillData.getInstance().getSkill(1323, 1);

	public L2PvpZone(int id) {
		super(id);
	}

	@Override protected void onEnter(L2Character character) {
		if (!(character instanceof L2PcInstance)) {
			return;
		}
		L2PcInstance pc = character.getActingPlayer();
		pc.sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);
		pc.setInsideZone(ZoneId.PVP, true);
		pc.setInsideZone(ZoneId.PEACE, false);
//		EventDispatcher.getInstance().notifyEventAsync(new OnCreatureZoneEnter(pc, this), Containers.Players());
	}

	@Override protected void onExit(L2Character character) {
		if (!(character instanceof L2PcInstance)) {
			return;
		}
		L2PcInstance pc = (L2PcInstance) character;
		pc.setInsideZone(ZoneId.PVP, false);
		pc.sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);
//		EventDispatcher.getInstance().notifyEventAsync(new OnCreatureZoneExit(pc, this), Containers.Players());
	}

	@Override public void onDieInside(L2Character character) {
		if (character instanceof L2PcInstance) {
			final L2PcInstance pc = ((L2PcInstance) character);
			displayReviveTime(pc);
			ThreadPoolManager.getInstance().scheduleGeneral(() -> {
				if (!pc.isDead()) {
					return;
				}
				pc.doRevive();
				heal(pc);
				Location loc = this.getSpawns().get(rand.nextInt(this.getSpawns().size()));
				pc.teleToLocation(loc, true);
			}, Configuration.customs().getPvpzoneReviveDelay(), TimeUnit.SECONDS);
		}
	}

	@Override public void onReviveInside(L2Character character) {

		if (character instanceof L2PcInstance) {
			L2PcInstance player = ((L2PcInstance) character);
			player.sendPacket(new ExSendUIEvent(player, true, false, 0, 0, null));
			if (noblessSkill != null) {
				noblessSkill.applyEffects(player, player, true, 10800);
				if(character.getSummon()!=null){
					noblessSkill.applyEffects(player.getSummon(), player.getSummon(), true, 10800);
				}
			}
		}
	}

	@Override public void onPlayerLoginInside(L2PcInstance player) {
	}

	@Override public void onPlayerLogoutInside(L2PcInstance player) {
	}

	private void heal(L2Character character) {
		character.setCurrentHp(character.getMaxHp());
		character.setCurrentCp(character.getMaxCp());
		character.setCurrentMp(character.getMaxMp());
	}

	private void displayReviveTime(L2PcInstance player) {
		AtomicInteger reviveTime = new AtomicInteger(Configuration.customs().getPvpzoneReviveDelay());
		int delay = 1000;
		int period = 1000;
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (!player.isDead()) {
					timer.cancel();
				}
				player.sendPacket(new ExShowScreenMessage("Revive in: " + reviveTime.get(), 1000));
				if (reviveTime.decrementAndGet() == 0) {
					timer.cancel();
				}
			}
		}, delay, period);
	}

}
