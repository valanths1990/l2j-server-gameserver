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
import java.util.Collections;
import java.util.List;

import com.l2jserver.Config;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2RaidBossInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.util.Rnd;

/**
 * @author Nos
 */
public class GroupedGeneralDropItem implements IDropItem
{
	private final double _chance;
	private List<GeneralDropItem> _items;
	
	/**
	 * @param chance the chance of this drop item.
	 */
	public GroupedGeneralDropItem(double chance)
	{
		_chance = chance;
	}
	
	/**
	 * Gets the chance of this drop item.
	 * @return the chance
	 */
	public double getChance()
	{
		return _chance;
	}
	
	/**
	 * Gets the chance of this drop item.
	 * @param victim the victim
	 * @param killer the killer
	 * @return the chance modified by any rates.
	 */
	public double getChance(L2Character victim, L2Character killer)
	{
		return getChance() * getChanceMultiplier(victim);
	}
	
	/**
	 * Gets the items.
	 * @return the items
	 */
	public List<GeneralDropItem> getItems()
	{
		return _items;
	}
	
	/**
	 * Sets an item list to this drop item.
	 * @param items the item list
	 */
	public void setItems(List<GeneralDropItem> items)
	{
		_items = Collections.unmodifiableList(items);
	}
	
	/**
	 * Returns a list of items in the group with chance multiplied by chance of the group
	 * @return the list of items with modified chances
	 */
	public List<GeneralDropItem> extractMe()
	{
		List<GeneralDropItem> items = new ArrayList<>();
		for (final GeneralDropItem item : getItems())
		{
			items.add(new GeneralDropItem(item.getItemId(), item.getMin(), item.getMax(), (item.getChance() * getChance()) / 100)
			{
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getMin(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				public long getMin(L2Character victim, L2Character killer)
				{
					return item.getMin(victim, killer);
				}
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getMax(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				public long getMax(L2Character victim, L2Character killer)
				{
					return item.getMax(victim, killer);
				}
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getChance(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				public double getChance(L2Character victim, L2Character killer)
				{
					return (item.getChance(victim, killer) * GroupedGeneralDropItem.this.getChance()) / 100;
				}
			});
		}
		return Collections.unmodifiableList(items);
	}
	
	/**
	 * @return <code>true</code> if this group contains only herbs
	 */
	public boolean isHerbOnly()
	{
		for (GeneralDropItem item : getItems())
		{
			if (!ItemTable.getInstance().getTemplate(item.getItemId()).hasExImmediateEffect())
			{
				return false;
			}
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.model.drop.IDropItem#calculateDrops(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
	 */
	@Override
	public List<ItemHolder> calculateDrops(L2Character victim, L2Character killer)
	{
		if (getItems().size() == 1)
		{
			
			final GeneralDropItem item = getItems().iterator().next();
			return new GeneralDropItem(item.getItemId(), item.getMin(), item.getMax(), (item.getChance() * getChance()) / 100)
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
					return (item.getChance() * GroupedGeneralDropItem.this.getChance(v, k)) / 100;
				}
			}.calculateDrops(victim, killer);
		}
		
		double chanceModifier = 1;
		
		int levelDifference = victim.getLevel() - killer.getLevel();
		if ((victim instanceof L2RaidBossInstance) && Config.DEEPBLUE_DROP_RULES_RAID)
		{
			chanceModifier = Math.max(0, Math.min(1, (levelDifference * 0.15) + 1));
		}
		else if (Config.DEEPBLUE_DROP_RULES)
		{
			
			double levelGapChanceToDrop;
			if (levelDifference >= -5)
			{
				levelGapChanceToDrop = 100;
			}
			else if (levelDifference >= -10)
			{
				levelGapChanceToDrop = levelDifference;
				levelGapChanceToDrop *= 18;
				levelGapChanceToDrop += 190;
			}
			else
			{
				levelGapChanceToDrop = 10;
			}
			
			// There is a chance of level gap that it wont drop this item
			if (levelGapChanceToDrop < (Rnd.nextDouble() * 100))
			{
				return null;
			}
		}
		
		double chance = getChance(victim, killer) * chanceModifier;
		if (chance > (Rnd.nextDouble() * 100))
		{
			double random = (Rnd.nextDouble() * 100);
			double totalChance = 0;
			for (GeneralDropItem item : getItems())
			{
				// Grouped item chance rates should not be modified.
				totalChance += item.getChance();
				if (totalChance > random)
				{
					int amountMultiply = 1;
					double totalItemChance = (chance * item.getChance()) / 100;
					if (Config.PRECISE_DROP_CALCULATION && (totalItemChance >= 100))
					{
						amountMultiply = (int) (totalItemChance) / 100;
						if ((totalItemChance % 100) > (Rnd.nextDouble() * 100))
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
	
	/**
	 * @param victim
	 * @return
	 */
	protected double getChanceMultiplier(L2Character victim)
	{
		if (isHerbOnly())
		{
			return Config.RATE_HERB_DROP_CHANCE_MULTIPLIER;
		}
		if (victim instanceof L2RaidBossInstance)
		{
			return Config.RATE_RAID_DROP_CHANCE_MULTIPLIER;
		}
		return getDefaultChanceMultiplier();
	}
	
	/**
	 * @return
	 */
	protected double getDefaultChanceMultiplier()
	{
		return 1;
	}
}
