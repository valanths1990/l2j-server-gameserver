package com.l2jserver.gameserver.custom.skin;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.*;
import java.util.stream.Collectors;

public class SkinHandler implements IBypassHandler {

	private String[] COMMANDS = { "skin;homepage" };
	private final String weaponTable = "<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\" >\n" + "<tr>\n" + "<td align=\"center\" width=\"120\" height=\"60\">\n" + "<button name=\"\" bypass=\"bypass skin;homepage sword rhand\" back=\"%swordrhand%\" fore=\"%swordrhand%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Sword One Hand</font>\n" + "</td>\n" + "<td align=\"center\" width=\"120\" height=\"60\">\n" + "<button name=\"\" bypass=\"bypass skin;homepage sword lrhand\" back=\"%swordlrhand%\" fore=\"%swordlrhand%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Sword Two Hand</font>\n" + "</td>\n" + "<td align=\"center\" width=\"120\" height=\"60\">\n" + "<button name=\"\" bypass=\"bypass skin;homepage blunt rhand\" back=\"%bluntrhand%\" fore=\"%bluntrhand%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Blunt One Hand</font>\n" + "</td>\n" + "<td align=\"center\" width=\"120\" height=\"60\">\n" + "   <button name=\"\" bypass=\"bypass skin;homepage blunt lrhand\" back=\"%bluntlrhand%\" fore=\"%bluntlrhand%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Blunt Two Hand</font>\n" + "</td>\n" + "<td align=\"center\" width=\"120\" height=\"60\">\n" + "   <button name=\"\" bypass=\"bypass skin;homepage dagger rhand\" back=\"%daggerrhand%\" fore=\"%daggerrhand%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Dagger</font>\n" + "</td>\n" + "</tr>\n" + "<tr>\n" + "<td align=\"center\" width=\"120\" height=\"60\">\n" + "   <button name=\"\" bypass=\"bypass skin;homepage pole lrhand\" back=\"%polelrhand%\" fore=\"%polelrhand%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Pole</font>\n" + "</td>\n" + "<td align=\"center\" width=\"120\" height=\"60\">\n" + "   <button name=\"\" bypass=\"bypass skin;homepage bow lrhand\" back=\"%bowlrhand%\" fore=\"%bowlrhand%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Bow</font>\n" + "</td>\n" + "<td align=\"center\" width=\"120\" height=\"60\">\n" + "   <button name=\"\" bypass=\"bypass skin;homepage dual lrhand \" back=\"%duallrhand%\" fore=\"%duallrhand%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Dual Blades</font>\n" + "</td>\n" + "<td align=\"center\" width=\"120\" height=\"60\">\n" + "<button name=\"\" bypass=\"bypass skin;homepage dualdagger lrhand\" back=\"%dualdaggerlrhand%\" fore=\"%dualdaggerlrhand%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Dual Daggers</font>\n" + "</td>\n" + "</tr>\n" + "</table>";
	private final String kamaelWeapons = "<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\">\n" + "<tr>\n" + "<td align=\"center\" width=\"120\" height=\"60\">\n" + "<button name=\"\" bypass=\"bypass skin;homepage rapier rhand\" back=\"%rapierrhand%\" fore=\"%rapierrhand%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Rapier</font>\n" + "</td>\n" + "<td align=\"center\" width=\"120\" height=\"60\">\n" + "<button name=\"\" bypass=\"bypass skin;homepage ancientsword lrhand\" back=\"%ancientswordlrhand%\" fore=\"%ancientswordlrhand%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Ancient Sword</font>\n" + "</td>\n" + "<td align=\"center\" width=\"120\" height=\"60\">\n" + "<button name=\"\" bypass=\"bypass skin;homepage crossbow lrhand\" back=\"%crossbowlrhand%\" fore=\"%crossbowlrhand%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Sword One Hand</font>\n" + "</td>\n" + "</tr>\n" + "</table>";
	private final String armorRow = "<td align=\"center\" width=\"120\" height=\"60\">\n" + "<button name=\"\" bypass=\"bypass skin;homepage %armorTypeBypass% armor\" back=\"%%armorIcon%\" fore=\"%armorIcon%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">%armorType% Armor</font>\n" + "</td>";
	private final String shieldRow = "<tr>\n" + "<td align=\"center\" width=\"100\" height=\"60\">\n" + "<button name=\"\" bypass=\"bypass skin;homepage shield\" back=\"%icon%\" fore=\"%icon%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Shield</font>\n" + "</td>\n" + "<td align=\"center\" width=\"100\" height=\"60\">\n" + "<button name=\"\" bypass=\"bypass skin;homepage sigil\" back=\"%icon%\" fore=\"%icon%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fca503\">Sigil</font>\n" + "</td>\n" + "</tr>";
	private final String costumeRow = "<tr>\n" + "<td align=\"center\" width=\"100\" height=\"60\">\n" + "<button name=\"\" bypass=\"bypass skin;homepage costume\" back=\"%icon%\" fore=\"%icon%\" width=32 height=32>\n" + "<font name=\"hs19\" color=\"fc030f\">Costume</font>\n" + "</td>\n" + "</tr>";
	private static final Set<BodyPart> excludeArmor;
	private static final Set<BodyPart> excludeWeapon;

	static {
		excludeArmor = Arrays.stream(BodyPart.values()).filter(p -> p.name().matches("[a-z]+_[a-z]+|alldress|onepiece|shield|sigil")).collect(Collectors.toSet());
		excludeWeapon = Arrays.stream(BodyPart.values()).filter(p -> !p.name().matches("[a-z]+_[a-z]+|alldress|onepiece|shield|sigil")).collect(Collectors.toSet());
	}

