package com.l2jserver.gameserver.config.converter;

import com.l2jserver.gameserver.model.base.ClassId;
import org.aeonbits.owner.Converter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class BalanceConverter implements Converter<Map<Integer, Float>> {
    @Override
    public Map<Integer, Float> convert(Method method, String input) {

        Map<Integer, Float> balanceMap = Arrays.stream(ClassId.values()).collect(Collectors.toMap(ClassId::getId, i -> 1.0f));
        if (!input.isEmpty()) {
            String[] split = input.split(";");
            Arrays.stream(split).forEach(i -> {
                String[] splitDash = i.split("-");
                ClassId classId = null;
                if (!splitDash[0].matches("[0-9]+")) {
                    classId = ClassId.valueOf(splitDash[0]);
                }
                balanceMap.put(classId == null ? Integer.parseInt(splitDash[0]) : classId.getId(), Float.parseFloat(splitDash[1]));
            });
        }
        return balanceMap;
    }
}
