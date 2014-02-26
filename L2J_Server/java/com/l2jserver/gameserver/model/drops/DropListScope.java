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

import com.l2jserver.Config;

/**
 * @author Nos
 */
public enum DropListScope
{
	DEATH(Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER, Config.RATE_DEATH_DROP_CHANCE_MULTIPLIER),
	CORPSE(Config.RATE_CORPSE_DROP_AMOUNT_MULTIPLIER, Config.RATE_CORPSE_DROP_CHANCE_MULTIPLIER);
	
	private final double _defaultAmountMultiplier;
	private final double _defaultChanceMultiplier;
	
	private DropListScope(double defaultAmountMultiplier, double defaultChanceMultiplier)
	{
		_defaultAmountMultiplier = defaultAmountMultiplier;
		_defaultChanceMultiplier = defaultChanceMultiplier;
	}
	
	public IDropItem newDropItem(int itemId, long min, long max, double chance)
	{
		return new GeneralDropItem(itemId, min, max, chance, this);
	}
	
	public GroupedGeneralDropItem newGroupedDropItem(double chance)
	{
		return new GroupedGeneralDropItem(chance);
	}
	
	public double getDefaultAmountMultiplier()
	{
		return _defaultAmountMultiplier;
	}
	
	public double getDefaultChanceMultiplier()
	{
		return _defaultChanceMultiplier;
	}
}
