package com.l2jserver.gameserver.config.converter;import java.math.BigInteger;

import org.aeonbits.owner.Converter;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Hex Id Converter test.
 * @author Zoey76
 * @version 2.6.1.0
 */
public class HexIdConverterTest {
	
	private static final String PROVIDE_HEX_ID = "PROVIDE_HEX_ID";
	
	private static final Converter<BigInteger> CONVERTER = new HexIdConverter();
	
	@Test(dataProvider = PROVIDE_HEX_ID)
	public void convertTest(String hexId, BigInteger expected) {
		Assert.assertEquals(CONVERTER.convert(null, hexId), expected);
	}
	
	@DataProvider(name = PROVIDE_HEX_ID)
	public Object[][] provideKeyValues() {
		return new Object[][] {
			{
				"-1eeb34fce0c64b610338d1269d8cfea4",
				new BigInteger("-1eeb34fce0c64b610338d1269d8cfea4", 16)
			}
		};
	}
}
