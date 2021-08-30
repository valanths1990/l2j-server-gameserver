package com.l2jserver.gameserver.model.items;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.items.type.ItemType;
import com.l2jserver.gameserver.model.items.type.ItemType1;
import com.l2jserver.gameserver.model.items.type.ItemType2;
import com.l2jserver.gameserver.model.items.type.WeaponType;

public class L2WeaponSkin extends L2Item {
	private final WeaponType _type;
	private final boolean _isMagicWeapon;

	public L2WeaponSkin(StatsSet set) {
		super(set);
		_type = WeaponType.valueOf(set.getString("weapon_type", "none").toUpperCase());
		_type1 = ItemType1.WEAPON_RING_EARRING_NECKLACE;
		_type2 = ItemType2.WEAPON;
		_isMagicWeapon = set.getBoolean("is_magic_weapon", false);
	}

	@Override
	public ItemType getItemType() {
		return _type;
	}

	@Override public int getItemMask() {
		return _type.mask();
	}

	@Override
	public boolean isMagicWeapon(){
		return _isMagicWeapon;
	}
}
