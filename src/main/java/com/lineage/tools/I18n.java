package com.lineage.tools;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Locale;

public class I18n {
    private static I18n instance;
    private INIParser currentIni;
    private Locale currentLocale;
    
    // Available themes
    public enum Theme {
        DARK, LIGHT, SYSTEM
    }
    
    // Available languages
    public enum Language {
        ENGLISH(Locale.ENGLISH, "en"),
        @SuppressWarnings("deprecation")
		PORTUGUESE(new Locale("pt", "BR"), "pt"),
        @SuppressWarnings("deprecation")
		SPANISH(new Locale("es"), "es"),
        FRENCH(Locale.FRENCH, "fr"),
        GERMAN(Locale.GERMAN, "de"),
        JAPANESE(Locale.JAPANESE, "ja"),
        CHINESE(Locale.CHINESE, "zh");
        
        private final Locale locale;
        private final String code;
        
        Language(Locale locale, String code) {
            this.locale = locale;
            this.code = code;
        }
        
        public Locale getLocale() { return locale; }
        public String getCode() { return code; }
    }
    
    private I18n() {
        // Default to English
        setLanguage(Language.ENGLISH);
    }
    
    public static I18n getInstance() {
        if (instance == null) {
            instance = new I18n();
        }
        return instance;
    }
    
    public void setLanguage(Language language) {
        this.currentLocale = language.getLocale();
        loadIniFile(language.getCode());
    }
    
    private void loadIniFile(String languageCode) {
        String fileName = "/Messages_" + languageCode + ".ini";
        InputStream is = getClass().getResourceAsStream(fileName);
        
        // Fallback para inglês se o arquivo não existir
        if (is == null && !languageCode.equals("en")) {
            is = getClass().getResourceAsStream("/Messages_en.ini");
        }
        
        // Fallback final
        if (is == null) {
            is = getClass().getResourceAsStream("/Messages_en.ini");
        }
        
        if (is != null) {
            currentIni = new INIParser();
            try {
                currentIni.load(is);
            } catch (Exception e) {
                e.printStackTrace();
                currentIni = null;
            }
        }
    }
    
    public String getString(String section, String key) {
        if (currentIni == null) return "!" + section + "." + key + "!";
        String value = currentIni.getString(section, key);
        return value != null ? value : "!" + section + "." + key + "!";
    }
    
    public String getString(String section, String key, Object... args) {
        String pattern = getString(section, key);
        try {
            return MessageFormat.format(pattern, args);
        } catch (Exception e) {
            return pattern;
        }
    }
    
    // Convenience methods for specific sections
    
    public String getMenu(String key) {
        return getString("Menu", key);
    }
    
    public String getToolbar(String key) {
        return getString("Toolbar", key);
    }
    
    public String getPanel(String key) {
        return getString("Panels", key);
    }
    
    public String getTab(String key) {
        return getString("Tabs", key);
    }
    
    public String getLabel(String key) {
        return getString("Labels", key);
    }
    
    public String getButton(String key) {
        return getString("Buttons", key);
    }
    
    public String getStatus(String key) {
        return getString("Status", key);
    }
    
    public String getStatus(String key, Object... args) {
        return getString("Status", key, args);
    }
    
    public String getTheme(String key) {
        return getString("Theme", key);
    }
    
    public String getLanguage(String key) {
        return getString("Language", key);
    }
    
    public String getDialog(String key) {
        return getString("Dialog", key);
    }
    
    public String getDialog(String key, Object... args) {
        return getString("Dialog", key, args);
    }
    
    public String getError(String key) {
        return getString("Error", key);
    }
    
    public String getError(String key, Object... args) {
        return getString("Error", key, args);
    }
    
    public String getSuccess(String key) {
        return getString("Success", key);
    }
    
    public String getSuccess(String key, Object... args) {
        return getString("Success", key, args);
    }
    
    public String getValidation(String key) {
        return getString("Validation", key);
    }
    
    public String getValidation(String key, Object... args) {
        return getString("Validation", key, args);
    }
    
    public String getGeneral(String key) {
        return getString("General", key);
    }
    
    public Locale getCurrentLocale() {
        return currentLocale;
    }
}