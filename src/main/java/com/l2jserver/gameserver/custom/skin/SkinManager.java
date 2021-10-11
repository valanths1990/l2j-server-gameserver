package com.l2jserver.gameserver.custom.skin;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.handler.ItemHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLogin;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.ArmorType;
import com.l2jserver.gameserver.model.items.type.WeaponType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SkinManager {

	private final Map<Integer, PlayerWearingSkins> playersWearingSkins = new ConcurrentHashMap<>();
	private final Map<Integer, List<SkinHolder>> playersSkins = new ConcurrentHashMap<>();
	private final Map<Integer, PlayerSkinConfig> playersSkinConfig = new ConcurrentHashMap<>();
	private final Map<BodyPart, List<L2Item>> skins = new HashMap<>();
	private static final Map<Integer, String> types = new HashMap<>();

	static {
		types.put(0x0200, "gloves");
		types.put(0x0400, "chest");
		types.put(0x0800, "legs");
		types.put(0x1000, "feet");
		types.put(0x8000, "chest"); // alldress
		types.put(0x020000, "alldress");
		types.put(0x0080, "rhand");
		types.put(0x0100, "lhand");
		types.put(0x4000, "lrhand");
	}
	private SkinManager() {
		load();
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::saveAll, 10, 10, TimeUnit.SECONDS);
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_GAME_SHUTDOWN, e -> saveAll(), this));
		ItemHandler.getInstance().registerHandler(new SkinItemHandler());

		ItemTable.getInstance().getArmorSkin().forEach((key, value) -> {
			try {
				String armorType = "";
				if ((armorType = ((ArmorType) value.getItemType()).name().toUpperCase()).equals("NONE")) {
					armorType = "";
				}
				BodyPart p = BodyPart.valueOf((armorType + types.get(value.getBodyPart()).toUpperCase()));
				skins.computeIfAbsent(p, k -> new ArrayList<>()).add(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		ItemTable.getInstance().getWeaponSkin().forEach((key, value) -> {
			try {
				BodyPart p = BodyPart.valueOf(((WeaponType) value.getItemType()).name().toUpperCase() + types.get(value.getBodyPart()).toUpperCase());
				skins.computeIfAbsent(p, k -> new ArrayList<>()).add(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LOGIN, this::onLogin, this));

	}

	public void onLogin(IBaseEvent event) {
		OnPlayerLogin onLogin = (OnPlayerLogin) event;
		skins.forEach((k, v) -> {
			v.forEach(i -> {
				SkinHolder s = new SkinHolder(onLogin.getActiveChar().getObjectId(), i.getId(), k, i.getIcon());
				//playersSkins.computeIfAbsent(onLogin.getActiveChar().getObjectId(),kk->new ArrayList<>()).add(s);
			});
		});
	}

	public void addNewSkinForPlayer(L2PcInstance pc, SkinHolder skin) {
		playersSkins.computeIfAbsent(pc.getObjectId(), k -> new ArrayList<>()).add(skin);
	}

	public List<SkinHolder> getPlayersSkinsByBodyPart(L2PcInstance pc, BodyPart bodyPart) {
		return playersSkins.computeIfAbsent(pc.getObjectId(), k -> new ArrayList<>()).stream().filter(s -> s.getSkinPart() == bodyPart).collect(Collectors.toList());
	}

	public List<L2Item> getAllSkins(BodyPart bodypart) {
		if (!skins.containsKey(bodypart))
			return Collections.emptyList();
		List<L2Item> bodySkins = skins.get(bodypart);
		bodySkins.sort(Comparator.comparing(L2Item::getCrystalType));
		return bodySkins;
	}

	public int getWearingSkin(L2PcInstance pc, L2ItemInstance equippedItem) {
		if (equippedItem == null || !types.containsKey(equippedItem.getItem().getBodyPart()) || pc.isInOlympiadMode()) {
			return -1;
		}
		Optional<BodyPart> bodyPart = getBodyPartFromL2Item(equippedItem.getItem());

		if (bodyPart.isEmpty()) {
			return -1;
		}
		Optional<SkinHolder> skinHolder = playersWearingSkins.computeIfAbsent(pc.getObjectId(), k -> new PlayerWearingSkins(pc.getObjectId())).getBodyPart(bodyPart.get());
		return skinHolder.map(SkinHolder::getSkinId).orElse(-1);
	}

	public static Optional<BodyPart> getBodyPartFromL2Item(L2Item item) {
		if (item.getItemType() instanceof ArmorType) {
			ArmorType armorType = (ArmorType) item.getItemType();
			if (armorType == ArmorType.NONE) {
				return Optional.empty();
			}
		return BodyPart.getBodyPart(armorType.name().toUpperCase() + types.get(item.getBodyPart()).toUpperCase());
		}
		if (item.getItemType() instanceof WeaponType) {
			WeaponType weaponType = (WeaponType) item.getItemType();
			if (weaponType == WeaponType.NONE) {
				return Optional.empty();
			}
			return BodyPart.getBodyPart(weaponType.name() + types.get(item.getBodyPart()).toUpperCase());
		}
		return Optional.empty();
	}

	public PlayerWearingSkins getPlayerWearingSkins(L2PcInstance pc) {
		return playersWearingSkins.computeIfAbsent(pc.getObjectId(), k -> new PlayerWearingSkins(pc.getObjectId()));// playersSkins.getOrDefault(pc, Collections.<SkinHolder>emptyList());
	}

	public Visibility isEnabled(L2PcInstance pc) {
		return playersSkinConfig.computeIfAbsent(pc.getObjectId(), k -> new PlayerSkinConfig(pc.getObjectId(), Visibility.ALL)).getVisibility();
	}
	public void setVisibility(L2PcInstance pc, Visibility newVisibility){
		playersSkinConfig.computeIfAbsent(pc.getObjectId(),k->new PlayerSkinConfig(pc.getObjectId(),newVisibility)).setVisibility(newVisibility);
	}
	private void load() {
		int objectId;
		try (Connection con = ConnectionFactory.getInstance().getConnection()) {

			ResultSet rs = con.createStatement().executeQuery("SELECT * FROM player_skins");
			while (rs.next()) {
				objectId = rs.getInt("char_id");
				SkinHolder tmp = new SkinHolder(objectId, rs.getInt("skin_id"), BodyPart.valueOf(rs.getString("skin_part").toUpperCase()), rs.getString("icon"));
				playersSkins.computeIfAbsent(objectId, v -> new ArrayList<>()).add(tmp);
			}

			rs = con.createStatement().executeQuery("SELECT * FROM player_skin_config");

			while (rs.next()) {
				objectId = rs.getInt("char_id");
				PlayerSkinConfig config = new PlayerSkinConfig(objectId, Visibility.valueOf(rs.getString("visibility")));
				playersSkinConfig.put(objectId, config);
			}

			rs = con.createStatement().executeQuery("SELECT * FROM player_wearing_skin");
			while (rs.next()) {
				Map<BodyPart, SkinHolder> playerWearingSkin = new ConcurrentHashMap<>();
				objectId = rs.getInt("char_id");
				int skinId = rs.getInt("skin_id");

				Optional<SkinHolder> wearingSkin = playersSkins.get(objectId).stream().filter(s -> s.getSkinId() == skinId).findFirst();
				wearingSkin.ifPresent(s -> playerWearingSkin.put(s.getSkinPart(), s));
				playersWearingSkins.put(objectId, new PlayerWearingSkins(objectId, playerWearingSkin));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void saveAll() {
		try (Connection con = ConnectionFactory.getInstance().getConnection()) {
			try (PreparedStatement st = con.prepareStatement("INSERT IGNORE INTO player_skins(char_id,skin_id,skin_part,icon) values ( ? , ? , ?, ?)")) {
				playersSkins.forEach((key, value) -> value.forEach(ps -> {
					try {
						st.setInt(1, ps.getObjectId());
						st.setInt(2, ps.getSkinId());
						st.setString(3, ps.getSkinPart().name());
						st.setString(4, ps.getIcon());
						st.addBatch();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}));
				st.executeBatch();
			}
			try (
				PreparedStatement st = con.prepareStatement("INSERT INTO player_wearing_skin(char_id,skin_id,skin_part) VALUES (?,?,?)" + "ON DUPLICATE KEY UPDATE skin_id = ? AND skin_part = ?")) {

				playersWearingSkins.forEach((k, v) -> {

					v.getPlayersWearingSkins().forEach((bodyPart, skin) -> {
						try {
							st.execute("DELETE FROM player_wearing_skin where char_id = " + k);
							st.setInt(1, k);
							st.setInt(2, skin.getSkinId());
							st.setString(3, bodyPart.name());

							st.setInt(4, skin.getSkinId());
							st.setString(5, bodyPart.name());

							st.addBatch();
						} catch (SQLException ex) {
							ex.printStackTrace();
						}
					});

				});

				st.executeBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static SkinManager getInstance() {
		return SkinManager.SingletonHolder.instance;
	}

	private static class SingletonHolder {
		protected static final SkinManager instance = new SkinManager();
	}

}