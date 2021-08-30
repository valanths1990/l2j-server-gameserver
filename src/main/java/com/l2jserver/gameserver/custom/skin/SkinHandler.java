package com.l2jserver.gameserver.custom.skin;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.enums.HtmlActionScope;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.PageResult;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.util.HtmlUtil;
import org.w3c.dom.html.HTMLTableCaptionElement;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SkinHandler implements IBypassHandler {

	private List<String> COMMANDS = new ArrayList<>();
	private final String tableHead = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">";
	private final String tableEnd = "</table>";
	private final String tableRow = "<tr>";
	private final String tableRowEnd = "</tr>";
	private final String tableCell = "<td align=\"center\">";
	private final String tableCellEnd = "</td>";
	private final String subMenuButton = "<button name=\"\" action=\"bypass skin;homepage submenu %bodypart%\" back=\"%itemIcon%\" fore=\"%itemIcon%\" width=32 height=32><font name=\"hs19\" color=\"fca503\">%itemName%</font>";
	private final String useSkinButton = " <button value=\"use\" action=\"bypass skin;%bodyPart% use %skinId% %pageNumber%\" width=50 height=20 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">";
	private final String skinImage = "<img src=\"%icon%\" height=\"32\" width=\"32\">";
	private final int CELLPERROW = 5;
	private final String pageString = " <a action=\"bypass skin;%bodyPart% submenu %pageNumber%\"><font name=\"hs22\" color=\"fca503\">%pageNumber%</font></a>";

	public SkinHandler() {
		Arrays.stream(BodyPart.values()).forEach(b -> COMMANDS.add("skin;" + b.name()));
		COMMANDS.add("skin;homepage");
	}

	@Override public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {

				String htmlResult = "";
				String[] splitted = command.split(" ");

				if (splitted.length <= 1) {
					htmlResult = getHomepage(activeChar);
				}
				if (splitted.length >= 3) {

					if (splitted[1].equals("submenu")) {

						BodyPart bodyPart = BodyPart.valueOf(splitted[2]);
						htmlResult = getSubMenu(activeChar, bodyPart, 0);

					}
					if (splitted[1].equals("open")) {

						//				BodyPart bodyPart = BodyPart.valueOf(splitted[2]);
						//				htmlResult = getSubMenu(activeChar,bodyPart);

					}

				}
		CommunityBoardHandler.separateAndSend(htmlResult, activeChar);
		boolean testMode= false;
		if(testMode){

		if(command.contains("use")){
			useSkin(activeChar,command.split(" ")[2],BodyPart.HEAVYCHEST);
		}
		StringBuilder sb = new StringBuilder();

		sb.append(tableHead);
		sb.append(tableRow);

		List<SkinHolder> skins = SkinManager.getInstance().getPlayersSkinsByBodyPart(activeChar, BodyPart.HEAVYCHEST);
		AtomicInteger counter = new AtomicInteger(0);
		skins.stream().forEach(s -> {
			sb.append(tableCell);
			sb.append(skinImage.replace("%icon%",s.getIcon()));
			sb.append(useSkinButton.replace("%bodyPart%", BodyPart.HEAVYCHEST.name()).replace("%skinId%", String.valueOf(s.getSkinId())).replace("%pageNumber%", "0"));
			sb.append(tableCellEnd);
			if(counter.getAndIncrement()%5==0){
				sb.append(tableRowEnd);
				sb.append(tableRow);
			}
		});
		sb.append(tableRowEnd);
		sb.append(tableEnd);
		String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/custom/skin/html/allskins.html");
		html = html.replace("%skins%", sb.toString());
		CommunityBoardHandler.separateAndSend(html, activeChar);
		}

		return false;
	}

	private String useSkin(L2PcInstance pc, String skinId, BodyPart bodypart) {

		Optional<L2Item> i = SkinManager.getInstance().getAllSkins(bodypart).stream().filter(item -> item.getId() == Integer.parseInt(skinId)).findFirst();

		Optional<SkinHolder> s = i.map(item -> new SkinHolder(pc.getObjectId(), item.getId(), bodypart, item.getIcon()));
		s.ifPresent(ss -> SkinManager.getInstance().getPlayerWearingSkins(pc).changeSkin(bodypart, ss));
	pc.broadcastUserInfo();
	pc.broadcastUserInfo();
		return "";
	}

	private String getSubMenu(L2PcInstance pc, BodyPart bodyPart, int page) {

		String html = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/skin/html/submenu.html");
		SkinHolder s = SkinManager.getInstance().getPlayerWearingSkins(pc).getPlayersWearingSkins().get(bodyPart);
		html = html.replace("%currentSkin%", skinImage.replace("%icon%", s == null ? bodyPart.getStandardIcon() : s.getIcon()));

		List<SkinHolder> playersSkins = SkinManager.getInstance().getPlayersSkinsByBodyPart(pc, bodyPart);

		PageResult pr = HtmlUtil.createPage(playersSkins, page, CELLPERROW, i -> {
			return "<td width=\"40\" align=\"center\">" + pageString.replaceAll("%pageNumber%", String.valueOf(i)).replace("%bodyPart%", bodyPart.name()) + "</a></td>";
		}, skin -> {
			StringBuilder sb = new StringBuilder();
			sb.append(tableCell);
			sb.append(skinImage.replace("%icon%", skin.getIcon()));
			sb.append(useSkinButton.replace("%bodyPart%", bodyPart.name()).replace("%skinId%", String.valueOf(skin.getSkinId())).replace("%pageNumber%", String.valueOf(page)));
			sb.append(tableCellEnd);
			return sb.toString();
		});

		String[] tdArray = pr.getBodyTemplate().toString().split("<td>");

		html = html.replace("%pages%", pr.getPagerTemplate().toString());

		return html;
	}

	//	private boolean buySkin(L2PcInstance pc,int id){
	//		return false;
	//	}
	private String getHomepage(L2PcInstance pc) {

		String html = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/skin/html/homepage.html");

		Map<BodyPart, SkinHolder> playerWearingSkins = SkinManager.getInstance().getPlayerWearingSkins(pc).getPlayersWearingSkins();

		StringBuilder table = new StringBuilder();
		AtomicInteger counter = new AtomicInteger(0);
		table.append(tableHead);
		table.append(tableRow);
		BodyPart.weapons.stream().filter(b -> !BodyPart.isKaemelWeapon(b)).forEach(b -> {  /// replace with bodypart list of wepaos
			table.append(tableCell);
			SkinHolder s = playerWearingSkins.get(b);
			String button = subMenuButton;
			if (s != null) {
				button = button.replaceAll("%itemIcon%", s.getIcon());
			}
			button = button.replaceAll("%itemIcon%", b.getStandardIcon()).replace("%itemName%", b.getName()).replace("%bodypart%", b.name());
			table.append(button);
			table.append(tableCellEnd);
			if (counter.getAndIncrement() == CELLPERROW) {
				table.append(tableRowEnd);
				table.append(tableRow);
			}
		});
		table.append(tableRowEnd);
		table.append(tableEnd);

		html = html.replace("%nonKamaelWeapons%", table.toString());

		if (pc.getRace() == Race.KAMAEL) {
			table.setLength(0);

			table.append(tableHead);
			table.append(tableRow);
			BodyPart.weapons.stream().filter(BodyPart::isKaemelWeapon).forEach(b -> {
				table.append(tableCell);
				String button = subMenuButton;
				SkinHolder s = playerWearingSkins.get(b);
				if (s != null) {
					button = button.replaceAll("%itemIcon%", s.getIcon());
				}
				button = button.replaceAll("%itemIcon%", b.getStandardIcon()).replace("%itemName%", b.getName()).replace("%bodypart%", b.name());

				table.append(button);
				table.append(tableCellEnd);
				if (counter.getAndIncrement() == CELLPERROW) {
					table.append(tableRowEnd);
					table.append(tableRow);
				}
			});
			table.append(tableRowEnd);
			table.append(tableEnd);
			html = html.replace("%kamaelWeapons%", table.toString());
		}
		table.setLength(0);

		table.append(tableHead);
		table.append(tableRow);
		BodyPart.armor.stream().filter(b -> b.name().matches(".+CHEST")).forEach(b -> {
			table.append(tableCell);
			String button = subMenuButton;
			SkinHolder s = playerWearingSkins.get(b);
			if (pc.getRace() != Race.KAMAEL) {
				if (s != null) {
					button = button.replaceAll("%itemIcon%", s.getIcon());
				}
				button = button.replaceAll("%itemIcon%", b.getStandardIcon()).replace("%itemName%", b.getName()).replace("%bodypart%", b.name());
				table.append(button);
			}

			if (pc.getRace() == Race.KAMAEL && b == BodyPart.LIGHTCHEST) {
				if (s != null) {
					button = button.replaceAll("%itemIcon%", s.getIcon());
				}
				button = button.replaceAll("%itemIcon%", b.getStandardIcon()).replace("%itemName%", b.getName()).replace("%bodypart%", b.name());
				table.append(button);
			}
			table.append(tableCellEnd);
		});
		table.append(tableRowEnd);
		table.append(tableEnd);

		html = html.replace("%armor%", table.toString());
		table.setLength(0);

		table.append(tableHead);
		table.append(tableRow);
		if (pc.getRace() != Race.KAMAEL) {
			BodyPart.shield.forEach(b -> {
				table.append(tableCell);
				String button = subMenuButton;
				SkinHolder s = playerWearingSkins.get(b);
				if (pc.getRace() != Race.KAMAEL) {
					if (s != null) {
						button = button.replaceAll("%itemIcon%", s.getIcon());
					}
					button = button.replaceAll("%itemIcon%", b.getStandardIcon()).replace("%itemName%", b.getName()).replace("%bodypart%", b.name());
					table.append(button);
				}

			});
			table.append(tableCellEnd);
		}
		table.append(tableRowEnd);
		table.append(tableEnd);

		html = html.replace("%shield%", table.toString());

		table.setLength(0);

		table.append(tableHead);
		table.append(tableRow);
		BodyPart.shield.forEach(b -> {
			table.append(tableCell);
			String button = subMenuButton;
			SkinHolder s = playerWearingSkins.get(b);
			if (pc.getRace() != Race.KAMAEL) {
				if (s != null) {
					button = button.replaceAll("%itemIcon%", s.getIcon());
				}
				button = button.replaceAll("%itemIcon%", b.getStandardIcon()).replace("%itemName%", b.getName()).replace("%bodypart%", b.name());
				table.append(button);
			}

		});
		table.append(tableCellEnd);
		table.append(tableRowEnd);
		table.append(tableEnd);

		return html.replaceAll("%.+%", "");
	}

	@Override public String[] getBypassList() {
		return COMMANDS.toArray(new String[0]);
	}
}