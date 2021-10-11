package com.l2jserver.gameserver.config.converter;

import org.aeonbits.owner.Converter;

import java.lang.reflect.Method;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WeekHourConverter implements Converter<Map<DayOfWeek, Integer>> {
	@Override public Map<DayOfWeek, Integer> convert(Method method, String input) {
		Map<DayOfWeek, Integer> spawnTimes = new HashMap<>();
		String[] splitted = input.split(";");
		Arrays.stream(splitted).forEach(s -> {
			String[] splitString = s.split("-");
			spawnTimes.put(DayOfWeek.valueOf(splitString[0].toUpperCase()), Integer.parseInt(splitString[1]));
		});
		return spawnTimes;
	}
}
