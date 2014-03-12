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
 * @author Battlecruiser
 */
public interface IPreciseDeterminationStrategy
{
	public static final IPreciseDeterminationStrategy ALWAYS = new IPreciseDeterminationStrategy()
	{
		
		@Override
		public boolean isPreciseCalculated(IDropItem dropItem)
		{
			return true;
		}
	};
	
	public static final IPreciseDeterminationStrategy DEFAULT = new IPreciseDeterminationStrategy()
	{
		
		@Override
		public boolean isPreciseCalculated(IDropItem dropItem)
		{
			return Config.PRECISE_DROP_CALCULATION;
		}
	};
	
	public static final IPreciseDeterminationStrategy NEVER = new IPreciseDeterminationStrategy()
	{
		
		@Override
		public boolean isPreciseCalculated(IDropItem dropItem)
		{
			return false;
		}
	};
	
	/**
	 * @param dropItem
	 * @return <code>true</code> if drop calculation strategy should use precise rules
	 */
	public boolean isPreciseCalculated(IDropItem dropItem);
}
