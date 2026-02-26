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

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkillManager {
    
    @XmlElement(name = "skill")
    private List<Skill> skills = new ArrayList<>();
    
    public SkillManager() {}
    
    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
    
    public void addSkill(Skill skill) {
        skills.add(skill);
    }
    
    public void removeSkill(Skill skill) {
        skills.remove(skill);
    }
    
    public Skill findSkillById(int skillId) {
        return skills.stream()
                .filter(s -> s.getSkillId() == skillId)
                .findFirst()
                .orElse(null);
    }
    
    public List<Skill> searchSkills(String searchTerm) {
        String term = searchTerm.toLowerCase();
        return skills.stream()
                .filter(s -> String.valueOf(s.getSkillId()).contains(term) ||
                            s.getName().toLowerCase().contains(term))
                .collect(Collectors.toList());
    }
    
    public int getNextSkillId() {
        return skills.stream()
                .mapToInt(Skill::getSkillId)
                .max()
                .orElse(0) + 1;
    }
    
    public void loadFromFile(File file) throws Exception {
        JAXBContext context = JAXBContext.newInstance(SkillManager.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        SkillManager loaded = (SkillManager) unmarshaller.unmarshal(file);
        this.skills = loaded.getSkills();
        System.out.println("Loaded " + skills.size() + " skills!");
    }
    
    public void saveToFile(File file) throws Exception {
        JAXBContext context = JAXBContext.newInstance(SkillManager.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(this, file);
    }
}