	@Override public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
		CommunityBoardHandler.separateAndSend(getHomepage(activeChar), activeChar);
		return false;
	}

	private String getHomepage(L2PcInstance pc) {

		String html = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/skin/html/homepage.html");

//		List<SkinHolder> playerSkinList = SkinManager.getInstance().getPlayersAllSkin(pc);
//		String tmpTable = weaponTable;
//
//		Map<BodyPart, Integer> playerWearingWeapons = new HashMap<>(SkinManager.getInstance().getPlayerWearingSkin(pc).getPlayersWearingSkins());
//		playerWearingWeapons.keySet().removeAll(excludeArmor);
//
//		for (Map.Entry<BodyPart, Integer> e : playerWearingWeapons.entrySet()) {
//			String icon = StandardIcons.getIcon(e.getKey()).getIcon();
//			if (e.getValue() > 0) {
//				icon = playerSkinList.stream().filter(s -> s.getSkinId() == e.getValue()).findFirst().map(SkinHolder::getIcon).orElse("");
//			}
//			tmpTable = tmpTable.replaceAll("%" + e.getKey().name() + "%", icon); //temp
//		}
//
//		html = html.replaceFirst("%weapon%", tmpTable);
//
//		if (pc.getClassId().getRace() == Race.KAMAEL) {
//			tmpTable = kamaelWeapons;
//			String icon = StandardIcons.getIcon(BodyPart.ancientswordlrhand).getIcon();
//			int ancientswordId = playerWearingWeapons.get(BodyPart.ancientswordlrhand);
//			if (ancientswordId > 0) {
//				icon = playerSkinList.stream().filter(s -> s.getSkinId() == ancientswordId).findFirst().map(SkinHolder::getIcon).orElse("");
//			}
//			tmpTable = tmpTable.replaceAll("%ancientswordlrhand%", icon);
//
//			icon = StandardIcons.getIcon(BodyPart.rapierrhand).getIcon();
//			int rapierId = playerWearingWeapons.get(BodyPart.rapierrhand);
//			if (rapierId > 0) {
//				icon = playerSkinList.stream().filter(s -> s.getSkinId() == rapierId).findFirst().map(SkinHolder::getIcon).orElse("");
//			}
//			tmpTable = tmpTable.replaceAll("%rapierrhand%", icon);
//
//			icon = StandardIcons.getIcon(BodyPart.crossbowlrhand).getIcon();
//			int crossbowId = playerWearingWeapons.get(BodyPart.crossbowlrhand);
//			if (crossbowId > 0) {
//				icon = playerSkinList.stream().filter(s -> s.getSkinId() == crossbowId).findFirst().map(SkinHolder::getIcon).orElse("");
//			}
//			tmpTable = tmpTable.replaceAll("%crossbowlrhand%", icon);
//			html = html.replaceFirst("%kamael%", tmpTable);
//		}
//
//		Map<BodyPart, Integer> playerWearingArmor = new HashMap<>(SkinManager.getInstance().getPlayerWearingSkin(pc).getPlayersWearingSkins());
//		playerWearingArmor.keySet().removeAll(excludeWeapon);
//
//		tmpTable = armorRow;
//		String finalArmorResult = "";
//		if (pc.getRace() != Race.KAMAEL) {
//			String icon = StandardIcons.heavy.getIcon();
//			int heavyChestId = playerWearingArmor.get(BodyPart.heavy_chest);
//			if (heavyChestId > 0) {
//				icon = playerSkinList.stream().filter(s -> s.getSkinId() == heavyChestId).findFirst().map(SkinHolder::getIcon).orElse("");
//			}
//			tmpTable = tmpTable.replaceAll("%armorIcon%", icon).replaceAll("%armorType%", "Heavy").replaceAll("%armorTypeBypass", "heavy");
//			finalArmorResult += tmpTable;
//			tmpTable = armorRow;
//			icon = StandardIcons.robe.getIcon();
//			int robeId = playerWearingArmor.get(BodyPart.heavy_chest);
//			if (robeId > 0) {
//				icon = playerSkinList.stream().filter(s -> s.getSkinId() == robeId).findFirst().map(SkinHolder::getIcon).orElse("");
//			}
//			tmpTable = tmpTable.replaceAll("%armorIcon%", icon).replaceAll("%armorType%", "Robe").replaceAll("%armorTypeBypass", "robe");
//			finalArmorResult += tmpTable;
//			tmpTable = armorRow;
//
//		}
//		String icon = StandardIcons.light.getIcon();
//		int lightId = playerWearingArmor.get(BodyPart.light_chest);
//		if (lightId > 0) {
//			icon = playerSkinList.stream().filter(s -> s.getSkinId() == lightId).findFirst().map(SkinHolder::getIcon).orElse("");
//		}
//		tmpTable = tmpTable.replaceAll("%armorIcon%", icon).replaceAll("%armorType%", "Light").replaceAll("%armorTypeBypass%", "light");
//		finalArmorResult += tmpTable;
//		html = html.replace("%armor%", finalArmorResult);
//
//		tmpTable = costumeRow;
//		icon = StandardIcons.alldress.getIcon();
//
//		int costoumeId = playerWearingArmor.get(BodyPart.alldress);
//		if (costoumeId > 0) {
//			icon = playerSkinList.stream().filter(s -> s.getSkinId() == costoumeId).findFirst().map(SkinHolder::getIcon).orElse("");
//		}
//		tmpTable =tmpTable.replaceAll("%icon%",icon);
//
//		html = html.replace("%costume%",tmpTable);

		return html;
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}
}
