/*
 * Copyright © 2004-2021 L2J Server
 *
 * This file is part of L2J Server.
 *
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.network.serverpackets;

import static com.l2jserver.gameserver.config.Configuration.general;

import com.l2jserver.gameserver.custom.skin.SkinManager;
import com.l2jserver.gameserver.custom.skin.Visibility;
import com.l2jserver.gameserver.data.json.ExperienceData;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jserver.gameserver.instancemanager.TerritoryWarManager;
import com.l2jserver.gameserver.model.Elementals;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.skills.AbnormalVisualEffect;
import com.l2jserver.gameserver.model.zone.ZoneId;

public final class UserInfo extends L2GameServerPacket {
	private final L2PcInstance _activeChar;
	private int _relation;
	private final int _airShipHelm;

	private final int _runSpd, _walkSpd;
	private final int _swimRunSpd, _swimWalkSpd;
	private final int _flyRunSpd, _flyWalkSpd;
	private final double _moveMultiplier;

	public UserInfo(L2PcInstance cha) {
		_activeChar = cha;

		int _territoryId = TerritoryWarManager.getInstance().getRegisteredTerritoryId(cha);
		_relation = _activeChar.isClanLeader() ? 0x40 : 0;
		if (_activeChar.getSiegeState() == 1) {
			if (_territoryId == 0) {
				_relation |= 0x180;
			} else {
				_relation |= 0x1000;
			}
		}
		if (_activeChar.getSiegeState() == 2) {
			_relation |= 0x80;
		}
		// _isDisguised = TerritoryWarManager.getInstance().isDisguised(character.getObjectId());
		if (_activeChar.isInAirShip() && _activeChar.getAirShip().isCaptain(_activeChar)) {
			_airShipHelm = _activeChar.getAirShip().getHelmItemId();
		} else {
			_airShipHelm = 0;
		}

		_moveMultiplier = cha.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(cha.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(cha.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(cha.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(cha.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = cha.isFlying() ? _runSpd : 0;
		_flyWalkSpd = cha.isFlying() ? _walkSpd : 0;
	}

	@Override protected void writeImpl() {
		writeC(0x32);

		writeD(_activeChar.getX());
		writeD(_activeChar.getY());
		writeD(_activeChar.getZ());
		writeD(_activeChar.getVehicle() != null ? _activeChar.getVehicle().getObjectId() : 0);

		writeD(_activeChar.getObjectId());
		writeS(_activeChar.getAppearance().getVisibleName());
		writeD(_activeChar.getRace().ordinal());
		writeD(_activeChar.getAppearance().getSex() ? 1 : 0);

		writeD(_activeChar.getBaseClass());

		writeD(_activeChar.getLevel());
		writeQ(_activeChar.getExp());
		writeF(ExperienceData.getInstance().getPercentFromCurrentLevel(_activeChar.getExp(), _activeChar.getLevel()));
		writeD(_activeChar.getSTR());
		writeD(_activeChar.getDEX());
		writeD(_activeChar.getCON());
		writeD(_activeChar.getINT());
		writeD(_activeChar.getWIT());
		writeD(_activeChar.getMEN());
		writeD(_activeChar.getMaxHp());
		writeD((int) Math.round(_activeChar.getCurrentHp()));
		writeD(_activeChar.getMaxMp());
		writeD((int) Math.round(_activeChar.getCurrentMp()));
		writeD(_activeChar.getSp());
		writeD(_activeChar.getCurrentLoad());
		writeD(_activeChar.getMaxLoad());

		writeD(_activeChar.getActiveWeaponItem() != null ? 40 : 20); // 20 no weapon, 40 weapon equipped

		for (int slot : getPaperdollOrder()) {
			writeD(_activeChar.getInventory().getPaperdollObjectId(slot));
		}

		for (int slot : getPaperdollOrder()) {
			//			int itemId = _activeChar.getInventory().getPaperdollItemDisplayId(slot);
			L2ItemInstance equippedItem = _activeChar.getInventory().getEquipedItemBySlot(slot);
			int actualItem = 0;
			Visibility v = SkinManager.getInstance().isEnabled(_activeChar);
			if (equippedItem != null && (v== Visibility.MINE || v == Visibility.ALL)) {
				actualItem = SkinManager.getInstance().getWearingSkin(_activeChar, equippedItem);
			}
			if((actualItem == -1 || actualItem==0) && equippedItem!=null){
					actualItem = equippedItem.getDisplayId();
			}
			writeD(actualItem);
		}

		for (int slot : getPaperdollOrder()) {
			writeD(_activeChar.getInventory().getPaperdollAugmentationId(slot));
		}

		writeD(_activeChar.getInventory().getTalismanSlots());
		writeD(_activeChar.getInventory().canEquipCloak() ? 1 : 0);
		writeD((int) _activeChar.getPAtk(null));
		writeD((int) _activeChar.getPAtkSpd());
		writeD((int) _activeChar.getPDef(null));
		writeD(_activeChar.getEvasionRate(null));
		writeD(_activeChar.getAccuracy());
		writeD(_activeChar.getCriticalHit(null, null));
		writeD((int) _activeChar.getMAtk(null, null));

		writeD(_activeChar.getMAtkSpd());
		writeD((int) _activeChar.getPAtkSpd());

		writeD((int) _activeChar.getMDef(null, null));

		writeD(_activeChar.getPvpFlag());
		writeD(_activeChar.getKarma());

		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimRunSpd);
		writeD(_swimWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);
		writeF(_moveMultiplier);
		writeF(_activeChar.getAttackSpeedMultiplier());

		writeF(_activeChar.getCollisionRadius());
		writeF(_activeChar.getCollisionHeight());

		writeD(_activeChar.getAppearance().getHairStyle());
		writeD(_activeChar.getAppearance().getHairColor());
		writeD(_activeChar.getAppearance().getFace());
		writeD(_activeChar.isGM() ? 1 : 0); // builder level

		String title = _activeChar.getTitle();
		if (_activeChar.isGM() && _activeChar.isInvisible()) {
			title = "Invisible";
		}
		if (_activeChar.getPoly().isMorphed()) {
			final L2NpcTemplate polyObj = NpcData.getInstance().getTemplate(_activeChar.getPoly().getPolyId());
			if (polyObj != null) {
				title += " - " + polyObj.getName();
			}
		}
		writeS(title);

		writeD(_activeChar.getClanId());
		writeD(_activeChar.getClanCrestId());
		writeD(_activeChar.getAllyId());
		writeD(_activeChar.getAllyCrestId()); // ally crest id
		// 0x40 leader rights
		// siege flags: attacker - 0x180 sword over name, defender - 0x80 shield, 0xC0 crown (|leader), 0x1C0 flag (|leader)
		writeD(_relation);
		writeC(_activeChar.getMountType().ordinal()); // mount type
		writeC(_activeChar.getPrivateStoreType().getId());
		writeC(_activeChar.hasDwarvenCraft() ? 1 : 0);
		writeD(_activeChar.getPkKills());
		writeD(_activeChar.getPvpKills());

		writeH(_activeChar.getCubics().size());
		for (int cubicId : _activeChar.getCubics().keySet()) {
			writeH(cubicId);
		}

		writeC(_activeChar.isInPartyMatchRoom() ? 1 : 0);

		writeD(_activeChar.isInvisible() ? _activeChar.getAbnormalVisualEffects() | AbnormalVisualEffect.STEALTH.getMask() : _activeChar.getAbnormalVisualEffects());
		writeC(_activeChar.isInsideZone(ZoneId.WATER) ? 1 : _activeChar.isFlyingMounted() ? 2 : 0);

		writeD(_activeChar.getClanPrivileges().getBitmask());

		writeH(_activeChar.getRecomLeft()); // c2 recommendations remaining
		writeH(_activeChar.getRecomHave()); // c2 recommendations received
		writeD(_activeChar.getMountNpcId() > 0 ? _activeChar.getMountNpcId() + 1000000 : 0);
		writeH(_activeChar.getInventoryLimit());

		writeD(_activeChar.getClassId().getId());
		writeD(0x00); // special effects? circles around player...
		writeD(_activeChar.getMaxCp());
		writeD((int) _activeChar.getCurrentCp());
		writeC(_activeChar.isMounted() || (_airShipHelm != 0) ? 0 : _activeChar.getEnchantEffect());

		writeC(_activeChar.getTeam().getId());

		writeD(_activeChar.getClanCrestLargeId());
		writeC(_activeChar.isNoble() ? 1 : 0); // 0x01: symbol on char menu ctrl+I
		writeC(_activeChar.isHero() || (_activeChar.isGM() && general().gmHeroAura()) ? 1 : 0); // 0x01: Hero Aura

		writeC(_activeChar.isFishing() ? 1 : 0); // Fishing Mode
		writeD(_activeChar.getFishx()); // fishing x
		writeD(_activeChar.getFishy()); // fishing y
		writeD(_activeChar.getFishz()); // fishing z
		writeD(_activeChar.getAppearance().getNameColor());

		// new c5
		writeC(_activeChar.isRunning() ? 0x01 : 0x00); // changes the Speed display on Status Window

		writeD(_activeChar.getPledgeClass()); // changes the text above CP on Status Window
		writeD(_activeChar.getPledgeType());

		writeD(_activeChar.getAppearance().getTitleColor());

		writeD(_activeChar.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_activeChar.getCursedWeaponEquippedId()) : 0);

		// T1 Starts
		writeD(_activeChar.getTransformationDisplayId());

		byte attackAttribute = _activeChar.getAttackElement();
		writeH(attackAttribute);
		writeH(_activeChar.getAttackElementValue(attackAttribute));
		writeH(_activeChar.getDefenseElementValue(Elementals.FIRE));
		writeH(_activeChar.getDefenseElementValue(Elementals.WATER));
		writeH(_activeChar.getDefenseElementValue(Elementals.WIND));
		writeH(_activeChar.getDefenseElementValue(Elementals.EARTH));
		writeH(_activeChar.getDefenseElementValue(Elementals.HOLY));
		writeH(_activeChar.getDefenseElementValue(Elementals.DARK));

		writeD(_activeChar.getAgathionId());

		// T2 Starts
		writeD(_activeChar.getFame()); // Fame
		writeD(_activeChar.isMinimapAllowed() ? 1 : 0); // Minimap on Hellbound
		writeD(_activeChar.getVitalityPoints()); // Vitality Points
		writeD(_activeChar.getAbnormalVisualEffectSpecial());
		// writeD(_territoryId); // CT2.3
		// writeD((_isDisguised ? 0x01: 0x00)); // CT2.3
		// writeD(_territoryId); // CT2.3
	}
}
