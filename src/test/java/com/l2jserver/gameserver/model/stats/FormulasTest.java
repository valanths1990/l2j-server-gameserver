package com.l2jserver.gameserver.model.stats;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Formulas test.
 * @author Zoey76
 */
public class FormulasTest
{
	private static final String PROVIDE_SPEED_SKILL_TIME = "PROVIDE_SPEED_SKILL_TIME";
	
	private static final int HP_REGENERATE_PERIOD_CHARACTER = 3000;
	
	private static final int HP_REGENERATE_PERIOD_DOOR = 300000;
	
	@Mock
	private L2Character character;
	
	@Mock
	private Skill skill;
	
	@BeforeClass
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetRegeneratePeriod()
	{
		Mockito.when(character.isDoor()).thenReturn(true);
		
		Assert.assertEquals(Formulas.getRegeneratePeriod(character), HP_REGENERATE_PERIOD_DOOR);
		
		Mockito.when(character.isDoor()).thenReturn(false);
		
		Assert.assertEquals(Formulas.getRegeneratePeriod(character), HP_REGENERATE_PERIOD_CHARACTER);
	}
	
	@Test(dataProvider = PROVIDE_SPEED_SKILL_TIME)
	public void testCalcAtkSpd(int hitTime, boolean isChanneling, int channelingSkillId, boolean isStatic, boolean isMagic, //
		int mAtkSpeed, double pAtkSpeed, boolean isChargedSpiritshots, boolean isChargedBlessedSpiritShots, double expected)
	{
		Mockito.when(skill.getHitTime()).thenReturn(hitTime);
		Mockito.when(skill.isChanneling()).thenReturn(isChanneling);
		Mockito.when(skill.getChannelingSkillId()).thenReturn(channelingSkillId);
		Mockito.when(skill.isStatic()).thenReturn(isStatic);
		Mockito.when(skill.isMagic()).thenReturn(isMagic);
		Mockito.when(character.getMAtkSpd()).thenReturn(mAtkSpeed);
		Mockito.when(character.getPAtkSpd()).thenReturn(pAtkSpeed);
		Mockito.when(character.isChargedShot(ShotType.SPIRITSHOTS)).thenReturn(isChargedSpiritshots);
		Mockito.when(character.isChargedShot(ShotType.BLESSED_SPIRITSHOTS)).thenReturn(isChargedBlessedSpiritShots);
		
		Assert.assertEquals(Formulas.calcCastTime(character, skill), expected);
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
