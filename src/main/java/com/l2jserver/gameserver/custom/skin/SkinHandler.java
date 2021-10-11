package com.l2jserver.gameserver.custom.skin;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.enums.HtmlActionScope;
import com.l2jserver.gameserver.enums.PlayerAction;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.PageResult;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerDlgAnswer;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ConfirmDlg;
import com.l2jserver.gameserver.util.HtmlUtil;
import org.w3c.dom.html.HTMLTableCaptionElement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SkinHandler implements IBypassHandler {

	private List<String> COMMANDS = new ArrayList<>();
	private Map<Integer,SkinHolder> pendingConfirmations=new ConcurrentHashMap<>();
	private Map<L2PcInstance,String> lastBypass = new ConcurrentHashMap<>();
	private final String tableHead = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">";
	private final String tableEnd = "</table>";
	private final String tableRow = "<tr>";
	private final String tableRowEnd = "</tr>";
	private final String tableCell = "<td align=\"center\" width=\"100\" height=\"50\">";
	private final String tableCellEnd = "</td>";
	private final String subMenuButton = "<button name=\"\" action=\"bypass skin;%bodyPart% submenu %bodyPart% 0\" back=\"%itemIcon%\" fore=\"%itemIcon%\" width=32 height=32><font name=\"hs19\" color=\"fca503\">%itemName%</font>";
	private final String useSkinButton = " <button value=\"use\" action=\"bypass skin;%bodyPart% use %skinId% %pageNumber%\" width=50 height=20 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">";
	private final String skinImage = "<img src=\"%icon%\" height=\"32\" width=\"32\">";
	private final int CELLPERROW = 5;
	private final String pageSubMenu = " <a action=\"bypass skin;%bodyPart% submenu %pageNumber%\"><font name=\"hs22\" color=\"fca503\">%pageNumber%</font></a>";
	private final String pageCategory = " <a action=\"bypass skin;%bodyPart% category %bodyPart% %pageNumber%\"><font name=\"hs22\" color=\"fca503\">%pageNumber%</font></a>";
	private final String categoryButton = "<button value=\"\" action=\"bypass skin;homepage category %bodyPart% %pageNumber%\" width=32 height=32 back=\"%icon%\" fore=\"%icon%\"><font name=\"hs19\" color=\"fca503\">%itemName%</font>";
	private final String buySkinButton = "<button value=\"buy\" action=\"bypass skin;%bodyPart% buy %skinId% %pageNumber%\" width=60 height=25 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">";
	private final String tryOutSkinButton ="<button value=\"try out\" action=\"bypass skin;%bodyPart% tryout %skinId% %pageNumber%\" width=60 height=25 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">";

	public SkinHandler() {
		Arrays.stream(BodyPart.values()).forEach(b -> COMMANDS.add("skin;" + b.name()));
		COMMANDS.add("skin;homepage");
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_DLG_ANSWER,this::buySkinConfirmed,this));
	}

	@Override public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
		String htmlResult = "";
		String[] splitted = command.split(" ");
		BodyPart bodyPart = null;
		if (splitted.length <= 1) {
			htmlResult = getHomepage(activeChar);
		}
		if(splitted.length ==2){
			 htmlResult = showCollectionCategories(activeChar,command);
		}
		if (splitted.length >= 3) {

			if (splitted[1].equals("submenu")) {
				 bodyPart = BodyPart.valueOf(splitted[0].split(";")[1]);
				int page = 0;
				if (splitted.length == 3) {
					page = Integer.parseInt(splitted[2]);
				}
				htmlResult = getSubMenu(activeChar, bodyPart, page);
			}
			else if (splitted[1].equals("use")) {
				 bodyPart = BodyPart.valueOf(splitted[0].split(";")[1]);
				useSkin(activeChar, splitted[2], bodyPart);
				htmlResult = getSubMenu(activeChar, bodyPart, Integer.parseInt(splitted[3]));
			}
			else if (splitted[1].equals("category")){
				bodyPart = BodyPart.valueOf(splitted[2]);
				int page = Integer.parseInt(splitted[3]);
				htmlResult = showCategory(activeChar,bodyPart,page);
			}
			else if(splitted[1].equals("buy")){
				bodyPart = BodyPart.valueOf(splitted[0].split(";")[1]);
				buySkin(activeChar, splitted[2], bodyPart);
				htmlResult = showCategory(activeChar, bodyPart, Integer.parseInt(splitted[3]));
				lastBypass.put(activeChar,"skin;homepage category "+ bodyPart.name()+" "+Integer.parseInt(splitted[3]));
			}
			else if(splitted[1].equals("tryout")){
				bodyPart = BodyPart.valueOf(splitted[0].split(";")[1]);
				tryOutSkin(activeChar, splitted[2], bodyPart);
				htmlResult = showCategory(activeChar, bodyPart, Integer.parseInt(splitted[3]));
			}

		}
		CommunityBoardHandler.separateAndSend(htmlResult, activeChar);
		return false;
	}
	private void tryOutSkin(L2PcInstance pc,String skinId,BodyPart bodyPart){
		Optional<L2Item> item = SkinManager.getInstance().getAllSkins(bodyPart).stream().filter(i -> i.getId() == Integer.parseInt(skinId)).findFirst();
		Optional<SkinHolder> skin = item.map(i-> new SkinHolder(pc.getObjectId(),i.getId(),bodyPart,i.getIcon()));
		skin.ifPresent(s -> SkinManager.getInstance().getPlayerWearingSkins(pc).tryOnSkin(bodyPart,s));
		pc.broadcastUserInfo();
		pc.broadcastUserInfo();
	}
	private void buySkinConfirmed(IBaseEvent event){
		OnPlayerDlgAnswer onPlayerDlgAnswer = (OnPlayerDlgAnswer) event;
		if(onPlayerDlgAnswer.getAnswer()<=0 || !pendingConfirmations.containsKey(onPlayerDlgAnswer.getRequesterId())){
			return;
		}
		SkinHolder skin = pendingConfirmations.remove(onPlayerDlgAnswer.getRequesterId());
		SkinManager.getInstance().addNewSkinForPlayer(onPlayerDlgAnswer.getActiveChar(),skin);
		useBypass(lastBypass.remove(onPlayerDlgAnswer.getActiveChar()), onPlayerDlgAnswer.getActiveChar(), null);
	}
	private void buySkin(L2PcInstance pc,String skinId,BodyPart bodyPart){
		Optional<L2Item> item = SkinManager.getInstance().getAllSkins(bodyPart).stream().filter(i -> i.getId() == Integer.parseInt(skinId)).findFirst();
		item.ifPresent(i -> {
			pendingConfirmations.put(pc.getObjectId(),new SkinHolder(pc.getObjectId(),i.getId(),bodyPart,i.getIcon()));
			ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.ARE_YOU_SURE);
			dlg.addRequesterId(pc.getObjectId());
			pc.sendPacket(dlg);
		});
	}
	private String showCategory(L2PcInstance pc, BodyPart bodyPart,int page){
		String html = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(),"data/custom/skin/html/collection.html");
		List<SkinHolder> skins = SkinManager.getInstance().getAllSkins(bodyPart).stream()
			.map(item-> new SkinHolder(pc.getObjectId(),item.getId(),bodyPart,item.getIcon()))
			.filter(s->!SkinManager.getInstance().getPlayersSkinsByBodyPart(pc,bodyPart).contains(s))
			.collect(Collectors.toList());

		PageResult pr = HtmlUtil.createTableWithPages(skins, skins.size(), page, 15, 4, 4, 0, 0, 0, 0, 0, "",
			i -> "<td width=\"40\" height=\"50\" align=\"center\">" + pageCategory.replaceAll("%pageNumber%", String.valueOf(i)).replaceAll("%bodyPart%", bodyPart.name()) + "</a></td>",
			skin -> {
				StringBuilder sb = new StringBuilder();
				sb.append("<td width=\"150\" height=\"80\" align=\"center\">");
				sb.append(skinImage.replace("%icon%", skin.getIcon()));
				sb.append("<table><tr><td>");
				sb.append(buySkinButton.replace("%bodyPart%", bodyPart.name()).replace("%skinId%", String.valueOf(skin.getSkinId())).replace("%pageNumber%", String.valueOf(page)));
				sb.append("</td>");
				sb.append("<td>");
				sb.append(tryOutSkinButton.replace("%bodyPart%", bodyPart.name()).replace("%skinId%", String.valueOf(skin.getSkinId())).replace("%pageNumber%", String.valueOf(page)));
				sb.append("</td></tr></table>");
				sb.append(tableCellEnd);
				return sb.toString();
			});
			html = html.replace("%items%",pr.getBodyTemplate().toString()).replace("%pages%",pr.getPagerTemplate().toString())
					.replace("%collectionName%",bodyPart.getName());

		return html;
	}


	private String showCollectionCategories(L2PcInstance pc,String command){

		String html = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(),"data/custom/skin/html/collectionCategories.html");

			StringBuilder sb = new StringBuilder();
			sb.append(tableRow);
			AtomicInteger counter= new AtomicInteger(1);
			Arrays.stream(BodyPart.values()).forEach(b->{
			sb.append(tableCell);
			sb.append(categoryButton.replaceAll("%icon%",b.getStandardIcon()).replaceAll("%bodyPart%",b.name()).replace("%itemName%",b.getName()).replaceAll("%pageNumber%","0"));
			sb.append(tableCellEnd);
		if(counter.getAndIncrement()%CELLPERROW==0 ){
				sb.append(tableRowEnd);
				sb.append(tableRow);
		}

		});
		sb.append(tableRowEnd);
		html = html.replaceAll("%categories%",sb.toString());
		return html;
	}

	private void useSkin(L2PcInstance pc, String skinId, BodyPart bodypart) {
		Optional<SkinHolder> i = SkinManager.getInstance().getPlayersSkinsByBodyPart(pc,bodypart).stream().filter(item -> item.getSkinId() == Integer.parseInt(skinId)).findFirst();
		i.ifPresent(ss -> SkinManager.getInstance().getPlayerWearingSkins(pc).changeSkin(bodypart, ss));
		pc.broadcastUserInfo();
		pc.broadcastUserInfo();
	}

	private String getSubMenu(L2PcInstance pc, BodyPart bodyPart, int page) {
		String html = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/skin/html/submenu.html");
		SkinHolder s = SkinManager.getInstance().getPlayerWearingSkins(pc).getPlayersWearingSkins().get(bodyPart);
		html = html.replace("%currentSkin%", skinImage.replace("%icon%", s == null ? bodyPart.getStandardIcon() : s.getIcon()));
		List<SkinHolder> playersSkins = SkinManager.getInstance().getPlayersSkinsByBodyPart(pc, bodyPart);
		PageResult pr = HtmlUtil.createTableWithPages(playersSkins, playersSkins.size(), page, 15, 3, 5, 0, 0, 0, 0, 0, "",
			i -> "<td width=\"40\" height=\"50\" align=\"center\">" + pageSubMenu.replaceAll("%pageNumber%", String.valueOf(i)).replace("%bodyPart%", bodyPart.name()) + "</a></td>",
			skin -> {
			StringBuilder sb = new StringBuilder();
			sb.append("<td width=\"80\" height=\"80\" align=\"center\">");
			sb.append(skinImage.replace("%icon%", skin.getIcon()));
			sb.append(useSkinButton.replace("%bodyPart%", bodyPart.name()).replace("%skinId%", String.valueOf(skin.getSkinId())).replace("%pageNumber%", String.valueOf(page)));
			sb.append(tableCellEnd);
			return sb.toString();
		});
		html = html.replace("%pages%", pr.getPagerTemplate().toString()).replace("%items%", pr.getBodyTemplate().toString());
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
			button = button.replaceAll("%itemIcon%", b.getStandardIcon()).replace("%itemName%", b.getName()).replaceAll("%bodyPart%", b.name());
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
				button = button.replaceAll("%itemIcon%", b.getStandardIcon()).replace("%itemName%", b.getName()).replaceAll("%bodyPart%", b.name());

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
			counter.set(1);
		table.append(tableHead);
		table.append(tableRow);
		BodyPart.armor.forEach(b -> {
			table.append(tableCell);
			String button = subMenuButton;
			SkinHolder s = playerWearingSkins.get(b);
			if (pc.getRace() != Race.KAMAEL) {
				if (s != null) {
					button = button.replaceAll("%itemIcon%", s.getIcon());
				}
				button = button.replaceAll("%itemIcon%", b.getStandardIcon()).replace("%itemName%", b.getName()).replaceAll("%bodyPart%", b.name());
				table.append(button);
			}
			if (pc.getRace() == Race.KAMAEL && BodyPart.isLightArmor(b)) {
				if (s != null) {
					button = button.replaceAll("%itemIcon%", s.getIcon());
				}
				button = button.replaceAll("%itemIcon%", b.getStandardIcon()).replace("%itemName%", b.getName()).replaceAll("%bodyPart%", b.name());
				table.append(button);
			}
			table.append(tableCellEnd);
			if(counter.getAndIncrement() % 4 ==0){
				table.append(tableRowEnd);
				table.append(tableRow);
			}
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
					button = button.replaceAll("%itemIcon%", b.getStandardIcon()).replace("%itemName%", b.getName()).replaceAll("%bodyPart%", b.name());
					table.append(button);
				}
					table.append(tableCellEnd);
			});
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
				button = button.replaceAll("%itemIcon%", b.getStandardIcon()).replace("%itemName%", b.getName()).replaceAll("%bodyPart%", b.name());
				table.append(button);
			}

		});
		table.append(tableCellEnd);
		table.append(tableRowEnd);
		table.append(tableEnd);

		html = html.replace("%shield%", table.toString());

		table.setLength(0);
		table.append(tableHead);
		table.append(tableRow);

		table.append(tableCell);
		SkinHolder s = playerWearingSkins.get(BodyPart.ALLDRESS);
		table.append(subMenuButton.replaceAll("%itemIcon%", s == null ? BodyPart.ALLDRESS.getStandardIcon() : s.getIcon()).replace("%itemName%", BodyPart.ALLDRESS.getName()).replaceAll("%bodyPart%", BodyPart.ALLDRESS.name()));
		table.append(tableCellEnd);
		table.append(tableRowEnd);
		table.append(tableEnd);
		html = html.replace("%costume%", table.toString());

		return html;
	}

	@Override public String[] getBypassList() {
		return COMMANDS.toArray(new String[0]);
	}
}