/*
 * Copyright (C) 2004-2016 L2J Server
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
package com.l2jserver.gameserver.network.clientpackets;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.Config;
import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.data.xml.impl.InitialEquipmentData;
import com.l2jserver.gameserver.data.xml.impl.InitialShortcutData;
import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.L2SkillLearn;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.appearance.PcAppearance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.stat.PcStat;
import com.l2jserver.gameserver.model.actor.templates.L2PcTemplate;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerCreate;
import com.l2jserver.gameserver.model.items.PcItemTemplate;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.gameserver.network.serverpackets.CharCreateFail;
import com.l2jserver.gameserver.network.serverpackets.CharCreateOk;
import com.l2jserver.gameserver.network.serverpackets.CharSelectionInfo;

@SuppressWarnings("unused")
public final class CharacterCreate extends L2GameClientPacket
{
	private static final String _C__0C_CHARACTERCREATE = "[C] 0C CharacterCreate";
	private static final int PLAYER_NAME_MAX_LENGHT = 16;
	
	private static final Logger LOG = LoggerFactory.getLogger("accounting");
	
	// cSdddddddddddd
	private String _name;
	private int _race;
	private byte _sex;
	private int _classId;
	private int _int;
	private int _str;
	private int _con;
	private int _men;
	private int _dex;
	private int _wit;
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
		_race = readD();
		_sex = (byte) readD();
		_classId = readD();
		_int = readD();
		_str = readD();
		_con = readD();
		_men = readD();
		_dex = readD();
		_wit = readD();
		_hairStyle = (byte) readD();
		_hairColor = (byte) readD();
		_face = (byte) readD();
	}
	
	@Override
	protected void runImpl()
	{
		if ((_name.length() < 1) || (_name.length() > 16))
		{
			if (Config.DEBUG)
			{
				_log.fine("Character Creation Failure: Character name " + _name + " is invalid.");
			}
			
			sendPacket(new CharCreateFail(CharCreateFail.REASON_16_ENG_CHARS));
			return;
		}
		
		if (Config.FORBIDDEN_NAMES.contains(_name.toLowerCase()))
		{
			sendPacket(new CharCreateFail(CharCreateFail.REASON_INCORRECT_NAME));
			return;
		}
		
		if (!isValidName(_name))
		{
			sendPacket(new CharCreateFail(CharCreateFail.REASON_INCORRECT_NAME));
			return;
		}
		
		if (_name.isEmpty() || (_name.length() > PLAYER_NAME_MAX_LENGHT))
		{
			sendPacket(new CharCreateFail(CharCreateFail.REASON_16_ENG_CHARS));
			return;
		}
		
		if ((_face > 2) || (_face < 0))
		{
			_log.warning("Character Creation Failure: Character face " + _face + " is invalid. Possible client hack. " + getClient());
			
			sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		if ((_hairStyle < 0) || ((_sex == 0) && (_hairStyle > 4)) || ((_sex != 0) && (_hairStyle > 6)))
		{
			_log.warning("Character Creation Failure: Character hair style " + _hairStyle + " is invalid. Possible client hack. " + getClient());
			
			sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		if ((_hairColor > 3) || (_hairColor < 0))
		{
			_log.warning("Character Creation Failure: Character hair color " + _hairColor + " is invalid. Possible client hack. " + getClient());
			
			sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		L2PcInstance newChar = null;
		
		/*
		 * DrHouse: Since checks for duplicate names are done using SQL, lock must be held until data is written to DB as well.
		 */
		synchronized (CharNameTable.getInstance())
		{
			if ((CharNameTable.getInstance().getAccountCharacterCount(getClient().getAccountName()) >= Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT) && (Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0))
			{
				if (Config.DEBUG)
				{
					_log.fine("Max number of characters reached. Creation failed.");
				}
				
				sendPacket(new CharCreateFail(CharCreateFail.REASON_TOO_MANY_CHARACTERS));
				return;
			}
			else if (CharNameTable.getInstance().doesCharNameExist(_name))
			{
				if (Config.DEBUG)
				{
					_log.fine("Character Creation Failure: Message generated: You cannot create another character. Please delete the existing character and try again.");
				}
				
				sendPacket(new CharCreateFail(CharCreateFail.REASON_NAME_ALREADY_EXISTS));
				return;
			}
			
			if (ClassId.getClassId(_classId).level() > 0)
			{
				_log.warning("Character Creation Failure: " + _name + " classId: " + _classId);
				
				sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
				return;
			}
			final PcAppearance app = new PcAppearance(_face, _hairColor, _hairStyle, _sex != 0);
			newChar = L2PcInstance.create(_classId, getClient().getAccountName(), _name, app);
		}
		
		// HP and MP are at maximum and CP is zero by default.
		newChar.setCurrentHp(newChar.getMaxHp());
		newChar.setCurrentMp(newChar.getMaxMp());
		// newChar.setMaxLoad(template.getBaseLoad());
		
		sendPacket(new CharCreateOk());
		
		initNewChar(getClient(), newChar);
		
		LOG.info("Created new character {} {}.", newChar, getClient());
	}
	
	private boolean isValidName(String text)
	{
		return Config.PLAYER_NAME_TEMPLATE.matcher(text).matches();
	}
	
	private void initNewChar(L2GameClient client, L2PcInstance newChar)
	{
		if (Config.DEBUG)
		{
			_log.fine("Character init start");
		}
		
		L2World.getInstance().storeObject(newChar);
		
		if (Config.STARTING_ADENA > 0)
		{
			newChar.addAdena("Init", Config.STARTING_ADENA, null, false);
		}
		
		final L2PcTemplate template = newChar.getTemplate();
		Location createLoc = template.getCreationPoint();
		newChar.setXYZInvisible(createLoc.getX(), createLoc.getY(), createLoc.getZ());
		newChar.setTitle("");
		
		if (Config.ENABLE_VITALITY)
		{
			newChar.setVitalityPoints(Math.min(Config.STARTING_VITALITY_POINTS, PcStat.MAX_VITALITY_POINTS), true);
		}
		if (Config.STARTING_LEVEL > 1)
		{
			newChar.getStat().addLevel((byte) (Config.STARTING_LEVEL - 1));
		}
		if (Config.STARTING_SP > 0)
		{
			newChar.getStat().addSp(Config.STARTING_SP);
		}
		
		final List<PcItemTemplate> initialItems = InitialEquipmentData.getInstance().getEquipmentList(newChar.getClassId());
		if (initialItems != null)
		{
			for (PcItemTemplate ie : initialItems)
			{
				final L2ItemInstance item = newChar.getInventory().addItem("Init", ie.getId(), ie.getCount(), newChar, null);
				if (item == null)
				{
					_log.warning("Could not create item during char creation: itemId " + ie.getId() + ", amount " + ie.getCount() + ".");
					continue;
				}
				
				if (item.isEquipable() && ie.isEquipped())
				{
					newChar.getInventory().equipItem(item);
				}
			}
		}
		
		for (L2SkillLearn skill : SkillTreesData.getInstance().getAvailableSkills(newChar, newChar.getClassId(), false, true))
		{
			if (Config.DEBUG)
			{
				_log.fine("Adding starter skill:" + skill.getSkillId() + " / " + skill.getSkillLevel());
			}
			
			newChar.addSkill(SkillData.getInstance().getSkill(skill.getSkillId(), skill.getSkillLevel()), true);
		}
		
		// Register all shortcuts for actions, skills and items for this new character.
		InitialShortcutData.getInstance().registerAllShortcuts(newChar);
		
		EventDispatcher.getInstance().notifyEvent(new OnPlayerCreate(newChar, newChar.getObjectId(), newChar.getName(), client), Containers.Players());
		
		newChar.setOnlineStatus(true, false);
		newChar.deleteMe();
		
		final CharSelectionInfo cl = new CharSelectionInfo(client.getAccountName(), client.getSessionId().playOkID1);
		client.setCharSelection(cl.getCharInfo());
		
		if (Config.DEBUG)
		{
			_log.fine("Character init end");
		}
	}
	
	@Override
	public String getType()
	{
		return _C__0C_CHARACTERCREATE;
	}
}
