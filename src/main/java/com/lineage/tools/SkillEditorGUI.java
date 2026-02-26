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

@SuppressWarnings("unused")
public class SkillEditorGUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private SkillManager skillManager;
    private JTable skillTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private Skill currentSkill;
    private boolean darkMode = true;
    
    // Componentes do editor
    private JTextField txtSkillId, txtName, txtLevels;
    private JTextField txtEnchantGroup1, txtEnchantGroup2, txtEnchantGroup3, txtEnchantGroup4;
    private JTable setsTable;
    private DefaultTableModel setsTableModel;
    private JTable tablesTable;
    private DefaultTableModel tablesTableModel;
    private JTextArea effectsArea;
    private JTextArea conditionsArea;
    private JLabel statusLabel;
    
    public SkillEditorGUI() {
        // Aplicar tema escuro por padrão
        applyTheme(true);
        
        skillManager = new SkillManager();
        initComponents();
        setIcon();
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
        // Carregar ícone da janela
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
        // Retorna ícone padrão se não encontrar
        return null;
    }
    
    private void initComponents() {
        setTitle("Lineage Skill Editor - Advanced Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        
        // Menu
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        // Toolbar
        JToolBar toolBar = createToolBar();
        add(toolBar, BorderLayout.NORTH);
        
        // Painel principal dividido
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(450);
        
        // ===== PAINEL ESQUERDO =====
        mainSplit.setLeftComponent(createLeftPanel());
        
        // ===== PAINEL DIREITO =====
        mainSplit.setRightComponent(createRightPanel());
        
        add(mainSplit, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        JMenuItem openItem = createMenuItem("Open XML", "icons/open.png", 
            KeyStroke.getKeyStroke("ctrl O"), e -> loadXml());
        JMenuItem saveItem = createMenuItem("Save XML", "icons/save.png", 
            KeyStroke.getKeyStroke("ctrl S"), e -> saveXml());
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        
        JMenuItem exitItem = createMenuItem("Exit", null, null, e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic('T');
        
        JMenuItem validateItem = createMenuItem("Validate Skills", "icons/validate.png", 
            null, e -> validateSkills());
        JMenuItem exportItem = createMenuItem("Export to CSV", "icons/export.png", 
            null, e -> exportToCsv());
        toolsMenu.add(validateItem);
        toolsMenu.add(exportItem);
        toolsMenu.addSeparator();
        
        JCheckBoxMenuItem themeItem = new JCheckBoxMenuItem("Dark Mode", darkMode);
        themeItem.addActionListener(e -> {
            applyTheme(!darkMode);
            themeItem.setSelected(darkMode);
        });
        toolsMenu.add(themeItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        JMenuItem aboutItem = createMenuItem("About", null, null, e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
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
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        addToolBarButton(toolBar, "New", "icons/new.png", e -> newSkill());
        addToolBarButton(toolBar, "Open", "icons/open.png", e -> loadXml());
        addToolBarButton(toolBar, "Save", "icons/save.png", e -> saveXml());
        toolBar.addSeparator();
        addToolBarButton(toolBar, "Clone", "icons/clone.png", e -> cloneSkill());
        addToolBarButton(toolBar, "Delete", "icons/delete.png", e -> deleteSkill());
        toolBar.addSeparator();
        addToolBarButton(toolBar, "Add Set", "icons/add.png", e -> addSet());
        addToolBarButton(toolBar, "Add Table", "icons/add.png", e -> addTable());
        
        return toolBar;
    }
    
    private void addToolBarButton(JToolBar toolBar, String text, String iconPath, ActionListener action) {
        JButton button = new JButton(text);
        ImageIcon icon = loadIcon(iconPath, 20, 20);
        if (icon != null) {
            button.setIcon(icon);
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        button.addActionListener(action);
        toolBar.add(button);
    }
    
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Skills List"));
        
        // Painel de busca
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(new JLabel("🔍 Search: "), BorderLayout.WEST);
        searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterSkills(); }
            public void removeUpdate(DocumentEvent e) { filterSkills(); }
            public void insertUpdate(DocumentEvent e) { filterSkills(); }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        
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
        
        // Renderizador para destacar skills com enchant
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
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        return leftPanel;
    }
    
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Skill Editor"));
        
        // Abas para diferentes aspectos da skill
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Aba 1: Atributos Básicos
        tabbedPane.addTab("Basic Attributes", createBasicAttributesPanel());
        
        // Aba 2: Sets
        tabbedPane.addTab("Sets", createSetsPanel());
        
        // Aba 3: Tables
        tabbedPane.addTab("Tables", createTablesPanel());
        
        // Aba 4: Effects
        tabbedPane.addTab("Effects", createEffectsPanel());
        
        // Aba 5: Conditions
        tabbedPane.addTab("Conditions", createConditionsPanel());
        
        rightPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Botão Save
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("💾 Save Changes");
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(150, 35));
        btnSave.addActionListener(e -> saveSkillChanges());
        buttonPanel.add(btnSave);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return rightPanel;
    }
    
    private JPanel createBasicAttributesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Skill ID
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("🆔 Skill ID:"), gbc);
        gbc.gridx = 1;
        txtSkillId = new JTextField(20);
        txtSkillId.setEditable(false);
        txtSkillId.setBackground(new Color(60, 60, 60));
        panel.add(txtSkillId, gbc);
        row++;
        
        // Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("📝 Name:"), gbc);
        gbc.gridx = 1;
        txtName = new JTextField(20);
        panel.add(txtName, gbc);
        row++;
        
        // Levels
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("📊 Levels:"), gbc);
        gbc.gridx = 1;
        txtLevels = new JTextField(20);
        panel.add(txtLevels, gbc);
        row++;
        
        // Enchant Groups
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("✨ Enchant Group 1:"), gbc);
        gbc.gridx = 1;
        txtEnchantGroup1 = new JTextField(20);
        panel.add(txtEnchantGroup1, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("✨ Enchant Group 2:"), gbc);
        gbc.gridx = 1;
        txtEnchantGroup2 = new JTextField(20);
        panel.add(txtEnchantGroup2, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("✨ Enchant Group 3:"), gbc);
        gbc.gridx = 1;
        txtEnchantGroup3 = new JTextField(20);
        panel.add(txtEnchantGroup3, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("✨ Enchant Group 4:"), gbc);
        gbc.gridx = 1;
        txtEnchantGroup4 = new JTextField(20);
        panel.add(txtEnchantGroup4, gbc);
        
        return panel;
    }
    
    private JPanel createSetsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tabela de sets
        String[] columns = {"Name", "Value"};
        setsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        
        setsTable = new JTable(setsTableModel);
        setsTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(setsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botões para gerenciar sets
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Set");
        btnAdd.setIcon(loadIcon("icons/add.png", 16, 16));
        btnAdd.addActionListener(e -> addSet());
        
        JButton btnRemove = new JButton("Remove");
        btnRemove.setIcon(loadIcon("icons/remove.png", 16, 16));
        btnRemove.addActionListener(e -> removeSet());
        
        JButton btnEdit = new JButton("Edit");
        btnEdit.setIcon(loadIcon("icons/edit.png", 16, 16));
        btnEdit.addActionListener(e -> editSet());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnEdit);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTablesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tabela de tables
        String[] columns = {"Name", "Values"};
        tablesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        
        tablesTable = new JTable(tablesTableModel);
        tablesTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tablesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botões para gerenciar tables
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Table");
        btnAdd.setIcon(loadIcon("icons/add.png", 16, 16));
        btnAdd.addActionListener(e -> addTable());
        
        JButton btnRemove = new JButton("Remove");
        btnRemove.setIcon(loadIcon("icons/remove.png", 16, 16));
        btnRemove.addActionListener(e -> removeTable());
        
        JButton btnEdit = new JButton("Edit");
        btnEdit.setIcon(loadIcon("icons/edit.png", 16, 16));
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
        
        JScrollPane scrollPane = new JScrollPane(effectsArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JLabel infoLabel = new JLabel("📋 Effects (XML format)");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(infoLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createConditionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        conditionsArea = new JTextArea();
        conditionsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        conditionsArea.setEditable(true);
        
        JScrollPane scrollPane = new JScrollPane(conditionsArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JLabel infoLabel = new JLabel("⚙️ Conditions (XML format)");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(infoLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        statusLabel = new JLabel("✅ Ready");
        statusLabel.setFont(statusLabel.getFont().deriveFont(11f));
        statusBar.add(statusLabel);
        
        return statusBar;
    }
    
    private void showAboutDialog() {
        String message = "Lineage Skill Editor v2.0\n\n" +
                        "Professional tool for editing Lineage 2 server skills.\n" +
                        "Supports advanced XML format with enchant groups.\n\n" +
                        "© 2026 Stayway\n" +
                        "Licensed under MIT License";
        
        ImageIcon logo = loadIcon("logo.png", 64, 64);
        
        JOptionPane.showMessageDialog(this, message, "About", 
            JOptionPane.INFORMATION_MESSAGE, logo);
    }
    
    private void loadXml() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML files", "xml"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                skillManager.loadFromFile(file);
                refreshSkillTable(skillManager.getSkills());
                JOptionPane.showMessageDialog(this, 
                    "Loaded " + skillManager.getSkills().size() + " skills successfully!");
                updateStatus("Loaded " + skillManager.getSkills().size() + " skills");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading XML: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void saveXml() {
        if (skillManager.getSkills().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No skills to save!");
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
                JOptionPane.showMessageDialog(this, "XML saved successfully!");
                updateStatus("Saved to " + file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving XML: " + ex.getMessage(), 
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
        
        // Carregar atributos básicos
        txtSkillId.setText(String.valueOf(currentSkill.getSkillId()));
        txtName.setText(currentSkill.getName());
        txtLevels.setText(String.valueOf(currentSkill.getLevels()));
        
        txtEnchantGroup1.setText(currentSkill.getEnchantGroup1() != null ? currentSkill.getEnchantGroup1() : "");
        txtEnchantGroup2.setText(currentSkill.getEnchantGroup2() != null ? currentSkill.getEnchantGroup2() : "");
        txtEnchantGroup3.setText(currentSkill.getEnchantGroup3() != null ? currentSkill.getEnchantGroup3() : "");
        txtEnchantGroup4.setText(currentSkill.getEnchantGroup4() != null ? currentSkill.getEnchantGroup4() : "");
        
        // Carregar sets
        setsTableModel.setRowCount(0);
        if (currentSkill.getSets() != null) {
            for (SkillSet set : currentSkill.getSets()) {
                setsTableModel.addRow(new Object[]{set.getName(), set.getVal()});
            }
        }
        
        // Carregar tables
        tablesTableModel.setRowCount(0);
        if (currentSkill.getTables() != null) {
            for (SkillTable table : currentSkill.getTables()) {
                tablesTableModel.addRow(new Object[]{table.getName(), table.getValues()});
            }
        }
        
        // Carregar effects (simplificado)
        if (currentSkill.getEffects() != null) {
            effectsArea.setText(currentSkill.getEffects().toString());
        } else {
            effectsArea.setText("");
        }
        
        // Carregar conditions (simplificado)
        if (currentSkill.getConditions() != null) {
            conditionsArea.setText(currentSkill.getConditions().toString());
        } else {
            conditionsArea.setText("");
        }
    }
    
    private void saveSkillChanges() {
        if (currentSkill == null) {
            JOptionPane.showMessageDialog(this, "No skill selected!");
            return;
        }
        
        // Salvar atributos básicos
        currentSkill.setName(txtName.getText());
        try {
            currentSkill.setLevels(Integer.parseInt(txtLevels.getText()));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid levels value!");
            return;
        }
        
        // Salvar enchant groups
        currentSkill.setEnchantGroup1(emptyToNull(txtEnchantGroup1.getText()));
        currentSkill.setEnchantGroup2(emptyToNull(txtEnchantGroup2.getText()));
        currentSkill.setEnchantGroup3(emptyToNull(txtEnchantGroup3.getText()));
        currentSkill.setEnchantGroup4(emptyToNull(txtEnchantGroup4.getText()));
        
        // Salvar sets
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
        
        // Salvar tables
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
        
        JOptionPane.showMessageDialog(this, "Skill updated successfully!");
        refreshSkillTable(skillManager.getSkills());
        updateStatus("Skill " + currentSkill.getSkillId() + " updated");
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
        
        // Selecionar a nova skill
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 0) == newId) {
                skillTable.setRowSelectionInterval(i, i);
                break;
            }
        }
        
        updateStatus("New skill created with ID: " + newId);
    }
    
    private void cloneSkill() {
        if (currentSkill == null) {
            JOptionPane.showMessageDialog(this, "Select a skill to clone!");
            return;
        }
        
        Skill clone = new Skill();
        clone.setSkillId(skillManager.getNextSkillId());
        clone.setName(currentSkill.getName() + " (Clone)");
        clone.setLevels(currentSkill.getLevels());
        
        // Clonar enchant groups
        clone.setEnchantGroup1(currentSkill.getEnchantGroup1());
        clone.setEnchantGroup2(currentSkill.getEnchantGroup2());
        clone.setEnchantGroup3(currentSkill.getEnchantGroup3());
        clone.setEnchantGroup4(currentSkill.getEnchantGroup4());
        
        skillManager.addSkill(clone);
        refreshSkillTable(skillManager.getSkills());
        
        updateStatus("Skill cloned with ID: " + clone.getSkillId());
    }
    
    private void deleteSkill() {
        if (currentSkill == null) {
            JOptionPane.showMessageDialog(this, "Select a skill to delete!");
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
            updateStatus("Skill deleted");
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
            
            String newValue = JOptionPane.showInputDialog(this, 
                "Edit value for " + name + ":", value);
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
        StringBuilder report = new StringBuilder("Validation Report:\n\n");
        int errors = 0;
        
        for (Skill skill : skillManager.getSkills()) {
            if (skill.getSkillId() <= 0) {
                report.append("❌ Invalid ID: ").append(skill).append("\n");
                errors++;
            }
            if (skill.getName() == null || skill.getName().trim().isEmpty()) {
                report.append("❌ Empty name for ID: ").append(skill.getSkillId()).append("\n");
                errors++;
            }
            if (skill.getLevels() <= 0) {
                report.append("❌ Invalid levels for: ").append(skill).append("\n");
                errors++;
            }
        }
        
        if (errors == 0) {
            report.append("✅ All skills are valid!");
        } else {
            report.append("\nFound ").append(errors).append(" error(s)");
        }
        
        JOptionPane.showMessageDialog(this, report.toString(), "Validation Result", 
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
                
                JOptionPane.showMessageDialog(this, "Export completed successfully!");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting: " + ex.getMessage(), 
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