package com.l2jserver.gameserver.model.stats;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;

import mockit.Mock;
import mockit.MockUp;

/**
 * Formulas test.
 * @author Zoey76
 */
public class FormulasTest
{
	private static final String PROVIDE_SPEED_SKILL_TIME = "PROVIDE_SPEED_SKILL_TIME";
	
	private static final String PROVIDE_CHARACTERS = "PROVIDE_CHARACTERS";
	
	private static final int HP_REGENERATE_PERIOD_CHARACTER = 3000;
	
	private static final int HP_REGENERATE_PERIOD_DOOR = 300000;
	
	@Test(dataProvider = PROVIDE_CHARACTERS)
	public void testGetRegeneratePeriod(L2Character character, int expected)
	{
		Assert.assertEquals(Formulas.getRegeneratePeriod(character), expected);
	}
	
	@Test(dataProvider = PROVIDE_SPEED_SKILL_TIME)
	public void testCalcAtkSpd(int hitTime, boolean isChanneling, int channelingSkillId, boolean isStatic, boolean isMagic, //
		int mAtkSpeed, double pAtkSpeed, boolean isChargedSpiritshots, boolean isChargedBlessedSpiritShots, double expected)
	{
		final L2Character character = new MockUp<L2Character>()
		{
			@Mock
			int getMAtkSpd()
			{
				return mAtkSpeed;
			}
			
			@Mock
			double getPAtkSpd()
			{
				return pAtkSpeed;
			}
			
			@Mock
			boolean isChargedShot(ShotType type)
			{
				switch (type)
				{
					case SPIRITSHOTS:
					{
						return isChargedSpiritshots;
					}
					case BLESSED_SPIRITSHOTS:
					{
						return isChargedBlessedSpiritShots;
					}
				}
				return false;
			}
		}.getMockInstance();
		
		final Skill skill = new MockUp<Skill>()
		{
			@Mock
			int getHitTime()
			{
				return hitTime;
			}
			
			@Mock
			boolean isChanneling()
			{
				return isChanneling;
			}
			
			@Mock
			int getChannelingSkillId()
			{
				return channelingSkillId;
			}
			
			@Mock
			boolean isStatic()
			{
				return isStatic;
			}
			
			@Mock
			boolean isMagic()
			{
				return isMagic;
			}
		}.getMockInstance();
		
		Assert.assertEquals(Formulas.calcCastTime(character, skill), expected);
	}
	
	@DataProvider(name = PROVIDE_CHARACTERS)
	private Iterator<Object[]> provideCharacters()
	{
		final Set<Object[]> result = new HashSet<>();
		final L2Character c1 = new MockUp<L2Character>()
		{
			@Mock
			boolean isDoor()
			{
				return true;
			}
		}.getMockInstance();
		final L2Character c2 = new MockUp<L2Character>()
		{
			@Mock
			boolean isDoor()
			{
				return false;
			}
		}.getMockInstance();
		// @formatter:off
		result.add(new Object[]{ c1, HP_REGENERATE_PERIOD_DOOR });
		result.add(new Object[]{ c2, HP_REGENERATE_PERIOD_CHARACTER });
		// @formatter:on
		return result.iterator();
	}
	
	@DataProvider(name = PROVIDE_SPEED_SKILL_TIME)
	private Iterator<Object[]> provide()
	{
		final Set<Object[]> result = new HashSet<>();
		// @formatter:off
		// TODO(Zoey76): Take care of the "bad" values.
		result.add(new Object[]{ 0, true, 1, false, false, 0, 0.0, false, false, 0.0 });
		result.add(new Object[]{ 0, true, 0, false, false, 0, 0.0, false, false, Double.NaN });
		result.add(new Object[]{ 0, false, 1, false, false, 0, 0.0, false, false, Double.NaN });
		result.add(new Object[]{ 0, false, 0, false, true, 500, 0.0, false, false, 0.0 });
		result.add(new Object[]{ 600, false, 0, false, true, 500, 0.0, false, false, 500.0 });
		result.add(new Object[]{ 3000, false, 0, false, true, 600, 0.0, false, false, 1665.0 });
		result.add(new Object[]{ 0, false, 0, false, false, 0, 500.0, false, false, 0.0 });
		result.add(new Object[]{ 600, false, 0, false, false, 0, 500.0, false, false, 500.0 });
		result.add(new Object[]{ 3000, false, 0, false, false, 0, 600.0, false, false, 1665.0 });
		result.add(new Object[]{ 1400, false, 0, false, true, 0, 0.0, true, false, 2.147483647E9 });
		result.add(new Object[]{ 1400, false, 0, false, true, 0, 0.0, false, true, 2.147483647E9 });	
		result.add(new Object[]{ 1400, false, 0, true, true, 0, 0.0, true, false, 1000.0 });
		result.add(new Object[]{ 1400, false, 0, true, true, 0, 0.0, false, true, 1000.0 });
		// @formatter:on
		return result.iterator();
	}
}
