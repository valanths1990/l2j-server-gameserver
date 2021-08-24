package com.l2jserver.gameserver.custom.skin;

public enum BodyPart {
	HEAVYCHEST("icon.armor_t97_u_i00"),
	HEAVYLEGS("icon.armor_t97_l_i00"),
	HEAVYFEET("icon.armor_t97_b_i00"),
	HEAVYGLOVES("icon.armor_t97_g_i00"),
	LIGHTCHEST("icon.armor_t98_u_i00"),
	LIGHTLEGS("icon.armor_t98_l_i00"),
	LIGHTFEET("icon.armor_t98_b_i00"),
	LIGHTGLOVES("icon.armor_t98_g_i00"),
	MAGICCHEST("icon.armor_t99_u_i00"),
	MAGICLEGS("icon.armor_t99_l_i00"),
	MAGICFEET("icon.armor_t97_b_i00"),
	MAGICGLOVES("icon.armor_t99_g_i00"),
	SIGIL("icon.weapon_pricklelotus_i01"),
	SHIELD("icon.heavengate_shield_i01"),
	SWORDRHAND("icon.weapon_incessantcore_sword_i01"),
	SWORDLRHAND("icon.weapon_lavamond_saw_i00"),
	BLUNTRHAND("icon.weapon_expowder_mace_i00"),
	BLUNTLRHAND("icon.weapon_pereztear_hammer_i00"),
	DAGGERRHAND("icon.weapon_mambaedge_i00"),
	DUALDAGGERLRHAND("icon.dual_dagger_i00"),
	DUALLRHAND("icon.weapon_dual_sword_i00"),
	DUALFISTLRHAND("icon.weapon_jademice_claw_i00"),
	POLELRHAND("icon.weapon_ghostcleaner_i00"),
	BOWLRHAND("icon.weapon_recurvethorne_bow_i00"),
	CROSSBOWLRHAND("icon.weapon_thorne_crossbow_i00"),
	ANCIENTSWORDLRHAND("icon.weapon_pyseal_blade_i00"),
	RAPIERRHAND("icon.weapon_heavenstair_rapier_i00"),
	ALLDRESS("icon.armor_t2000_ul_i00"),
	ONEPIECE("");
		private final String icon;
		BodyPart(String icon){
				this.icon = icon;
		}
		public String getStandardIcon(){
				return this.icon;
		}
}
