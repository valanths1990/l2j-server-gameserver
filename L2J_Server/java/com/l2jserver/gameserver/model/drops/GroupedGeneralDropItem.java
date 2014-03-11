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

/**
 * @author Nos
 */
public class GroupedGeneralDropItem implements IDropItem
{
	
	private final double _chance;
	private List<GeneralDropItem> _items;
	protected final IGroupedItemDropCalculationStrategy _strategy;
	
	/**
	 * @param chance the chance of this drop item.
	 */
	public GroupedGeneralDropItem(double chance)
	{
		this(chance, IGroupedItemDropCalculationStrategy.DEFAULT_STRATEGY);
	}
	
	/**
	 * @param chance the chance of this drop item.
	 * @param strategy to calculate drops.
	 */
	public GroupedGeneralDropItem(double chance, IGroupedItemDropCalculationStrategy strategy)
	{
		_chance = chance;
		_strategy = strategy;
	}
	
	/**
	 * Gets the chance of this drop item.
	 * @return the chance
	 */
	public final double getChance()
	{
		return _chance;
	}
	
	/**
	 * Gets the items.
	 * @return the items
	 */
	public final List<GeneralDropItem> getItems()
	{
		return _items;
	}
	
	/**
	 * @return the strategy
	 */
	public final IGroupedItemDropCalculationStrategy getStrategy()
	{
		return _strategy;
	}
	
	/**
	 * Sets an item list to this drop item.
	 * @param items the item list
	 */
	public final void setItems(List<GeneralDropItem> items)
	{
		_items = Collections.unmodifiableList(items);
	}
	
