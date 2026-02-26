/*
Copyright (c) 2026 Stayway

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.lineage.tools;

import javax.xml.bind.annotation.*;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Skill {
    
    @XmlAttribute(name = "id")
    private int skillId;
    
    @XmlAttribute(name = "levels")
    private int levels;
    
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlAttribute(name = "enchantGroup1")
    private String enchantGroup1;
    
    @XmlAttribute(name = "enchantGroup2")
    private String enchantGroup2;
    
    @XmlAttribute(name = "enchantGroup3")
    private String enchantGroup3;
    
    @XmlAttribute(name = "enchantGroup4")
    private String enchantGroup4;
    
    @XmlElement(name = "set")
    private List<SkillSet> sets = new ArrayList<>();
    
    @XmlElement(name = "table")
    private List<SkillTable> tables = new ArrayList<>();
    
    @XmlElement(name = "conditions")
    private SkillConditions conditions;
    
    @XmlElement(name = "effects")
    private SkillEffects effects;
    
    // Construtor padrão
    public Skill() {}
    
    // Getters e Setters
    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }
    
    public int getLevels() { return levels; }
    public void setLevels(int levels) { this.levels = levels; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEnchantGroup1() { return enchantGroup1; }
    public void setEnchantGroup1(String enchantGroup1) { this.enchantGroup1 = enchantGroup1; }
    
    public String getEnchantGroup2() { return enchantGroup2; }
    public void setEnchantGroup2(String enchantGroup2) { this.enchantGroup2 = enchantGroup2; }
    
    public String getEnchantGroup3() { return enchantGroup3; }
    public void setEnchantGroup3(String enchantGroup3) { this.enchantGroup3 = enchantGroup3; }
    
    public String getEnchantGroup4() { return enchantGroup4; }
    public void setEnchantGroup4(String enchantGroup4) { this.enchantGroup4 = enchantGroup4; }
    
    public List<SkillSet> getSets() { return sets; }
    public void setSets(List<SkillSet> sets) { this.sets = sets; }
    
    public List<SkillTable> getTables() { return tables; }
    public void setTables(List<SkillTable> tables) { this.tables = tables; }
    
    public SkillConditions getConditions() { return conditions; }
    public void setConditions(SkillConditions conditions) { this.conditions = conditions; }
    
    public SkillEffects getEffects() { return effects; }
    public void setEffects(SkillEffects effects) { this.effects = effects; }
    
    // Método auxiliar para pegar valor de um set específico
    public String getSetValue(String setName) {
        return sets.stream()
                .filter(s -> s.getName().equals(setName))
                .map(SkillSet::getVal)
                .findFirst()
                .orElse(null);
    }
    
    // Método auxiliar para pegar tabela específica
    public SkillTable getTable(String tableName) {
        return tables.stream()
                .filter(t -> t.getName().equals(tableName))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public String toString() {
        return String.format("%d - %s (%d levels)", skillId, name, levels);
    }
}

// Classe para tags <set>
@XmlAccessorType(XmlAccessType.FIELD)
class SkillSet {
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlAttribute(name = "val")
    private String val;
    
    public String getName() { return name; }
    public String getVal() { return val; }
    public void setName(String name) { this.name = name; }
    public void setVal(String val) { this.val = val; }
    
    @Override
    public String toString() {
        return name + " = " + val;
    }
}

// Classe para tags <table>
@XmlAccessorType(XmlAccessType.FIELD)
class SkillTable {
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlValue
    private String values;
    
    public String getName() { return name; }
    public String getValues() { return values; }
    public void setName(String name) { this.name = name; }
    public void setValues(String values) { this.values = values; }
    
    public List<String> getValuesList() {
        if (values == null || values.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(values.split(" "));
    }
    
    @Override
    public String toString() {
        return name + " = " + values;
    }
}

// Classe para conditions
@XmlAccessorType(XmlAccessType.FIELD)
class SkillConditions {
    @XmlAttribute(name = "msgId")
    private String msgId;
    
    @XmlAttribute(name = "addName")
    private String addName;
    
    @XmlElement(name = "using")
    private SkillUsing using;
    
    @XmlElement(name = "and")
    private SkillAnd and;
    
    @XmlElement(name = "target")
    private SkillTarget target;
    
    @XmlElement(name = "player")
    private SkillPlayer player;
    
    public String getMsgId() { return msgId; }
    public String getAddName() { return addName; }
    public SkillUsing getUsing() { return using; }
    public SkillAnd getAnd() { return and; }
    public SkillTarget getTarget() { return target; }
    public SkillPlayer getPlayer() { return player; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (using != null) sb.append("Using: ").append(using.getKind());
        if (target != null) sb.append(" Target: ").append(target.getRace());
        if (player != null) sb.append(" Player HP: ").append(player.getHp());
        return sb.toString();
    }
}

// Classe para and (condições compostas)
@XmlAccessorType(XmlAccessType.FIELD)
class SkillAnd {
    @XmlElement(name = "using")
    private SkillUsing using;
    
    @XmlElement(name = "target")
    private SkillTarget target;
    
    @XmlElement(name = "player")
    private SkillPlayer player;
    
    public SkillUsing getUsing() { return using; }
    public SkillTarget getTarget() { return target; }
    public SkillPlayer getPlayer() { return player; }
}

// Classe para using
@XmlAccessorType(XmlAccessType.FIELD)
class SkillUsing {
    @XmlAttribute(name = "kind")
    private String kind;
    
    public String getKind() { return kind; }
}

// Classe para target
@XmlAccessorType(XmlAccessType.FIELD)
class SkillTarget {
    @XmlAttribute(name = "race")
    private String race;
    
    @XmlAttribute(name = "mindistance")
    private String mindistance;
    
    @XmlAttribute(name = "abnormal")
    private String abnormal;
    
    public String getRace() { return race; }
    public String getMindistance() { return mindistance; }
    public String getAbnormal() { return abnormal; }
}

// Classe para player
@XmlAccessorType(XmlAccessType.FIELD)
class SkillPlayer {
    @XmlAttribute(name = "hp")
    private String hp;
    
    @XmlAttribute(name = "Charges")
    private String charges;
    
    @XmlAttribute(name = "invSize")
    private String invSize;
    
    @XmlAttribute(name = "weight")
    private String weight;
    
    public String getHp() { return hp; }
    public String getCharges() { return charges; }
    public String getInvSize() { return invSize; }
    public String getWeight() { return weight; }
}

// Classe para effects
@XmlAccessorType(XmlAccessType.FIELD)
class SkillEffects {
    @XmlElement(name = "effect")
    private List<SkillEffect> effects = new ArrayList<>();
    
    public List<SkillEffect> getEffects() { return effects; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (SkillEffect effect : effects) {
            sb.append(effect.toString()).append("\n");
        }
        return sb.toString();
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class SkillEffect {
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlElement(name = "param")
    private List<SkillParam> params;
    
    @XmlElement(name = "mul")
    private List<SkillStat> muls;
    
    @XmlElement(name = "add")
    private List<SkillStat> adds;
    
    @XmlElement(name = "sub")
    private List<SkillStat> subs;
    
    @XmlElement(name = "set")
    private List<SkillStat> sets;
    
    public String getName() { return name; }
    public List<SkillParam> getParams() { return params; }
    public List<SkillStat> getMuls() { return muls; }
    public List<SkillStat> getAdds() { return adds; }
    public List<SkillStat> getSubs() { return subs; }
    public List<SkillStat> getSets() { return sets; }
    
    @Override
    public String toString() {
        return name + (params != null ? " params:" + params.size() : "");
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class SkillParam {
    @XmlAttribute(name = "stat")
    private String stat;
    
    @XmlAttribute(name = "val")
    private String val;
    
    @XmlAttribute(name = "power")
    private String power;
    
    @XmlAttribute(name = "time")
    private String time;
    
    @XmlAttribute(name = "chance")
    private String chance;
    
    public String getStat() { return stat; }
    public String getVal() { return val; }
    public String getPower() { return power; }
    public String getTime() { return time; }
    public String getChance() { return chance; }
}

@XmlAccessorType(XmlAccessType.FIELD)
class SkillStat {
    @XmlAttribute(name = "stat")
    private String stat;
    
    @XmlAttribute(name = "val")
    private String val;
    
    public String getStat() { return stat; }
    public String getVal() { return val; }
}