package com.lineage.tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class INIParser {
    private Map<String, Map<String, String>> sections;
    private String currentSection;
    
    public INIParser() {
        sections = new HashMap<>();
    }
    
    public void load(InputStream inputStream) throws IOException {
        sections.clear();
        currentSection = null;
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Ignorar linhas vazias
                if (line.isEmpty()) continue;
                
                // Seção [nome]
                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1);
                    sections.putIfAbsent(currentSection, new HashMap<>());
                }
                // Comentário
                else if (line.startsWith(";") || line.startsWith("#")) {
                    continue;
                }
                // Chave=Valor
                else if (line.contains("=") && currentSection != null) {
                    int equalsIndex = line.indexOf('=');
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();
                    
                    // Remover aspas se houver
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    
                    sections.get(currentSection).put(key, value);
                }
            }
        }
    }
    
    public String getString(String section, String key) {
        Map<String, String> sectionMap = sections.get(section);
        if (sectionMap != null) {
            return sectionMap.get(key);
        }
        return null;
    }
    
    public String getString(String section, String key, String defaultValue) {
        String value = getString(section, key);
        return value != null ? value : defaultValue;
    }
    
    public Map<String, String> getSection(String section) {
        return sections.get(section);
    }
    
    public Set<String> getSections() {
        return sections.keySet();
    }
}