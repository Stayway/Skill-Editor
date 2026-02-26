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