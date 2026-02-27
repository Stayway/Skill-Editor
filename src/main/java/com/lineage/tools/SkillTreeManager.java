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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkillTreeManager {
    
    @XmlElement(name = "skillTree")
    private List<SkillTreeClass> classTrees = new ArrayList<>();
    
    public SkillTreeManager() {}
    
    public List<SkillTreeClass> getClassTrees() { return classTrees; }
    public void setClassTrees(List<SkillTreeClass> classTrees) { this.classTrees = classTrees; }
    
    public SkillTreeClass getClassTree(int classId) {
        return classTrees.stream()
                .filter(ct -> ct.getClassId() == classId)
                .findFirst()
                .orElse(null);
    }
    
    public void loadFromFile(File file) throws Exception {
        JAXBContext context = JAXBContext.newInstance(SkillTreeManager.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        SkillTreeManager loaded = (SkillTreeManager) unmarshaller.unmarshal(file);
        this.classTrees = loaded.getClassTrees();
    }
    
    public void saveToFile(File file) throws Exception {
        JAXBContext context = JAXBContext.newInstance(SkillTreeManager.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(this, file);
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class SkillTreeClass {
    
    @XmlAttribute(name = "type")
    private String type;
    
    @XmlAttribute(name = "classId")
    private int classId;
    
    @XmlAttribute(name = "parentClassId")
    private int parentClassId;
    
    @XmlElement(name = "skill")
    private List<SkillTreeEntry> skills = new ArrayList<>();
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }
    
    public int getParentClassId() { return parentClassId; }
    public void setParentClassId(int parentClassId) { this.parentClassId = parentClassId; }
    
    public List<SkillTreeEntry> getSkills() { return skills; }
    public void setSkills(List<SkillTreeEntry> skills) { this.skills = skills; }
    
    public void addSkill(SkillTreeEntry skill) {
        skills.add(skill);
    }
    
    public void removeSkill(SkillTreeEntry skill) {
        skills.remove(skill);
    }
    
    public List<SkillTreeEntry> getSkillsByLevel(int minLevel, int maxLevel) {
        return skills.stream()
                .filter(s -> s.getGetLevel() >= minLevel && s.getGetLevel() <= maxLevel)
                .collect(Collectors.toList());
    }
    
    public List<SkillTreeEntry> getSkillsByGetLevel(int getLevel) {
        return skills.stream()
                .filter(s -> s.getGetLevel() == getLevel)
                .collect(Collectors.toList());
    }
}