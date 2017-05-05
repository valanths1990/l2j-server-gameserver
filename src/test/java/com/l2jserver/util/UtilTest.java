package com.l2jserver.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Util test.
 * @author Zoey76
 */
public class UtilTest
{
	private static final String PROVIDE_ARGS = "PROVIDE_ARGS";
	
	private static final String PROVIDE_ARGS_FAIL = "PROVIDE_ARGS_FAIL";
	
	private static final String IGNORE_QUESTS = "-noquest";
	
	private static final String DP = "-dp";
	
	private static final String DP_PATH = "../../../L2J_DataPack/dist/game";
	
	@Test(dataProvider = PROVIDE_ARGS)
	public void testParseArg(String[] args, String arg, boolean hasArgValue, String expected)
	{
		Assert.assertEquals(Util.parseArg(args, arg, hasArgValue), expected);
	}
	
	@Test(dataProvider = PROVIDE_ARGS_FAIL, expectedExceptions = IllegalArgumentException.class)
	public void testParseArgFail(String[] args, String arg, boolean hasArgValue)
	{
		Util.parseArg(args, arg, hasArgValue);
	}
	
	@DataProvider(name = PROVIDE_ARGS)
	private Iterator<Object[]> provideArgs()
	{
		final List<Object[]> result = new LinkedList<>();
		// @formatter:off
		result.add(new Object[] { null, null, false, null });
		result.add(new Object[] { new String[] { }, IGNORE_QUESTS, false, null });
		result.add(new Object[] { new String[] { IGNORE_QUESTS }, null, false, null });
		result.add(new Object[] { new String[] { IGNORE_QUESTS }, "", false, null });
		result.add(new Object[] { new String[] { DP, DP_PATH }, DP, true, DP_PATH });
		result.add(new Object[] { new String[] { IGNORE_QUESTS }, IGNORE_QUESTS, false, IGNORE_QUESTS });
		result.add(new Object[] { new String[] { IGNORE_QUESTS, DP, DP_PATH }, DP, true, DP_PATH });
		// @formatter:on
		return result.iterator();
	}
	
	@DataProvider(name = PROVIDE_ARGS_FAIL)
	private Iterator<Object[]> provideArgsFail()
	{
		// @formatter:off
		return Collections.singletonList(new Object[] { new String[] { IGNORE_QUESTS }, IGNORE_QUESTS, true }).iterator();
		// @formatter:on
	}
}
