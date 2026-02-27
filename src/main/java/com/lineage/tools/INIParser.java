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
                
                // Ignore empty lines
                if (line.isEmpty()) continue;
                
                // Section [name]
                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1);
                    sections.putIfAbsent(currentSection, new HashMap<>());
                }
                // Comment
                else if (line.startsWith(";") || line.startsWith("#")) {
                    continue;
                }
                // Key=Value
                else if (line.contains("=") && currentSection != null) {
                    int equalsIndex = line.indexOf('=');
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();
                    
                    // Remove quotation marks if present.
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