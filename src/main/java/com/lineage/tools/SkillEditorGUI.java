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

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class SkillEditorGUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private SkillManager skillManager;
    private JTable skillTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private Skill currentSkill;
    private boolean darkMode = true;
    
    // Internationalization
    private ResourceBundle messages;
    private Locale currentLocale = Locale.ENGLISH;
    
    // Avaiable languages
	private final Locale[] supportedLocales = {
    Locale.ENGLISH,
    Locale.of("pt", "PT"),  // New method in Java 19+
    Locale.of("es", "ES"),   // New method in Java 19+
    Locale.of("fr", "FR"),   // New method in Java 19+
    Locale.of("de", "DE"),
    Locale.of("ro", "RO"),   // New method in Java 19+
    Locale.of("ja", "JA"),   // New method in Java 19+
    Locale.of("ru", "RU"),   // New method in Java 19+
    Locale.of("el", "EL")   // New method in Java 19+
	};
    
    private final String[] languageNames = {"English", "Português", "Español"," Deutsch",  "Français", "Română", "日本語", "Русский", "Ελληνικά"};
    
    // Components that need to be updated with the language.
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JTabbedPane tabbedPane;
    private JLabel statusLabel;
    private JButton btnSave;
    private JLabel searchLabel;
    
    // Editor components
    private JTextField txtSkillId, txtName, txtLevels;
    private JTextField txtEnchantGroup1, txtEnchantGroup2, txtEnchantGroup3, txtEnchantGroup4;
    private JTable setsTable;
    private DefaultTableModel setsTableModel;
    private JTable tablesTable;
    private DefaultTableModel tablesTableModel;
    private JTextArea effectsArea;
    private JTextArea conditionsArea;
    
    public SkillEditorGUI() {
        // Default Language English
        setLanguage(Locale.ENGLISH);
        
        // Apply dark theme by default
        applyTheme(true);
        
        skillManager = new SkillManager();
        initComponents();
        setIcon();
    }
    
    private void openItemEditor() {
        SwingUtilities.invokeLater(() -> {
            ItemEditorGUI editor = new ItemEditorGUI(currentLocale);
            editor.setVisible(true);
        });
    }
    
    private void setLanguage(Locale locale) {
        currentLocale = locale;
        messages = ResourceBundle.getBundle("Messages", locale);
        
        // If the interface has already been created, update it.
        if (menuBar != null) {
            updateUILanguage();
        }
    }
    
    private void updateUILanguage() {
        setTitle(getMsg("app.title") + " - Advanced Edition");
        
        // Recreate menu
        JMenuBar newMenuBar = createMenuBar();
        setJMenuBar(newMenuBar);
        
        // Recreate toolbar
        if (toolBar != null) {
            getContentPane().remove(toolBar);
        }
        toolBar = createToolBar();
        add(toolBar, BorderLayout.NORTH);
        
        // Update panel titles
        if (leftPanel != null) {
            leftPanel.setBorder(BorderFactory.createTitledBorder(getMsg("panel.skills")));
        }
        if (rightPanel != null) {
            rightPanel.setBorder(BorderFactory.createTitledBorder(getMsg("panel.editor")));
        }
        
        // Update search label
        if (searchLabel != null) {
            searchLabel.setText("🔍 " + getMsg("panel.search") + ": ");
        }
        
        // Update abas
        if (tabbedPane != null) {
            tabbedPane.setTitleAt(0, getMsg("tab.basic"));
            tabbedPane.setTitleAt(1, getMsg("tab.sets"));
            tabbedPane.setTitleAt(2, getMsg("tab.tables"));
            tabbedPane.setTitleAt(3, getMsg("tab.effects"));
            tabbedPane.setTitleAt(4, getMsg("tab.conditions"));
        }
        
        // Update save button
        if (btnSave != null) {
            btnSave.setText(getMsg("button.save"));
        }
        
        // Update status
        updateStatus(getMsg("status.ready"));
        
        revalidate();
        repaint();
    }
    
    private String getMsg(String key) {
        try {
            return messages.getString(key);
        } catch (Exception e) {
            return key;
        }
    }
    
    private void applyTheme(boolean dark) {
        try {
            if (dark) {
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
                darkMode = true;
            } else {
                UIManager.setLookAndFeel(new FlatMacLightLaf());
                darkMode = false;
            }
            if (isVisible()) {
                SwingUtilities.updateComponentTreeUI(this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void setIcon() {
        URL iconURL = getClass().getClassLoader().getResource("icon.png");
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            setIconImage(icon.getImage());
        }
    }
    
    private ImageIcon loadIcon(String path, int width, int height) {
        URL iconURL = getClass().getClassLoader().getResource(path);
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }
    
    private void initComponents() {
        setTitle(getMsg("app.title") + " - Advanced Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        
        // Menu
        menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        // Toolbar
        toolBar = createToolBar();
        add(toolBar, BorderLayout.NORTH);
        
       // Split main panel
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(450);
        
        // ===== Left Panel =====
        leftPanel = createLeftPanel();
        mainSplit.setLeftComponent(leftPanel);
        
        // ===== Right Panel =====
        rightPanel = createRightPanel();
        mainSplit.setRightComponent(rightPanel);
        
        add(mainSplit, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar newMenuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu(getMsg("menu.file"));
        fileMenu.setMnemonic('F');
        
        JMenuItem openItem = createMenuItem(getMsg("menu.file.open"), "icons/open.png", 
            KeyStroke.getKeyStroke("ctrl O"), e -> loadXml());
        JMenuItem saveItem = createMenuItem(getMsg("menu.file.save"), "icons/save.png", 
            KeyStroke.getKeyStroke("ctrl S"), e -> saveXml());
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        
        JMenuItem exitItem = createMenuItem(getMsg("menu.file.exit"), null, null, e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Tools Menu
        JMenu toolsMenu = new JMenu(getMsg("menu.tools"));
        toolsMenu.setMnemonic('T');
        
        JMenuItem itemEditorItem = new JMenuItem("Item Editor");
        ImageIcon itemIcon = loadIcon("icons/item.png", 16, 16);
        if (itemIcon != null) itemEditorItem.setIcon(itemIcon);
        itemEditorItem.addActionListener(e -> openItemEditor());
        toolsMenu.add(itemEditorItem);
        
        JMenuItem validateItem = createMenuItem(getMsg("menu.tools.validate"), "icons/validate.png", 
            null, e -> validateSkills());
        JMenuItem exportItem = createMenuItem(getMsg("menu.tools.export"), "icons/export.png", 
            null, e -> exportToCsv());
        toolsMenu.add(validateItem);
        toolsMenu.add(exportItem);
        toolsMenu.addSeparator();
        
        // Theme Submenu
        JMenu themeMenu = new JMenu(getMsg("menu.tools.theme"));
        ButtonGroup themeGroup = new ButtonGroup();
        
        JRadioButtonMenuItem darkTheme = new JRadioButtonMenuItem(getMsg("theme.dark"), darkMode);
        darkTheme.addActionListener(e -> applyTheme(true));
        themeGroup.add(darkTheme);
        themeMenu.add(darkTheme);
        
        JRadioButtonMenuItem lightTheme = new JRadioButtonMenuItem(getMsg("theme.light"), !darkMode);
        lightTheme.addActionListener(e -> applyTheme(false));
        themeGroup.add(lightTheme);
        themeMenu.add(lightTheme);
        
        toolsMenu.add(themeMenu);
        
        // Language Submenu
        JMenu languageMenu = new JMenu(getMsg("menu.tools.language"));
        ButtonGroup languageGroup = new ButtonGroup();
        
        for (int i = 0; i < supportedLocales.length; i++) {
            Locale loc = supportedLocales[i];
            String langName = languageNames[i];
            JRadioButtonMenuItem langItem = new JRadioButtonMenuItem(langName, 
                loc.equals(currentLocale));
            int index = i;
            langItem.addActionListener(e -> setLanguage(supportedLocales[index]));
            languageGroup.add(langItem);
            languageMenu.add(langItem);
        }
        
        toolsMenu.add(languageMenu);
        
        // Skill Tree Editor
        toolsMenu.addSeparator();
        JMenuItem skillTreeItem = new JMenuItem("Skill Tree Editor");
        ImageIcon treeIcon = loadIcon("icons/tree.png", 16, 16);
        if (treeIcon != null) skillTreeItem.setIcon(treeIcon);
        skillTreeItem.addActionListener(e -> openSkillTreeEditor());
        toolsMenu.add(skillTreeItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu(getMsg("menu.help"));
        helpMenu.setMnemonic('H');
        
        JMenuItem aboutItem = createMenuItem(getMsg("menu.help.about"), null, null, e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        newMenuBar.add(fileMenu);
        newMenuBar.add(toolsMenu);
        newMenuBar.add(helpMenu);
        
        return newMenuBar;
    }
    
    private JMenuItem createMenuItem(String text, String iconPath, KeyStroke accelerator, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        if (iconPath != null) {
            ImageIcon icon = loadIcon(iconPath, 16, 16);
            if (icon != null) item.setIcon(icon);
        }
        if (accelerator != null) item.setAccelerator(accelerator);
        item.addActionListener(action);
        return item;
    }
    
    private JToolBar createToolBar() {
        JToolBar newToolBar = new JToolBar();
        newToolBar.setFloatable(false);
        
        addToolBarButton(newToolBar, getMsg("toolbar.new"), "icons/new.png", e -> newSkill());
        addToolBarButton(newToolBar, getMsg("toolbar.open"), "icons/open.png", e -> loadXml());
        addToolBarButton(newToolBar, getMsg("toolbar.save"), "icons/save.png", e -> saveXml());
        newToolBar.addSeparator();
        addToolBarButton(newToolBar, getMsg("toolbar.clone"), "icons/clone.png", e -> cloneSkill());
        addToolBarButton(newToolBar, getMsg("toolbar.delete"), "icons/delete.png", e -> deleteSkill());
        newToolBar.addSeparator();
        addToolBarButton(newToolBar, getMsg("toolbar.validate"), "icons/validate.png", e -> validateSkills());
        addToolBarButton(newToolBar, getMsg("toolbar.export"), "icons/export.png", e -> exportToCsv());
        
        return newToolBar;
    }
    
    private void addToolBarButton(JToolBar toolBar, String tooltip, String iconPath, ActionListener action) {
        JButton button = new JButton();
        ImageIcon icon = loadIcon(iconPath, 55, 20);
        if (icon != null) {
            button.setIcon(icon);
        }
        button.setToolTipText(tooltip);
        button.addActionListener(action);
        button.setPreferredSize(new Dimension(60, 40));
        toolBar.add(button);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(getMsg("panel.skills")));
        
        // Painel de busca
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchLabel = new JLabel("🔍 " + getMsg("panel.search") + ": ");
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterSkills(); }
            public void removeUpdate(DocumentEvent e) { filterSkills(); }
            public void insertUpdate(DocumentEvent e) { filterSkills(); }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Tabela de skills
        String[] columns = {"ID", "Name", "Levels", "Enchant"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        skillTable = new JTable(tableModel);
        skillTable.setRowHeight(25);
        skillTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedSkill();
            }
        });
        
        skillTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    int modelRow = table.convertRowIndexToModel(row);
                    int skillId = (int) tableModel.getValueAt(modelRow, 0);
                    Skill skill = skillManager.findSkillById(skillId);
                    
                    if (skill != null && (skill.getEnchantGroup1() != null || 
                                           skill.getEnchantGroup2() != null ||
                                           skill.getEnchantGroup3() != null ||
                                           skill.getEnchantGroup4() != null)) {
                        c.setBackground(new Color(70, 70, 100));
                    } else {
                        c.setBackground(table.getBackground());
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(skillTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(getMsg("panel.editor")));
        
        // Tabs for different aspects of the skill.
        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab(getMsg("tab.basic"), createBasicAttributesPanel());
        tabbedPane.addTab(getMsg("tab.sets"), createSetsPanel());
        tabbedPane.addTab(getMsg("tab.tables"), createTablesPanel());
        tabbedPane.addTab(getMsg("tab.effects"), createEffectsPanel());
        tabbedPane.addTab(getMsg("tab.conditions"), createConditionsPanel());
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        // Save Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton(getMsg("button.save"));
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(150, 35));
        btnSave.addActionListener(e -> saveSkillChanges());
        buttonPanel.add(btnSave);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBasicAttributesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(getMsg("label.skillId")), gbc);
        gbc.gridx = 1;
        txtSkillId = new JTextField(20);
        txtSkillId.setEditable(false);
        txtSkillId.setBackground(new Color(60, 60, 60));
        panel.add(txtSkillId, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(getMsg("label.name")), gbc);
        gbc.gridx = 1;
        txtName = new JTextField(20);
        panel.add(txtName, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(getMsg("label.levels")), gbc);
        gbc.gridx = 1;
        txtLevels = new JTextField(20);
        panel.add(txtLevels, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(getMsg("label.enchant1")), gbc);
        gbc.gridx = 1;
        txtEnchantGroup1 = new JTextField(20);
        panel.add(txtEnchantGroup1, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(getMsg("label.enchant2")), gbc);
        gbc.gridx = 1;
        txtEnchantGroup2 = new JTextField(20);
        panel.add(txtEnchantGroup2, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(getMsg("label.enchant3")), gbc);
        gbc.gridx = 1;
        txtEnchantGroup3 = new JTextField(20);
        panel.add(txtEnchantGroup3, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(getMsg("label.enchant4")), gbc);
        gbc.gridx = 1;
        txtEnchantGroup4 = new JTextField(20);
        panel.add(txtEnchantGroup4, gbc);
        
        return panel;
    }
    
    private JPanel createSetsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Name", "Value"};
        setsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        
        setsTable = new JTable(setsTableModel);
        setsTable.setRowHeight(25);
        panel.add(new JScrollPane(setsTable), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton(getMsg("button.addSet"));
        btnAdd.addActionListener(e -> addSet());
        JButton btnRemove = new JButton(getMsg("button.remove"));
        btnRemove.addActionListener(e -> removeSet());
        JButton btnEdit = new JButton(getMsg("button.edit"));
        btnEdit.addActionListener(e -> editSet());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnEdit);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTablesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Name", "Values"};
        tablesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        
        tablesTable = new JTable(tablesTableModel);
        tablesTable.setRowHeight(25);
        panel.add(new JScrollPane(tablesTable), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton(getMsg("button.addTable"));
        btnAdd.addActionListener(e -> addTable());
        JButton btnRemove = new JButton(getMsg("button.remove"));
        btnRemove.addActionListener(e -> removeTable());
        JButton btnEdit = new JButton(getMsg("button.edit"));
        btnEdit.addActionListener(e -> editTable());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnEdit);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createEffectsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        effectsArea = new JTextArea();
        effectsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        effectsArea.setEditable(true);
        panel.add(new JScrollPane(effectsArea), BorderLayout.CENTER);
        
        JLabel infoLabel = new JLabel(getMsg("label.effects"));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(infoLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createConditionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        conditionsArea = new JTextArea();
        conditionsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        conditionsArea.setEditable(true);
        panel.add(new JScrollPane(conditionsArea), BorderLayout.CENTER);
        
        JLabel infoLabel = new JLabel(getMsg("label.conditions"));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(infoLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        statusLabel = new JLabel(getMsg("status.ready"));
        statusLabel.setFont(statusLabel.getFont().deriveFont(11f));
        statusBar.add(statusLabel);
        
        return statusBar;
    }
    
    private void showAboutDialog() {
        String message = getMsg("dialog.about.message");
        JOptionPane.showMessageDialog(this, message, getMsg("dialog.about"), 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openSkillTreeEditor() {
        SwingUtilities.invokeLater(() -> {
            SkillTreeEditorGUI editor = new SkillTreeEditorGUI(currentLocale);
            editor.setVisible(true);
        });
    }
    
		// ========== FUNCTIONALITY METHODS ==========
    
    private void loadXml() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML files", "xml"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                skillManager.loadFromFile(file);
                refreshSkillTable(skillManager.getSkills());
                JOptionPane.showMessageDialog(this, 
                    getMsg("success.loaded") + " " + skillManager.getSkills().size() + " skills");
                updateStatus(getMsg("status.loaded") + " " + skillManager.getSkills().size() + " skills");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    getMsg("error.loadXML") + " " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void saveXml() {
        if (skillManager.getSkills().isEmpty()) {
            JOptionPane.showMessageDialog(this, getMsg("error.noSkill"));
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML files", "xml"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".xml")) {
                    file = new File(file.getAbsolutePath() + ".xml");
                }
                skillManager.saveToFile(file);
                JOptionPane.showMessageDialog(this, getMsg("success.saved"));
                updateStatus(getMsg("status.saved") + " " + file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    getMsg("error.saveXML") + " " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void refreshSkillTable(List<Skill> skills) {
        tableModel.setRowCount(0);
        for (Skill skill : skills) {
            String enchantInfo = "";
            if (skill.getEnchantGroup1() != null) enchantInfo += "1";
            if (skill.getEnchantGroup2() != null) enchantInfo += (enchantInfo.isEmpty() ? "2" : ",2");
            if (skill.getEnchantGroup3() != null) enchantInfo += (enchantInfo.isEmpty() ? "3" : ",3");
            if (skill.getEnchantGroup4() != null) enchantInfo += (enchantInfo.isEmpty() ? "4" : ",4");
            
            tableModel.addRow(new Object[]{
                skill.getSkillId(),
                skill.getName(),
                skill.getLevels(),
                enchantInfo
            });
        }
    }
    
    private void filterSkills() {
        String search = searchField.getText();
        if (search.isEmpty()) {
            refreshSkillTable(skillManager.getSkills());
        } else {
            refreshSkillTable(skillManager.searchSkills(search));
        }
    }
    
    private void loadSelectedSkill() {
        int selectedRow = skillTable.getSelectedRow();
        if (selectedRow < 0) return;
        
        int skillId = (int) tableModel.getValueAt(selectedRow, 0);
        currentSkill = skillManager.findSkillById(skillId);
        
        if (currentSkill == null) return;
        
        txtSkillId.setText(String.valueOf(currentSkill.getSkillId()));
        txtName.setText(currentSkill.getName());
        txtLevels.setText(String.valueOf(currentSkill.getLevels()));
        
        txtEnchantGroup1.setText(currentSkill.getEnchantGroup1() != null ? currentSkill.getEnchantGroup1() : "");
        txtEnchantGroup2.setText(currentSkill.getEnchantGroup2() != null ? currentSkill.getEnchantGroup2() : "");
        txtEnchantGroup3.setText(currentSkill.getEnchantGroup3() != null ? currentSkill.getEnchantGroup3() : "");
        txtEnchantGroup4.setText(currentSkill.getEnchantGroup4() != null ? currentSkill.getEnchantGroup4() : "");
        
        setsTableModel.setRowCount(0);
        if (currentSkill.getSets() != null) {
            for (SkillSet set : currentSkill.getSets()) {
                setsTableModel.addRow(new Object[]{set.getName(), set.getVal()});
            }
        }
        
        tablesTableModel.setRowCount(0);
        if (currentSkill.getTables() != null) {
            for (SkillTable table : currentSkill.getTables()) {
                tablesTableModel.addRow(new Object[]{table.getName(), table.getValues()});
            }
        }
        
        effectsArea.setText(currentSkill.getEffects() != null ? currentSkill.getEffects().toString() : "");
        conditionsArea.setText(currentSkill.getConditions() != null ? currentSkill.getConditions().toString() : "");
    }
    
    private void saveSkillChanges() {
        if (currentSkill == null) {
            JOptionPane.showMessageDialog(this, getMsg("error.noSkill"));
            return;
        }
        
        currentSkill.setName(txtName.getText());
        try {
            currentSkill.setLevels(Integer.parseInt(txtLevels.getText()));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, getMsg("error.invalidLevels"));
            return;
        }
        
        currentSkill.setEnchantGroup1(emptyToNull(txtEnchantGroup1.getText()));
        currentSkill.setEnchantGroup2(emptyToNull(txtEnchantGroup2.getText()));
        currentSkill.setEnchantGroup3(emptyToNull(txtEnchantGroup3.getText()));
        currentSkill.setEnchantGroup4(emptyToNull(txtEnchantGroup4.getText()));
        
        // Save sets
        List<SkillSet> newSets = new ArrayList<>();
        for (int i = 0; i < setsTableModel.getRowCount(); i++) {
            String name = (String) setsTableModel.getValueAt(i, 0);
            String value = (String) setsTableModel.getValueAt(i, 1);
            if (name != null && !name.trim().isEmpty()) {
                SkillSet set = new SkillSet();
                set.setName(name);
                set.setVal(value != null ? value : "");
                newSets.add(set);
            }
        }
        currentSkill.setSets(newSets);
        
        // Save tables
        List<SkillTable> newTables = new ArrayList<>();
        for (int i = 0; i < tablesTableModel.getRowCount(); i++) {
            String name = (String) tablesTableModel.getValueAt(i, 0);
            String values = (String) tablesTableModel.getValueAt(i, 1);
            if (name != null && !name.trim().isEmpty()) {
                SkillTable table = new SkillTable();
                table.setName(name);
                table.setValues(values != null ? values : "");
                newTables.add(table);
            }
        }
        currentSkill.setTables(newTables);
        
        JOptionPane.showMessageDialog(this, getMsg("success.updated"));
        refreshSkillTable(skillManager.getSkills());
        updateStatus(getMsg("status.updated") + " " + currentSkill.getSkillId());
    }
    
    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
    
    private void newSkill() {
        int newId = skillManager.getNextSkillId();
        Skill skill = new Skill();
        skill.setSkillId(newId);
        skill.setName("New Skill");
        skill.setLevels(1);
        
        skillManager.addSkill(skill);
        refreshSkillTable(skillManager.getSkills());
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 0) == newId) {
                skillTable.setRowSelectionInterval(i, i);
                break;
            }
        }
        
        updateStatus(getMsg("status.created") + " " + newId);
    }
    
    private void cloneSkill() {
        if (currentSkill == null) {
            JOptionPane.showMessageDialog(this, getMsg("error.selectSkill"));
            return;
        }
        
        Skill clone = new Skill();
        clone.setSkillId(skillManager.getNextSkillId());
        clone.setName(currentSkill.getName() + " (Clone)");
        clone.setLevels(currentSkill.getLevels());
        clone.setEnchantGroup1(currentSkill.getEnchantGroup1());
        clone.setEnchantGroup2(currentSkill.getEnchantGroup2());
        clone.setEnchantGroup3(currentSkill.getEnchantGroup3());
        clone.setEnchantGroup4(currentSkill.getEnchantGroup4());
        
        skillManager.addSkill(clone);
        refreshSkillTable(skillManager.getSkills());
        updateStatus(getMsg("status.created") + " " + clone.getSkillId());
    }
    
    private void deleteSkill() {
        if (currentSkill == null) {
            JOptionPane.showMessageDialog(this, getMsg("error.selectSkillDelete"));
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete skill: " + currentSkill.getName() + " (ID: " + currentSkill.getSkillId() + ")?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            skillManager.removeSkill(currentSkill);
            refreshSkillTable(skillManager.getSkills());
            currentSkill = null;
            clearEditor();
            updateStatus(getMsg("status.deleted"));
        }
    }
    
    private void addSet() {
        String name = JOptionPane.showInputDialog(this, "Enter set name:");
        if (name != null && !name.trim().isEmpty()) {
            setsTableModel.addRow(new Object[]{name, ""});
        }
    }
    
    private void removeSet() {
        int selectedRow = setsTable.getSelectedRow();
        if (selectedRow >= 0) {
            setsTableModel.removeRow(selectedRow);
        }
    }
    
    private void editSet() {
        int selectedRow = setsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String name = (String) setsTableModel.getValueAt(selectedRow, 0);
            String value = (String) setsTableModel.getValueAt(selectedRow, 1);
            String newValue = JOptionPane.showInputDialog(this, "Edit value for " + name + ":", value);
            if (newValue != null) {
                setsTableModel.setValueAt(newValue, selectedRow, 1);
            }
        }
    }
    
    private void addTable() {
        String name = JOptionPane.showInputDialog(this, "Enter table name (e.g., #power):");
        if (name != null && !name.trim().isEmpty()) {
            tablesTableModel.addRow(new Object[]{name, ""});
        }
    }
    
    private void removeTable() {
        int selectedRow = tablesTable.getSelectedRow();
        if (selectedRow >= 0) {
            tablesTableModel.removeRow(selectedRow);
        }
    }
    
    private void editTable() {
        int selectedRow = tablesTable.getSelectedRow();
        if (selectedRow >= 0) {
            String name = (String) tablesTableModel.getValueAt(selectedRow, 0);
            String values = (String) tablesTableModel.getValueAt(selectedRow, 1);
            String newValues = JOptionPane.showInputDialog(this, 
                "Edit values for " + name + " (space-separated):", values);
            if (newValues != null) {
                tablesTableModel.setValueAt(newValues, selectedRow, 1);
            }
        }
    }
    
    private void clearEditor() {
        txtSkillId.setText("");
        txtName.setText("");
        txtLevels.setText("");
        txtEnchantGroup1.setText("");
        txtEnchantGroup2.setText("");
        txtEnchantGroup3.setText("");
        txtEnchantGroup4.setText("");
        setsTableModel.setRowCount(0);
        tablesTableModel.setRowCount(0);
        effectsArea.setText("");
        conditionsArea.setText("");
    }
    
    private void validateSkills() {
        StringBuilder report = new StringBuilder(getMsg("validation.title") + "\n\n");
        int errors = 0;
        
        for (Skill skill : skillManager.getSkills()) {
            if (skill.getSkillId() <= 0) {
                report.append(getMsg("validation.invalidID") + " " + skill).append("\n");
                errors++;
            }
            if (skill.getName() == null || skill.getName().trim().isEmpty()) {
                report.append(getMsg("validation.emptyName") + " " + skill.getSkillId()).append("\n");
                errors++;
            }
            if (skill.getLevels() <= 0) {
                report.append(getMsg("validation.invalidLevels") + " " + skill).append("\n");
                errors++;
            }
        }
        
        if (errors == 0) {
            report.append(getMsg("validation.allValid"));
        } else {
            report.append("\n" + getMsg("validation.errors") + " " + errors);
        }
        
        JOptionPane.showMessageDialog(this, report.toString(), getMsg("validation.title"), 
            errors == 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }
    
    private void exportToCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("skills_export.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                writer.println("ID,Name,Levels,Enchant Groups,Icon,OperateType,TargetType");
                
                for (Skill skill : skillManager.getSkills()) {
                    String enchantGroups = "";
                    if (skill.getEnchantGroup1() != null) enchantGroups += skill.getEnchantGroup1() + " ";
                    if (skill.getEnchantGroup2() != null) enchantGroups += skill.getEnchantGroup2() + " ";
                    if (skill.getEnchantGroup3() != null) enchantGroups += skill.getEnchantGroup3() + " ";
                    if (skill.getEnchantGroup4() != null) enchantGroups += skill.getEnchantGroup4();
                    
                    String icon = skill.getSetValue("icon");
                    String operateType = skill.getSetValue("operateType");
                    String targetType = skill.getSetValue("targetType");
                    
                    writer.printf("%d,\"%s\",%d,\"%s\",%s,%s,%s%n",
                        skill.getSkillId(),
                        skill.getName(),
                        skill.getLevels(),
                        enchantGroups,
                        icon != null ? icon : "",
                        operateType != null ? operateType : "",
                        targetType != null ? targetType : ""
                    );
                }
                
                JOptionPane.showMessageDialog(this, getMsg("success.exported"));
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    getMsg("error.saveXML") + " " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText("✅ " + message);
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new SkillEditorGUI().setVisible(true);
        });
    }
}