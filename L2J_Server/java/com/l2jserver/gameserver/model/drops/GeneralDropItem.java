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
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2RaidBossInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.util.Rnd;

/**
 * @author Nos
 */
public class GeneralDropItem implements IDropItem
{
	private final int _itemId;
	private final long _min;
	private final long _max;
	private final double _chance;
	protected final double _defaultAmountMultiplier;
	protected final double _defaultChanceMultiplier;
	
	/**
	 * @param itemId the item id
	 * @param min the min count
	 * @param max the max count
	 * @param chance the chance of this drop item
	 */
	public GeneralDropItem(int itemId, long min, long max, double chance)
	{
		this(itemId, min, max, chance, 1, 1);
	}
	
	public GeneralDropItem(int itemId, long min, long max, double chance, double defaultAmountMultiplier, double defaultChanceMultiplier)
	{
		_itemId = itemId;
		_min = min;
		_max = max;
		_chance = chance;
		_defaultAmountMultiplier = defaultAmountMultiplier;
		_defaultChanceMultiplier = defaultChanceMultiplier;
	}
	
	/**
	 * Gets the item id
	 * @return the item id
	 */
	public int getItemId()
	{
		return _itemId;
	}
	
	/**
	 * Gets the min drop count
	 * @return the min
	 */
	public long getMin()
	{
		return _min;
	}
	
	/**
	 * Gets the min drop count
	 * @param victim the victim
	 * @param killer the killer
	 * @return the min modified by any rates.
	 */
	public long getMin(L2Character victim, L2Character killer)
	{
		return (long) (getMin() * getAmountMultiplier(victim));
	}
	
	/**
	 * Gets the max drop count
	 * @return the max
	 */
	public long getMax()
	{
		return _max;
	}
	
	/**
	 * Gets the max drop count
	 * @param victim the victim
	 * @param killer the killer
	 * @return the max modified by any rates.
	 */
	public long getMax(L2Character victim, L2Character killer)
	{
		return (long) (getMax() * getAmountMultiplier(victim));
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
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.model.drop.IDropItem#calculateDrops(com.l2jserver.gameserver.model.actor.L2Character, com.l2jserver.gameserver.model.actor.L2Character)
	 */
	@Override
	public List<ItemHolder> calculateDrops(L2Character victim, L2Character killer)
	{
		if (((!(victim instanceof L2RaidBossInstance)) && Config.DEEPBLUE_DROP_RULES) || ((victim instanceof L2RaidBossInstance) && Config.DEEPBLUE_DROP_RULES_RAID))
		{
			double levelGapChanceToDrop = getDeepBlueDropChance(victim, killer);
			
			// There is a chance of level gap that it wont drop this item
			if (levelGapChanceToDrop < (Rnd.nextDouble() * 100))
			{
				return null;
			}
		}
		
		if (getModifiedChance(victim, killer) > (Rnd.nextDouble() * 100))
		{
			int amountMultiply = 1;
			if (isPreciseCalculated() && (getModifiedChance(victim, killer) > 100))
			{
				amountMultiply = (int) getModifiedChance(victim, killer) / 100;
				if ((getModifiedChance(victim, killer) % 100) > (Rnd.nextDouble() * 100))
				{
					amountMultiply++;
				}
			}
			
			long amount = Rnd.get(getMin(victim, killer) * amountMultiply, getMax(victim, killer) * amountMultiply);
			
			List<ItemHolder> items = new ArrayList<>(1);
			items.add(new ItemHolder(getItemId(), amount));
			return items;
		}
		
		return null;
	}
	
	/**
	 * @return
	 */
	public boolean isPreciseCalculated()
	{
		return Config.PRECISE_DROP_CALCULATION;
	}
	
	/**
	 * NOTE: isolated from {@link GeneralDropItem#getChance(L2Character, L2Character)} to avoid confusion in drop groups
	 * @param victim
	 * @param killer
	 * @return a chance to drop modified by deep blue drop rules
	 */
	public double getModifiedChance(L2Character victim, L2Character killer)
	{
		return (getDeepBlueDropChance(victim, killer) * getChance(victim, killer)) / 100;
	}
	
	/**
	 * @param victim
	 * @param killer
	 * @return gets a chance modifier for large level differencies
	 */
	protected double getDeepBlueDropChance(L2Character victim, L2Character killer)
	{
		if (((!(victim instanceof L2RaidBossInstance)) && Config.DEEPBLUE_DROP_RULES) || ((victim instanceof L2RaidBossInstance) && Config.DEEPBLUE_DROP_RULES_RAID))
		{
			int levelDifference = victim.getLevel() - killer.getLevel();
			double levelGapChanceToDrop;
			if (getItemId() == Inventory.ADENA_ID)
			{
				
				if (levelDifference >= -8)
				{
					levelGapChanceToDrop = 100;
				}
				else if (levelDifference >= -15)
				{
					levelGapChanceToDrop = levelDifference;
					levelGapChanceToDrop *= 12.857;
					levelGapChanceToDrop += 202.857;
				}
				else
				{
					levelGapChanceToDrop = 10;
				}
			}
			else
			{
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
			}
			return levelGapChanceToDrop;
		}
		return 100;
	}
	
	/**
	 * @param victim
	 * @return
	 */
	protected double getAmountMultiplier(L2Character victim)
	{
		double multiplier = 1;
		if (victim.isChampion())
		{
			multiplier *= getItemId() != Inventory.ADENA_ID ? Config.L2JMOD_CHAMPION_REWARDS : Config.L2JMOD_CHAMPION_ADENAS_REWARDS;
		}
		Float dropChanceMultiplier = Config.RATE_DROP_AMOUNT_MULTIPLIER.get(getItemId());
		if (dropChanceMultiplier != null)
		{
			multiplier *= dropChanceMultiplier;
		}
		else if (ItemTable.getInstance().getTemplate(getItemId()).hasExImmediateEffect())
		{
			multiplier *= Config.RATE_HERB_DROP_AMOUNT_MULTIPLIER;
		}
		else
		{
			multiplier *= _defaultAmountMultiplier;
		}
		return multiplier;
	}
	
	/**
	 * @param victim
	 * @return
	 */
	protected double getChanceMultiplier(L2Character victim)
	{
		float multiplier = 1;
		Float dropChanceMultiplier = Config.RATE_DROP_CHANCE_MULTIPLIER.get(getItemId());
		if (dropChanceMultiplier != null)
		{
			multiplier *= dropChanceMultiplier;
		}
		else if (ItemTable.getInstance().getTemplate(getItemId()).hasExImmediateEffect())
		{
			multiplier *= Config.RATE_HERB_DROP_CHANCE_MULTIPLIER;
		}
		else if (victim instanceof L2RaidBossInstance)
		{
			multiplier *= Config.RATE_RAID_DROP_CHANCE_MULTIPLIER;
		}
		else
		{
			multiplier *= _defaultChanceMultiplier;
		}
		return multiplier;
	}
}
