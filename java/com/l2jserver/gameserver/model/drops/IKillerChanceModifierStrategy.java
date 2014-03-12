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
 * @author Battlecruiser
 */
public interface IKillerChanceModifierStrategy extends INonGroupedKillerChanceModifierStrategy
{
	public static final IKillerChanceModifierStrategy DEFAULT_STRATEGY = new IKillerChanceModifierStrategy()
	{
		
		@Override
		public double getKillerChanceModifier(IDropItem item, L2Character victim, L2Character killer)
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
		
		@Override
		public double getKillerChanceModifier(GeneralDropItem item, L2Character victim, L2Character killer)
		{
			return getKillerChanceModifier((IDropItem) item, victim, killer);
		}
		
	};
	public static final INonGroupedKillerChanceModifierStrategy DEFAULT_NONGROUP_STRATEGY = new INonGroupedKillerChanceModifierStrategy()
	{
		
		@Override
		public double getKillerChanceModifier(GeneralDropItem item, L2Character victim, L2Character killer)
		{
			if (((!(victim.isRaid())) && Config.DEEPBLUE_DROP_RULES) || ((victim.isRaid()) && Config.DEEPBLUE_DROP_RULES_RAID))
			{
				int levelDifference = victim.getLevel() - killer.getLevel();
				double levelGapChanceToDrop;
				if (item.getItemId() == Inventory.ADENA_ID)
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
				return levelGapChanceToDrop / 100;
			}
			return 1;
		}
	};
	
	IKillerChanceModifierStrategy NO_RULES = new IKillerChanceModifierStrategy()
	{
		
		@Override
		public double getKillerChanceModifier(GeneralDropItem item, L2Character victim, L2Character killer)
		{
			return 1;
		}
		
		@Override
		public double getKillerChanceModifier(IDropItem item, L2Character victim, L2Character killer)
		{
			return 1;
		}
		
	};
	
	public double getKillerChanceModifier(IDropItem item, L2Character victim, L2Character killer);
}
