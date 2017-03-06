/*
 * Copyright (C) 2004-2017 L2J Server
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
package com.l2jserver.gameserver.dao.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.dao.PetDAO;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Pet DAO implementation.
 * @author Zoey76
 */
public class PetDAOMySQLImpl implements PetDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(PetDAOMySQLImpl.class);
	
	private static final String UPDATE_FOOD = "UPDATE pets SET fed=? WHERE item_obj_id = ?";
	
	@Override
	public void updateFood(L2PcInstance player, int petId)
	{
		if ((player.getControlItemId() != 0) && (petId != 0))
		{
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_FOOD))
			{
				ps.setInt(1, player.getCurrentFeed());
				ps.setInt(2, player.getControlItemId());
				ps.executeUpdate();
				player.setControlItemId(0);
			}
			catch (Exception e)
			{
				LOG.error("Failed to store Pet [NpcId: {}] data {}", petId, e);
			}
		}
	}
}
