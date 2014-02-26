/*
 * Copyright (C) 2004-2014 L2J Server
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
package com.l2jserver.gameserver.model.drops;

import com.l2jserver.gameserver.model.actor.L2Character;

/**
 * @author Battlecruiser
 */
public class StaticDropItem extends GeneralDropItem
{
	/**
	 * @param itemId
	 * @param min
	 * @param max
	 * @param chance
	 */
	public StaticDropItem(int itemId, long min, long max, double chance)
	{
		super(itemId, min, max, chance);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getChanceMultiplier(com.l2jserver.gameserver.model.actor.L2Character)
	 */
	@Override
	protected double getChanceMultiplier(L2Character victim)
	{
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getAmountMultiplier(com.l2jserver.gameserver.model.actor.L2Character)
	 */
	@Override
	protected double getAmountMultiplier(L2Character victim)
	{
		return 1;
	}
	
	@Override
	protected double getDeepBlueDropChance(L2Character victim, L2Character killer)
	{
		return 100;
	}
}