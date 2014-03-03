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
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;

/**
 * @author Nos
 */
public enum DropListScope implements IDropItemFactory
{
	DEATH(new IDropItemFactory()
	{
		
		@Override
		public IDropItem newDropItem(int itemId, long min, long max, double chance)
		{
			return new GeneralDropItem(itemId, min, max, chance, Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER, Config.RATE_CORPSE_DROP_CHANCE_MULTIPLIER);
		}
		
	}),
	CORPSE(new IDropItemFactory()
	{
		
		@Override
		public IDropItem newDropItem(int itemId, long min, long max, double chance)
		{
			return new GeneralDropItem(itemId, min, max, chance, Config.RATE_CORPSE_DROP_AMOUNT_MULTIPLIER, Config.RATE_CORPSE_DROP_CHANCE_MULTIPLIER);
		}
		
	}),
	/**
	 * This droplist scope isn't affected by ANY rates, nor Champion, etc...
	 */
	STATIC(new IDropItemFactory()
	{
		
		@Override
		public IDropItem newDropItem(int itemId, long min, long max, double chance)
		{
			return new StaticDropItem(itemId, min, max, chance);
		}
		
	}),
	QUEST(new IDropItemFactory()
	{
		
		@Override
		public IDropItem newDropItem(int itemId, long min, long max, double chance)
		{
			return new StaticDropItem(itemId, min, max, chance)
			{
				@Override
				public boolean isPreciseCalculated()
				{
					return true;
				}
				
				@Override
				protected double getChanceMultiplier(L2Character victim)
				{
					double championmult;
					if ((getItemId() == Inventory.ADENA_ID) || (getItemId() == Inventory.ANCIENT_ADENA_ID))
					{
						championmult = Config.L2JMOD_CHAMPION_ADENAS_REWARDS;
					}
					else
					{
						championmult = Config.L2JMOD_CHAMPION_REWARDS;
					}
					
					return (Config.L2JMOD_CHAMPION_ENABLE && (victim != null) && victim.isChampion()) ? (Config.RATE_QUEST_DROP * championmult) : Config.RATE_QUEST_DROP;
				}
			};
		}
	});
	
	private final IDropItemFactory _factory;
	
	private DropListScope(IDropItemFactory factory)
	{
		_factory = factory;
	}
	
	@Override
	public IDropItem newDropItem(int itemId, long min, long max, double chance)
	{
		return _factory.newDropItem(itemId, min, max, chance);
	}
	
	public GroupedGeneralDropItem newGroupedDropItem(double chance)
	{
		return new GroupedGeneralDropItem(chance);
	}
}
