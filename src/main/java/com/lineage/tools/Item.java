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
public class Item {
    
    @XmlAttribute(name = "id")
    private int itemId;
    
    @XmlAttribute(name = "type")
    private String type;
    
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlElement(name = "set")
    private List<ItemSet> sets = new ArrayList<>();
    
    @XmlElement(name = "stats")
    private ItemStats stats;
    
    @XmlElement(name = "skills")
    private ItemSkills skills;
    
    // Getters e Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<ItemSet> getSets() { return sets; }
    public void setSets(List<ItemSet> sets) { this.sets = sets; }
    
    public ItemStats getStats() { return stats; }
    public void setStats(ItemStats stats) { this.stats = stats; }
    
    public ItemSkills getSkills() { return skills; }
    public void setSkills(ItemSkills skills) { this.skills = skills; }
    
    // Auxiliary method for retrieving value from a specific set.
    public String getSetValue(String setName) {
        return sets.stream()
                .filter(s -> s.getName().equals(setName))
                .map(ItemSet::getVal)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public String toString() {
        return String.format("%d - %s [%s]", itemId, name, type);
    }
}

	// Class for <set> tags
	@XmlAccessorType(XmlAccessType.FIELD)
	class ItemSet {
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlAttribute(name = "val")
    private String val;
    
    public String getName() { return name; }
    public String getVal() { return val; }
    public void setName(String name) { this.name = name; }
    public void setVal(String val) { this.val = val; }
}

	// Class for tags <stats>
	@XmlAccessorType(XmlAccessType.FIELD)
	class ItemStats {
    @XmlElement(name = "stat")
    private List<ItemStat> stats = new ArrayList<>();
    
    public List<ItemStat> getStats() { return stats; }
    public void setStats(List<ItemStat> stats) { this.stats = stats; }
    
    public String getStatValue(String statType) {
        return stats.stream()
                .filter(s -> s.getType().equals(statType))
                .map(ItemStat::getValue)
                .findFirst()
                .orElse(null);
    }
}

	@XmlAccessorType(XmlAccessType.FIELD)
	class ItemStat {
    @XmlAttribute(name = "type")
    private String type;
    
    @XmlValue
    private String value;
    
    public String getType() { return type; }
    public String getValue() { return value; }
    public void setType(String type) { this.type = type; }
    public void setValue(String value) { this.value = value; }
}

	// Class for <skills> tags
	@XmlAccessorType(XmlAccessType.FIELD)
	class ItemSkills {
    @XmlElement(name = "skill")
    private List<ItemSkill> skills = new ArrayList<>();
    
    public List<ItemSkill> getSkills() { return skills; }
    public void setSkills(List<ItemSkill> skills) { this.skills = skills; }
}

	@XmlAccessorType(XmlAccessType.FIELD)
	class ItemSkill {
    @XmlAttribute(name = "id")
    private int id;
    
    @XmlAttribute(name = "level")
    private int level;
    
    public int getId() { return id; }
    public int getLevel() { return level; }
    public void setId(int id) { this.id = id; }
    public void setLevel(int level) { this.level = level; }
}