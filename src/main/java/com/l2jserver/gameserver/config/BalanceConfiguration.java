package com.l2jserver.gameserver.config;

import com.l2jserver.gameserver.config.converter.BalanceConverter;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Reloadable;

import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.aeonbits.owner.Config.HotReloadType.ASYNC;
import static org.aeonbits.owner.Config.LoadType.MERGE;

@Config.Sources({
        "file:${L2J_HOME}/custom/game/config/balance.properties",
        "file:./config/balance.properties",
        "classpath:config/balance.properties"
})
@Config.LoadPolicy(MERGE)
@Config.HotReload(value = 1, unit = MINUTES, type = ASYNC)
public interface BalanceConfiguration extends Reloadable {

     Map<Integer,Float> classes = new HashMap<>();

    default Float getStateForClassId(int classId){
        return 0F;
    }


//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_MAGICAL_SKILL_DAMAGE_MULTIPLIERS")
//    Map<Integer, Float> getPvpMagicalSkillDamageMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_MAGICAL_SKILL_DEFENCE_MULTIPLIERS")
//    Map<Integer, Float> getPvpMagicalSkillDefenceMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_MAGICAL_SKILL_CRITICAL_CHANCE_MULTIPLIERS")
//    Map<Integer, Float> getPvpMagicalSkillCriticalChanceMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_MAGICAL_SKILL_CRITICAL_DAMAGE_MULTIPLIERS")
//    Map<Integer, Float> getPvpMagicalSkillCriticalDamageMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_MAGICAL_ATTACK_DAMAGE_MULTIPLIERS")
//    Map<Integer, Float> getPvpMagicalDamageMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_PHYSICAL_SKILL_DAMAGE_MULTIPLIERS")
//    Map<Integer, Float> getPvpPhysicalSkillDamageMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_PHYSICAL_SKILL_DEFENCE_MULTIPLIERS")
//    Map<Integer, Float> getPvpPhysicalSkillDefenceMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_PHYSICAL_SKILL_CRITICAL_CHANCE_MULTIPLIERS")
//    Map<Integer, Float> getPvpPhysicalSkillCriticalChanceMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_PHYSICAL_SKILL_CRITICAL_DAMAGE_MULTIPLIERS")
//    Map<Integer, Float> getPvpPhysicalSkillCriticalDamageMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_PHYSICAL_ATTACK_DAMAGE_MULTIPLIERS")
//    Map<Integer, Float> getPvpPhysicalAttackDamageMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_PHYSICAL_ATTACK_DEFENCE_MULTIPLIERS")
//    Map<Integer, Float> getPvpPhysicalAttackDefenceMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_PHYSICAL_ATTACK_CRITICAL_CHANCE_MULTIPLIERS")
//    Map<Integer, Float> getPvpPhysicalAttackCriticalChanceMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_PHYSICAL_ATTACK_CRITICAL_DAMAGE_MULTIPLIERS")
//    Map<Integer, Float> getPvpPhysicalAttackCriticalDamageMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_BLOW_SKILL_DAMAGE_MULTIPLIERS")
//    Map<Integer, Float> getPvpBlowSkillDamageMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_BLOW_SKILL_DEFENCE_MULTIPLIERS")
//    Map<Integer, Float> getPvpBlowSkillDefenceMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_ENERGY_SKILL_DAMAGE_MULTIPLIERS")
//    Map<Integer, Float> getPvpEnergySkillDamageMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_ENERGY_SKILL_DEFENCE_MULTIPLIERS")
//    Map<Integer, Float> getPvpEnergySkillDefenceMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PLAYER_HEALING_SKILL_MULTIPLIERS")
//    Map<Integer, Float> getPlayerHealingSkillMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("SKILL_MASTERY_CHANCE_MULTIPLIERS")
//    Map<Integer, Float> getSkillMasteryChanceMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_BLOW_DAMAGE_MULTIPLIERS")
//    Map<Integer, Float> getPvpBlowDamageMultipliers();
//
//    @ConverterClass(BalanceConverter.class)
//    @Key("PVP_BACKSTAB_DAMAGE_MULTIPLIERS")
//    Map<Integer, Float> getPvpBackStabDamageMultipliers();
}
