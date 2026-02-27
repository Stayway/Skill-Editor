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

@XmlAccessorType(XmlAccessType.FIELD)
public class SkillTreeEntry {
    
    @XmlAttribute(name = "skillName")
    private String skillName;
    
    @XmlAttribute(name = "skillId")
    private int skillId;
    
    @XmlAttribute(name = "skillLevel")
    private int skillLevel;
    
    @XmlAttribute(name = "getLevel")
    private int getLevel;
    
    @XmlAttribute(name = "levelUpSp")
    private int levelUpSp;
    
    @XmlAttribute(name = "learnedByNpc")
    private boolean learnedByNpc;
    
    // Construtor padrão
    public SkillTreeEntry() {}
    
    // Getters e Setters
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    
    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }
    
    public int getSkillLevel() { return skillLevel; }
    public void setSkillLevel(int skillLevel) { this.skillLevel = skillLevel; }
    
    public int getGetLevel() { return getLevel; }
    public void setGetLevel(int getLevel) { this.getLevel = getLevel; }
    
    public int getLevelUpSp() { return levelUpSp; }
    public void setLevelUpSp(int levelUpSp) { this.levelUpSp = levelUpSp; }
    
    public boolean isLearnedByNpc() { return learnedByNpc; }
    public void setLearnedByNpc(boolean learnedByNpc) { this.learnedByNpc = learnedByNpc; }
    
    @Override
    public String toString() {
        return String.format("%s (ID: %d, Lvl: %d) - Learn at %d, SP: %d", 
            skillName, skillId, skillLevel, getLevel, levelUpSp);
    }
}