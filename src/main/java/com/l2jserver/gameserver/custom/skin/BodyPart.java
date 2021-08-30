package com.l2jserver.gameserver.custom.skin;

import com.l2jserver.gameserver.network.serverpackets.ExClosePartyRoom;

import java.util.ArrayList;
import java.util.List;

public enum BodyPart {
	HEAVYCHEST("icon.armor_t97_u_i00","Heavy Breastplate"),
	HEAVYLEGS("icon.armor_t97_l_i00","Heavy Gaiters"),
	HEAVYFEET("icon.armor_t97_b_i00","Heavy Boots"),
	HEAVYGLOVES("icon.armor_t97_g_i00","Heavy Gloves"),
	LIGHTCHEST("icon.armor_t98_u_i00","Light Chest"),
	LIGHTLEGS("icon.armor_t98_l_i00","Light Legging"),
	LIGHTFEET("icon.armor_t98_b_i00","Light Boots"),
	LIGHTGLOVES("icon.armor_t98_g_i00","Light Gloves"),
	MAGICCHEST("icon.armor_t99_u_i00","Robe Tunic"),
	MAGICLEGS("icon.armor_t99_l_i00","Robe Stocking"),
	MAGICFEET("icon.armor_t97_b_i00","Robe Shoes"),
	MAGICGLOVES("icon.armor_t99_g_i00","Robe Gloves"),
	SIGIL("icon.weapon_pricklelotus_i01","Sigil"),
	SHIELD("icon.heavengate_shield_i01","Shield"),
	SWORDRHAND("icon.weapon_incessantcore_sword_i01","Sword One Hand"),
	SWORDLRHAND("icon.weapon_lavamond_saw_i00","Sword Two Hand"),
	BLUNTRHAND("icon.weapon_expowder_mace_i00","Blunt One Hand"),
	BLUNTLRHAND("icon.weapon_pereztear_hammer_i00","Blunt Two Hand"),
	DAGGERRHAND("icon.weapon_mambaedge_i00","Dagger One Hand"),
	DUALDAGGERLRHAND("icon.dual_dagger_i00","Dual Daggers"),
	DUALLRHAND("icon.weapon_dual_sword_i00","Dual Blades"),
	DUALFISTLRHAND("icon.weapon_jademice_claw_i00","Dual Fists"),
	POLELRHAND("icon.weapon_ghostcleaner_i00","Pole"),
	BOWLRHAND("icon.weapon_recurvethorne_bow_i00","Bow"),
	CROSSBOWLRHAND("icon.weapon_thorne_crossbow_i00","Crossbow"),
	ANCIENTSWORDLRHAND("icon.weapon_pyseal_blade_i00","Ancientsword"),
	RAPIERRHAND("icon.weapon_heavenstair_rapier_i00","Rapier"),
	ALLDRESS("icon.armor_t2000_ul_i00","Alldress");


		public static final List<BodyPart> weapons = new ArrayList<>();
		public static final List<BodyPart> armor = new ArrayList<>();
		public static final List<BodyPart> shield = new ArrayList<>();
		static{
				weapons.add(SWORDRHAND);
				weapons.add(SWORDLRHAND);
				weapons.add(BLUNTRHAND);
				weapons.add(BLUNTLRHAND);
				weapons.add(DAGGERRHAND);
				weapons.add(DUALDAGGERLRHAND);
				weapons.add(DUALLRHAND);
				weapons.add(DUALFISTLRHAND);
				weapons.add(POLELRHAND);
				weapons.add(BOWLRHAND);
				weapons.add(CROSSBOWLRHAND);
				weapons.add(ANCIENTSWORDLRHAND);
				weapons.add(RAPIERRHAND);

				armor.add(HEAVYCHEST);
				armor.add(HEAVYLEGS);
				armor.add(HEAVYFEET);
				armor.add(HEAVYGLOVES);
				armor.add(LIGHTCHEST);
				armor.add(LIGHTLEGS);
				armor.add(LIGHTFEET);
				armor.add(LIGHTGLOVES);
				armor.add(MAGICCHEST);
				armor.add(MAGICLEGS);
				armor.add(MAGICFEET);
				armor.add(MAGICGLOVES);

				shield.add(SIGIL);
				shield.add(SHIELD);

		}
		private final String icon;
		private final String name;
		BodyPart(String icon,String name){
				this.icon = icon;
				this.name= name;
		}
		public String getStandardIcon(){
				return this.icon;
		}

		public String getName() {
			return name;
		}
		public static boolean isArmor(BodyPart part){
			return armor.contains(part);
		}

		public static boolean isWeapon(BodyPart part) {
			return weapons.contains(part);
		}

		public static boolean isShield(BodyPart part) {
			return shield.contains(part);
		}

		public static boolean isKaemelWeapon (BodyPart part){
			return part == RAPIERRHAND || part == ANCIENTSWORDLRHAND || part == CROSSBOWLRHAND;
		}

}
