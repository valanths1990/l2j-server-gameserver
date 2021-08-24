package com.l2jserver.gameserver.custom.skin;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.handler.ItemHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
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

public class SkinManager {

	private final Map<Integer, PlayerWearingSkins> playersWearingSkins = new ConcurrentHashMap<>();
	private final Map<Integer, List<SkinHolder>> playersSkins = new ConcurrentHashMap<>();
	private final Map<Integer, PlayerSkinConfig> playersSkinConfig = new ConcurrentHashMap<>();
	private static final Map<Integer, String> types = new HashMap<>();

	static {
		types.put(0x0200, "gloves");
		types.put(0x0400, "chest");
		types.put(0x0800, "legs");
		types.put(0x1000, "feet");
		types.put(0x8000, "alldress");
		types.put(0x020000, "onepiece");
		types.put(0x0080, "rhand");
		types.put(0x0100, "lhand");
		types.put(0x4000, "lrhand");
	}

	private SkinManager() {
		load();
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::saveAll, 10, 10, TimeUnit.MINUTES);
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_GAME_SHUTDOWN, e -> saveAll(), this));
		ItemHandler.getInstance().registerHandler(new SkinItemHandler());
	}

	public int getWearingSkin(L2PcInstance pc, L2ItemInstance equippedItem) {
		if (equippedItem == null || !types.containsKey(equippedItem.getItem().getBodyPart()) || pc.isInOlympiadMode()) {
			return -1;
		}
		BodyPart bodyPart = null;
		if (equippedItem.getItemType() instanceof ArmorType) {
			ArmorType armorType = (ArmorType) equippedItem.getItemType();
			bodyPart = BodyPart.valueOf(armorType.name().toUpperCase() + types.get(equippedItem.getItem().getBodyPart()).toUpperCase());
		}
		if (equippedItem.getItemType() instanceof WeaponType) {
			WeaponType weaponType = (WeaponType) equippedItem.getItemType();
			bodyPart = BodyPart.valueOf(weaponType.name() + types.get(equippedItem.getItem().getBodyPart()));
		}
		if (bodyPart == null) {
			return -1;
		}
		Optional<SkinHolder> skinHolder = playersWearingSkins.computeIfAbsent(pc.getObjectId(), k -> new PlayerWearingSkins(pc.getObjectId())).getBodyPart(bodyPart);
		return skinHolder.map(SkinHolder::getSkinId).orElse(-1);
	}

	public List<SkinHolder> getPlayersAllSkin(L2PcInstance pc) {
		return null;// playersSkins.getOrDefault(pc, Collections.<SkinHolder>emptyList());
	}

	public Visibility isEnabled(L2PcInstance pc) {
		return playersSkinConfig.computeIfAbsent(pc.getObjectId(), k -> new PlayerSkinConfig(pc.getObjectId(), Visibility.ALL)).getVisibility();
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

	public void addNewSkin(L2PcInstance pc, SkinHolder skin) {
		if (skin == null || pc == null)
			return;
		//		playersSkins.computeIfAbsent(pc.getObjectId(), (key) -> new ArrayList<>()).add(skin);
	}

	private void saveAll() {
		//		try (Connection con = ConnectionFactory.getInstance().getConnection()) {
		//			try (PreparedStatement st = con.prepareStatement("INSERT IGNORE INTO player_skins(char_id,skin_id,skin_part) values ( ? , ? , ?)")) {
		//				playersSkins.forEach((key, value) -> value.forEach(ps -> {
		//					try {
		//						st.setInt(1, ps.getObjectId());
		//						st.setInt(2, ps.getSkinId());
		//						st.setString(3, ps.getSkinPart().name());
		//						st.addBatch();
		//					} catch (SQLException e) {
		//						e.printStackTrace();
		//					}
		//				}));
		//				st.executeBatch();
		//			}
		//			try (
		//				PreparedStatement st = con.prepareStatement("INSERT INTO player_skin(char_id,visibility,heavy_chest,heavy_legs,heavy_feet,heavy_gloves,light_chest" + ",light_legs,light_feet,light_gloves,robe_chest,robe_legs,robe_feet,robe_gloves,sigil,shield,blunt,pole,sword,bow,dagger,crossbow,rapier" + ",ancientsword,dualfist,dualdagger,dual,alldress,onepiece) " + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" + "on duplicate key update heavy_chest = ? and heavy_legs = ? and heavy_feet = ? and heavy_gloves = ? and light_chest = ? and light_legs = ? and light_feet = ? and light_gloves = ? and robe_chest = ? and robe_legs" + " = ? and robe_feet = ? and robe_gloves = ? and sigil = ? and shield = ? and blunt = ?" + " and pole = ? and sword = ? and bow = ? and dagger = ? and crossbow = ? and rapier = ? and ancientsword = ? and dualfist = ? and dualdagger = ? and dual = ? " + "and alldress = ? and onepiece = ? and visibility = ?")) {
		//				st.executeBatch();
		//			}
		//		} catch (SQLException e) {
		//			e.printStackTrace();
		//		}
	}

	public static SkinManager getInstance() {
		return SkinManager.SingletonHolder.instance;
	}

	private static class SingletonHolder {
		protected static final SkinManager instance = new SkinManager();
	}

}