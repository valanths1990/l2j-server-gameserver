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

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.util.Rnd;

/**
 * @author Battlecruiser
 */
public interface IGroupedItemDropCalculationStrategy
{
	public static final IGroupedItemDropCalculationStrategy DEFAULT_STRATEGY = new IGroupedItemDropCalculationStrategy()
	{
		
		@Override
		public List<ItemHolder> calculateDrops(final GroupedGeneralDropItem dropItem, L2Character victim, L2Character killer)
		{
			if (dropItem.getItems().size() == 1)
			{
				
				final GeneralDropItem item = dropItem.getItems().iterator().next();
				return new GeneralDropItem(item.getItemId(), item.getMin(), item.getMax(), (item.getChance() * dropItem.getChance()) / 100)
				{
					
					@Override
					public long getMax(L2Character v, L2Character k)
					{
						return item.getMax(v, k);
					}
					
					@Override
					public long getMin(L2Character v, L2Character k)
					{
						return item.getMin(v, k);
					}
					
					@Override
					public double getChance(L2Character v, L2Character k)
					{
						return (item.getChance(v, k) * dropItem.getChance()) / 100;
					}
					
					@Override
					public double getModifiedChance(L2Character victim, L2Character killer)
					{
						return item.getModifiedChance(victim, killer);
					}
				}.calculateDrops(victim, killer);
			}
			
			GroupedGeneralDropItem normalized = dropItem.normalizeMe(victim, killer, true);
			if (normalized.getChance() > (Rnd.nextDouble() * 100))
			{
				double random = (Rnd.nextDouble() * 100);
				double totalChance = 0;
				for (GeneralDropItem item : normalized.getItems())
				{
					// Grouped item chance rates should not be modified.
					totalChance += item.getChance();
					if (totalChance > random)
					{
						int amountMultiply = 1;
						if (Config.PRECISE_DROP_CALCULATION && (normalized.getChance() >= 100))
						{
							amountMultiply = (int) (normalized.getChance()) / 100;
							if ((normalized.getChance() % 100) > (Rnd.nextDouble() * 100))
							{
								amountMultiply++;
							}
						}
						
						long amount = Rnd.get(item.getMin(victim, killer) * amountMultiply, item.getMax(victim, killer) * amountMultiply);
						
						List<ItemHolder> items = new ArrayList<>(1);
						items.add(new ItemHolder(item.getItemId(), amount));
						return items;
					}
				}
			}
			return null;
		}
		
	};
	
	public List<ItemHolder> calculateDrops(GroupedGeneralDropItem item, L2Character victim, L2Character killer);
}
