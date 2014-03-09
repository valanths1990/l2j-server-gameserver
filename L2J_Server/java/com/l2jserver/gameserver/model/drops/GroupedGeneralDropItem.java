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
import com.l2jserver.gameserver.model.actor.L2Character;
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
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getModifiedChance(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				public double getModifiedChance(L2Character victim, L2Character killer)
				{
					return item.getModifiedChance(victim, killer);
				}
			});
		}
		return Collections.unmodifiableList(items);
	}
	
	/**
	 * statically normalizes a group, useful when need to convert legacy SQL data
	 * @return a new group with items, which have a sum of getChance() of 100%
	 */
	public GroupedGeneralDropItem normalizeMe()
	{
		double sumchance = 0;
		for (GeneralDropItem item : getItems())
		{
			sumchance += (item.getChance() * getChance()) / 100;
		}
		final double sumchance1 = sumchance;
		GroupedGeneralDropItem group = new GroupedGeneralDropItem(sumchance1);
		List<GeneralDropItem> items = new ArrayList<>();
		for (final GeneralDropItem item : getItems())
		{
			items.add(new GeneralDropItem(item.getItemId(), item.getMin(), item.getMax(), (item.getChance() * getChance()) / sumchance1)
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
					return (item.getChance(victim, killer) * GroupedGeneralDropItem.this.getChance()) / sumchance1;
				}
			});
		}
		group.setItems(items);
		return group;
	}
	
	public GroupedGeneralDropItem normalizeMe(L2Character victim, L2Character killer)
	{
		return normalizeMe(victim, killer, false, 1);
	}
	
	/**
	 * Creates a normalized group taking into account all drop modifiers, needed when handling a group which has items with different chance rates
	 * @param victim
	 * @param killer
	 * @param chanceModifier an additional chance modifier
	 * @return a new normalized group with all drop modifiers applied
	 */
	public GroupedGeneralDropItem normalizeMe(L2Character victim, L2Character killer, double chanceModifier)
	{
		return normalizeMe(victim, killer, false, chanceModifier);
	}
	
	/**
	 * Creates a normalized group taking into account all drop modifiers, needed when handling a group which has items with different chance rates
	 * @param victim
	 * @param killer
	 * @param includeDeepBlueChance if to modify chance by {@link GroupedGeneralDropItem#getDeepBlueDropChance(L2Character, L2Character)}
	 * @param chanceModifier an additional chance modifier
	 * @return a new normalized group with all drop modifiers applied
	 */
	public GroupedGeneralDropItem normalizeMe(L2Character victim, L2Character killer, boolean includeDeepBlueChance, double chanceModifier)
	{
		if (includeDeepBlueChance)
		{
			chanceModifier *= (getDeepBlueDropChance(victim, killer) / 100);
		}
		double sumchance = 0;
		for (GeneralDropItem item : getItems())
		{
			sumchance += (item.getChance(victim, killer) * getChance() * chanceModifier) / 100;
		}
		GroupedGeneralDropItem group = new GroupedGeneralDropItem(sumchance)
		{
			/*
			 * (non-Javadoc)
			 * @see com.l2jserver.gameserver.model.drops.GroupedGeneralDropItem#getDeepBlueDropChance(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
			 */
			@Override
			public double getDeepBlueDropChance(L2Character victim, L2Character killer)
			{
				// further normalizing will return same value
				return 100;
			}
		};
		List<GeneralDropItem> items = new ArrayList<>();
		for (GeneralDropItem item : getItems())
		{
			items.add(new GeneralDropItem(item.getItemId(), item.getMin(victim, killer), item.getMax(victim, killer), (item.getChance(victim, killer) * getChance() * chanceModifier) / sumchance)
			{
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getMin(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				public long getMin(L2Character victim, L2Character killer)
				{
					return getMin();
				}
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getMax(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				public long getMax(L2Character victim, L2Character killer)
				{
					return getMax();
				}
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getChance(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				public double getChance(L2Character victim, L2Character killer)
				{
					return getChance();
				}
			});
		}
		group.setItems(items);
		return group;
		
	}
	
	/**
	 * Creates a normalized group taking into account all drop modifiers, needed when handling a group which has items with different chance rates
	 * @param victim
	 * @param killer
	 * @param includeDeepBlueChance if to modify chance by {@link GroupedGeneralDropItem#getDeepBlueDropChance(L2Character, L2Character)}
	 * @return a new normalized group with all drop modifiers applied
	 */
	public GroupedGeneralDropItem normalizeMe(L2Character victim, L2Character killer, boolean includeDeepBlueChance)
	{
		return normalizeMe(victim, killer, includeDeepBlueChance, 1);
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
					return (item.getChance(v, k) * GroupedGeneralDropItem.this.getChance()) / 100;
				}
				
				@Override
				public double getModifiedChance(L2Character victim, L2Character killer)
				{
					return item.getModifiedChance(victim, killer);
				}
			}.calculateDrops(victim, killer);
		}
		
		GroupedGeneralDropItem normalized = normalizeMe(victim, killer, true);
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
	
	/**
	 * @param victim
	 * @param killer
	 * @return
	 */
	public double getDeepBlueDropChance(L2Character victim, L2Character killer)
	{
		int levelDifference = victim.getLevel() - killer.getLevel();
		if ((victim.isRaid()) && Config.DEEPBLUE_DROP_RULES_RAID)
		{
			return Math.max(0, Math.min(1, (levelDifference * 0.15) + 1)) * 100;
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
			// Merged two probability rolls into single roll
			
			return levelGapChanceToDrop;
		}
		return 100;
	}
}
