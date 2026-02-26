package com.lineage.tools;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "skill")
public class Skill {
    
    @XmlElement(name = "skill_id")
    private int skillId;
    
    @XmlElement
    private int level;
    
    @XmlElement
    private String name;
    
    @XmlElement(name = "operate_type")
    private String operateType;
    
    @XmlElement(name = "magic_level")
    private int magicLevel;
    
    @XmlElement(name = "mp_consume")
    private int mpConsume;
    
    @XmlElement(name = "hp_consume")
    private int hpConsume;
    
    @XmlElement(name = "item_consume")
    private int itemConsume;
    
    @XmlElement(name = "cast_range")
    private int castRange;
    
    @XmlElement(name = "effect_range")
    private int effectRange;
    
    @XmlElement(name = "skill_time")
    private int skillTime;
    
    @XmlElement(name = "reuse_delay")
    private int reuseDelay;
    
    @XmlElement
    private int attribute;
    
    @XmlElement
    private String target;
    
    @XmlElement(name = "skill_type")
    private String skillType;
    
    @XmlElement(name = "magic_critical")
    private boolean magicCritical;

    // Construtores
    public Skill() {}

    public Skill(int skillId, int level, String name) {
        this.skillId = skillId;
        this.level = level;
        this.name = name;
        // Valores padrão
        this.operateType = "OP_ACTIVE";
        this.magicLevel = 1;
        this.mpConsume = 10;
        this.hpConsume = 0;
        this.itemConsume = 0;
        this.castRange = 400;
        this.effectRange = 900;
        this.skillTime = 10;
        this.reuseDelay = 5000;
        this.attribute = 0;
        this.target = "target_one";
        this.skillType = "BUFF";
        this.magicCritical = false;
    }

    // Getters e Setters
    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getOperateType() { return operateType; }
    public void setOperateType(String operateType) { this.operateType = operateType; }
    
    public int getMagicLevel() { return magicLevel; }
    public void setMagicLevel(int magicLevel) { this.magicLevel = magicLevel; }
    
    public int getMpConsume() { return mpConsume; }
    public void setMpConsume(int mpConsume) { this.mpConsume = mpConsume; }
    
    public int getHpConsume() { return hpConsume; }
    public void setHpConsume(int hpConsume) { this.hpConsume = hpConsume; }
    
    public int getItemConsume() { return itemConsume; }
    public void setItemConsume(int itemConsume) { this.itemConsume = itemConsume; }
    
    public int getCastRange() { return castRange; }
    public void setCastRange(int castRange) { this.castRange = castRange; }
    
    public int getEffectRange() { return effectRange; }
    public void setEffectRange(int effectRange) { this.effectRange = effectRange; }
    
    public int getSkillTime() { return skillTime; }
    public void setSkillTime(int skillTime) { this.skillTime = skillTime; }
    
    public int getReuseDelay() { return reuseDelay; }
    public void setReuseDelay(int reuseDelay) { this.reuseDelay = reuseDelay; }
    
    public int getAttribute() { return attribute; }
    public void setAttribute(int attribute) { this.attribute = attribute; }
    
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    
    public String getSkillType() { return skillType; }
    public void setSkillType(String skillType) { this.skillType = skillType; }
    
    public boolean isMagicCritical() { return magicCritical; }
    public void setMagicCritical(boolean magicCritical) { this.magicCritical = magicCritical; }

    @Override
    public String toString() {
        return String.format("%d [Lv.%d] - %s", skillId, level, name);
    }
}