package com.l2jserver.gameserver.model.items;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.items.type.ArmorType;
import com.l2jserver.gameserver.model.items.type.ItemType;
import com.l2jserver.gameserver.model.items.type.ItemType1;
import com.l2jserver.gameserver.model.items.type.ItemType2;

public class L2ArmorSkin extends L2Item{
	/**
	 * Constructor of the L2Item that fill class variables.<BR>
	 * <BR>
	 *
	 * @param set : StatsSet corresponding to a set of couples (key,value) for description of the item
	 */
	private ArmorType _type;
	public L2ArmorSkin(StatsSet set) {
		super(set);
		_type = set.getEnum("armor_type", ArmorType.class, ArmorType.NONE);

		int _bodyPart = getBodyPart();
		if ((_bodyPart == L2Item.SLOT_NECK) || ((_bodyPart & L2Item.SLOT_L_EAR) != 0) || ((_bodyPart & L2Item.SLOT_L_FINGER) != 0) || ((_bodyPart & L2Item.SLOT_R_BRACELET) != 0) || ((_bodyPart & L2Item.SLOT_L_BRACELET) != 0)) {
			_type1 = ItemType1.WEAPON_RING_EARRING_NECKLACE;
			_type2 = ItemType2.ACCESSORY;
		} else {
			if ((_type == ArmorType.NONE) && (getBodyPart() == L2Item.SLOT_L_HAND)) {
				_type = ArmorType.SHIELD;
			}
			_type1 = ItemType1.SHIELD_ARMOR;
			_type2 = ItemType2.SHIELD_ARMOR;
		}

	}

	@Override public ItemType getItemType() {
		return _type;
	}

	@Override public int getItemMask() {
		return _type.mask();
	}
}
