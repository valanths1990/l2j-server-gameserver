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
package com.l2jserver.gameserver.model.drops.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.drops.GeneralDropItem;
import com.l2jserver.gameserver.model.drops.GroupedGeneralDropItem;
import com.l2jserver.gameserver.model.drops.IDropItem;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.util.Rnd;

/**
 * @author Battlecruiser
 */
public interface IGroupedItemDropCalculationStrategy
{
	/**
	 * The default strategy used in L2J to calculate drops.
	 */
	public static final IGroupedItemDropCalculationStrategy DEFAULT_STRATEGY = (dropItem, victim, killer) ->
	{
		if (dropItem.getItems().size() == 1)
		{
			
			final GeneralDropItem item1 = dropItem.getItems().iterator().next();
			return new GeneralDropItem(item1.getItemId(), item1.getMin(), item1.getMax(), (item1.getChance() * dropItem.getChance()) / 100, item1.getAmountStrategy(), item1.getChanceStrategy(), dropItem.getPreciseStrategy(), dropItem.getKillerChanceModifierStrategy(), item1.getDropCalculationStrategy()).calculateDrops(victim, killer);
		}
		
		GroupedGeneralDropItem normalized = dropItem.normalizeMe(victim, killer);
		if (normalized.getChance() > (Rnd.nextDouble() * 100))
		{
			double random = (Rnd.nextDouble() * 100);
			double totalChance = 0;
			for (GeneralDropItem item2 : normalized.getItems())
			{
				// Grouped item chance rates should not be modified.
				totalChance += item2.getChance();
				if (totalChance > random)
				{
					int amountMultiply = 1;
					if (dropItem.isPreciseCalculated() && (normalized.getChance() >= 100))
					{
						amountMultiply = (int) (normalized.getChance()) / 100;
						if ((normalized.getChance() % 100) > (Rnd.nextDouble() * 100))
						{
							amountMultiply++;
						}
					}
					
					long amount = Rnd.get(item2.getMin(victim) * amountMultiply, item2.getMax(victim) * amountMultiply);
					
					return Collections.singletonList(new ItemHolder(item2.getItemId(), amount));
				}
			}
		}
		return null;
	};
	
	/**
	 * This strategy calculates a group's drop by calculating drops of its individual items and merging its results.
	 */
	public static final IGroupedItemDropCalculationStrategy DISBAND_GROUP = (item, victim, killer) ->
	{
		List<ItemHolder> dropped = new ArrayList<>();
		for (IDropItem dropItem : item.extractMe())
		{
			dropped.addAll(dropItem.calculateDrops(victim, killer));
		}
		return dropped.isEmpty() ? null : dropped;
	};
	
	public List<ItemHolder> calculateDrops(GroupedGeneralDropItem item, L2Character victim, L2Character killer);
}