	/**
	 * Returns a list of items in the group with chance multiplied by chance of the group
	 * @return the list of items with modified chances
	 */
	public final List<GeneralDropItem> extractMe()
	{
		List<GeneralDropItem> items = new ArrayList<>();
		for (final GeneralDropItem item : getItems())
		{
			items.add(new GeneralDropItem(item.getItemId(), item.getMin(), item.getMax(), (item.getChance() * getChance()) / 100)
			{
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getAmountMultiplier(com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				protected double getAmountMultiplier(L2Character victim)
				{
					return item.getAmountMultiplier(victim);
				}
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getChanceMultiplier(com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				protected double getChanceMultiplier(L2Character victim)
				{
					return item.getChanceMultiplier(victim);
				}
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getKillerChanceModifier(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				protected double getKillerChanceModifier(L2Character victim, L2Character killer)
				{
					// delegate this to the group
					return getKillerModifier(victim, killer);
				}
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#isPreciseCalculated()
				 */
				@Override
				public boolean isPreciseCalculated()
				{
					// delegate this to the group
					return GroupedGeneralDropItem.this.isPreciseCalculated();
				}
			});
		}
		return items;
	}
	
	/**
	 * statically normalizes a group, useful when need to convert legacy SQL data
	 * @return a new group with items, which have a sum of getChance() of 100%
	 */
	public final GroupedGeneralDropItem normalizeMe()
	{
		double sumchance = 0;
		for (GeneralDropItem item : getItems())
		{
			sumchance += (item.getChance() * getChance()) / 100;
		}
		final double sumchance1 = sumchance;
		GroupedGeneralDropItem group = new GroupedGeneralDropItem(sumchance1, _strategy)
		{
			/*
			 * (non-Javadoc)
			 * @see com.l2jserver.gameserver.model.drops.GroupedGeneralDropItem#isPreciseCalculated()
			 */
			@Override
			public boolean isPreciseCalculated()
			{
				return GroupedGeneralDropItem.this.isPreciseCalculated();
			}
			
			/*
			 * (non-Javadoc)
			 * @see com.l2jserver.gameserver.model.drops.GroupedGeneralDropItem#getDeepBlueDropChance(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
			 */
			@Override
			public double getKillerModifier(L2Character victim, L2Character killer)
			{
				return GroupedGeneralDropItem.this.getKillerModifier(victim, killer);
			}
		};
		List<GeneralDropItem> items = new ArrayList<>();
		for (final GeneralDropItem item : getItems())
		{
			items.add(new GeneralDropItem(item.getItemId(), item.getMin(), item.getMax(), (item.getChance() * getChance()) / sumchance1)
			{
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getAmountMultiplier(com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				protected double getAmountMultiplier(L2Character victim)
				{
					return item.getAmountMultiplier(victim);
				}
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getChanceMultiplier(com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				protected double getChanceMultiplier(L2Character victim)
				{
					return item.getChanceMultiplier(victim);
				}
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getKillerChanceModifier(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				protected double getKillerChanceModifier(L2Character victim, L2Character killer)
				{
					return item.getKillerChanceModifier(victim, killer);
				}
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#isPreciseCalculated()
				 */
				@Override
				public boolean isPreciseCalculated()
				{
					return item.isPreciseCalculated();
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
	 * @return a new normalized group with all drop modifiers applied
	 */
	public final GroupedGeneralDropItem normalizeMe(L2Character victim, L2Character killer)
	{
		return normalizeMe(victim, killer, true, 1);
	}
	
	/**
	 * Creates a normalized group taking into account all drop modifiers, needed when handling a group which has items with different chance rates
	 * @param victim
	 * @param killer
	 * @param chanceModifier an additional chance modifier
	 * @return a new normalized group with all drop modifiers applied
	 */
	public final GroupedGeneralDropItem normalizeMe(L2Character victim, L2Character killer, double chanceModifier)
	{
		return normalizeMe(victim, killer, true, chanceModifier);
	}
	
	/**
	 * Creates a normalized group taking into account all drop modifiers, needed when handling a group which has items with different chance rates
	 * @param victim
	 * @return a new normalized group with all victim modifiers applied
	 */
	public final GroupedGeneralDropItem normalizeMe(L2Character victim)
	{
		return normalizeMe(victim, null, false, 1);
	}
	
	/**
	 * Creates a normalized group taking into account all drop modifiers, needed when handling a group which has items with different chance rates
	 * @param victim
	 * @param chanceModifier an additional chance modifier
	 * @return a new normalized group with all victim modifiers applied
	 */
	public final GroupedGeneralDropItem normalizeMe(L2Character victim, double chanceModifier)
	{
		return normalizeMe(victim, null, false, chanceModifier);
	}
	
	/**
	 * Creates a normalized group taking into account all drop modifiers, needed when handling a group which has items with different chance rates
	 * @param victim
	 * @param killer
	 * @param applyKillerModifier if to modify chance by {@link GroupedGeneralDropItem#getKillerModifier(L2Character, L2Character)}
	 * @param chanceModifier an additional chance modifier
	 * @return a new normalized group with all drop modifiers applied
	 */
	private final GroupedGeneralDropItem normalizeMe(L2Character victim, L2Character killer, boolean applyKillerModifier, double chanceModifier)
	{
		if (applyKillerModifier)
		{
			chanceModifier *= (getKillerModifier(victim, killer));
		}
		double sumchance = 0;
		for (GeneralDropItem item : getItems())
		{
			sumchance += (item.getChance(victim) * getChance() * chanceModifier) / 100;
		}
		GroupedGeneralDropItem group = new GroupedGeneralDropItem(sumchance, _strategy)
		{
			/*
			 * (non-Javadoc)
			 * @see com.l2jserver.gameserver.model.drops.GroupedGeneralDropItem#isPreciseCalculated()
			 */
			@Override
			public boolean isPreciseCalculated()
			{
				return GroupedGeneralDropItem.this.isPreciseCalculated();
			}
			
			/*
			 * (non-Javadoc)
			 * @see com.l2jserver.gameserver.model.drops.GroupedGeneralDropItem#getDeepBlueDropChance(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
			 */
			@Override
			public double getKillerModifier(L2Character victim, L2Character killer)
			{
				return 100;
			}
		}; // to discard further deep blue calculations
		List<GeneralDropItem> items = new ArrayList<>();
		for (GeneralDropItem item : getItems())
		{
			items.add(new GeneralDropItem(item.getItemId(), item.getMin(victim), item.getMax(victim), (item.getChance(victim) * getChance() * chanceModifier) / sumchance)
			{
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getAmountMultiplier(com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				protected double getAmountMultiplier(L2Character victim)
				{
					// All the modifiers are already resolved, the base values don't have any meaning here
					return 1;
				}
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getChanceMultiplier(com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				protected double getChanceMultiplier(L2Character victim)
				{
					// All the modifiers are already resolved, the base values don't have any meaning here
					return 1;
				}
				
				/*
				 * (non-Javadoc)
				 * @see com.l2jserver.gameserver.model.drops.GeneralDropItem#getKillerChanceModifier(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
				 */
				@Override
				protected double getKillerChanceModifier(L2Character victim, L2Character killer)
				{
					// All the modifiers are already resolved, the base values don't have any meaning here
					return 1;
				}
			});
		}
		group.setItems(items);
		return group;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.model.drop.IDropItem#calculateDrops(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
	 */
	@Override
	public final List<ItemHolder> calculateDrops(L2Character victim, L2Character killer)
	{
		return _strategy.calculateDrops(this, victim, killer);
	}
	
	/**
	 * This handles by default deep blue drop rules. It may also be used to handle another drop chance rules based on killer
	 * @param victim the victim who drops the item
	 * @param killer who kills the victim
	 * @return a number between 0 and 1 (usually)
	 */
	public double getKillerModifier(L2Character victim, L2Character killer)
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
			
			return levelGapChanceToDrop / 100;
		}
		return 1;
	}
	
	public boolean isPreciseCalculated()
	{
		return Config.PRECISE_DROP_CALCULATION;
	}
}
