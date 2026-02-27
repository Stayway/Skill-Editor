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
import java.util.prefs.Preferences;

@SuppressWarnings("unused")
public class ItemEditorGUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private static final String PREF_LAST_DIR = "lastItemDirectory";
    private Preferences prefs;    

    private ItemManager itemManager;
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private Item currentItem;
    private boolean darkMode = true;
    private Locale currentLocale;
    
    // Editor components
    private JTextField txtItemId, txtName, txtType;
    private JTable setsTable;
    private DefaultTableModel setsTableModel;
    private JTable statsTable;
    private DefaultTableModel statsTableModel;
    private JTable skillsTable;
    private DefaultTableModel skillsTableModel;
    private JLabel statusLabel;
    
    public ItemEditorGUI(Locale locale) {
    	prefs = Preferences.userNodeForPackage(ItemEditorGUI.class);
    	this.currentLocale = locale;
        
        // Apply dark theme by default
        applyTheme(true);
        
        itemManager = new ItemManager();
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
        setTitle("Lineage Item Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1300, 700);
        setLocationRelativeTo(null);
        
        // Menu
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        // Toolbar
        JToolBar toolBar = createToolBar();
        add(toolBar, BorderLayout.NORTH);
        
        // Split main panel
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(400);
        
        // ===== LEFT PANEL =====
        mainSplit.setLeftComponent(createLeftPanel());
        
        // ===== RIGHT PANEL =====
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
        
        JMenuItem openItem = new JMenuItem("Open XML");
        openItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        openItem.addActionListener(_ -> loadXml());
        
        JMenuItem saveItem = new JMenuItem("Save XML");
        saveItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        saveItem.addActionListener(e -> saveXml());
        
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Close");
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        
        // Theme Menu
        JMenu themeMenu = new JMenu("Theme");
        JCheckBoxMenuItem darkThemeItem = new JCheckBoxMenuItem("Dark Mode", darkMode);
        darkThemeItem.addActionListener(e -> {
            applyTheme(!darkMode);
            darkThemeItem.setSelected(darkMode);
        });
        themeMenu.add(darkThemeItem);
        menuBar.add(themeMenu);
        
        return menuBar;
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        addToolBarButton(toolBar, "New Item", "icons/new.png", e -> newItem());
        addToolBarButton(toolBar, "Open XML", "icons/open.png", e -> loadXml());
        addToolBarButton(toolBar, "Save XML", "icons/save.png", e -> saveXml());
        toolBar.addSeparator();
        addToolBarButton(toolBar, "Clone", "icons/clone.png", e -> cloneItem());
        addToolBarButton(toolBar, "Delete", "icons/delete.png", e -> deleteItem());
        
        return toolBar;
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
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Items List"));
        
        // Painel de busca
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(new JLabel("🔍 Search: "), BorderLayout.WEST);
        searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterItems(); }
            public void removeUpdate(DocumentEvent e) { filterItems(); }
            public void insertUpdate(DocumentEvent e) { filterItems(); }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Tabela de items
        String[] columns = {"ID", "Name", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        itemTable = new JTable(tableModel);
        itemTable.setRowHeight(25);
        itemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedItem();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(itemTable);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        return leftPanel;
    }
    
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Item Editor"));
        
        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Basic Attributes
        tabbedPane.addTab("Basic Attributes", createBasicAttributesPanel());
        
        // Tab 2: Sets
        tabbedPane.addTab("Properties (Sets)", createSetsPanel());
        
        // Tab 3: Stats
        tabbedPane.addTab("Stats", createStatsPanel());
        
        // Tab 4: Skills
        tabbedPane.addTab("Skills", createSkillsPanel());
        
        rightPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Save Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("💾 Save Changes");
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(150, 35));
        btnSave.addActionListener(e -> saveItemChanges());
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
        
        // Item ID
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("🆔 Item ID:"), gbc);
        gbc.gridx = 1;
        txtItemId = new JTextField(20);
        txtItemId.setEditable(false);
        txtItemId.setBackground(new Color(60, 60, 60));
        panel.add(txtItemId, gbc);
        row++;
        
        // Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("📝 Name:"), gbc);
        gbc.gridx = 1;
        txtName = new JTextField(20);
        panel.add(txtName, gbc);
        row++;
        
        // Type
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("🔧 Type:"), gbc);
        gbc.gridx = 1;
        txtType = new JTextField(20);
        panel.add(txtType, gbc);
        row++;
        
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
        JButton btnAdd = new JButton("Add Set");
        btnAdd.addActionListener(e -> addSet());
        JButton btnRemove = new JButton("Remove");
        btnRemove.addActionListener(e -> removeSet());
        JButton btnEdit = new JButton("Edit");
        btnEdit.addActionListener(e -> editSet());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnEdit);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Stat Type", "Value"};
        statsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        
        statsTable = new JTable(statsTableModel);
        statsTable.setRowHeight(25);
        panel.add(new JScrollPane(statsTable), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Stat");
        btnAdd.addActionListener(e -> addStat());
        JButton btnRemove = new JButton("Remove");
        btnRemove.addActionListener(e -> removeStat());
        JButton btnEdit = new JButton("Edit");
        btnEdit.addActionListener(e -> editStat());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnEdit);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSkillsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Skill ID", "Level"};
        skillsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        
        skillsTable = new JTable(skillsTableModel);
        skillsTable.setRowHeight(25);
        panel.add(new JScrollPane(skillsTable), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Skill");
        btnAdd.addActionListener(e -> addSkill());
        JButton btnRemove = new JButton("Remove");
        btnRemove.addActionListener(e -> removeSkill());
        JButton btnEdit = new JButton("Edit");
        btnEdit.addActionListener(e -> editSkill());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnEdit);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
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
    
    // ========== FUNCTIONAL METHODS ==========
    
    private void loadXml() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML files", "xml"));
        String lastDir = prefs.get(PREF_LAST_DIR, null);
        if (lastDir != null) {
            fileChooser.setCurrentDirectory(new File(lastDir));
        }      
 
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                prefs.put(PREF_LAST_DIR, file.getParent());
                itemManager.loadFromFile(file);
                refreshItemTable(itemManager.getItems());
                JOptionPane.showMessageDialog(this, 
                    "Loaded " + itemManager.getItems().size() + " items successfully!");
                updateStatus("Loaded " + itemManager.getItems().size() + " items");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading XML: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void saveXml() {
        if (itemManager.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items to save!");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML files", "xml"));
        // Recuperar o último diretório usado
        String lastDir = prefs.get(PREF_LAST_DIR, null);
        if (lastDir != null) {
            fileChooser.setCurrentDirectory(new File(lastDir));
        }
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".xml")) {
                    file = new File(file.getAbsolutePath() + ".xml");
                }
                prefs.put(PREF_LAST_DIR, file.getParent());
                itemManager.saveToFile(file);
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
    
    private void refreshItemTable(List<Item> items) {
        tableModel.setRowCount(0);
        for (Item item : items) {
            tableModel.addRow(new Object[]{
                item.getItemId(),
                item.getName(),
                item.getType()
            });
        }
    }
    
    private void filterItems() {
        String search = searchField.getText();
        if (search.isEmpty()) {
            refreshItemTable(itemManager.getItems());
        } else {
            refreshItemTable(itemManager.searchItems(search));
        }
    }
    
    private void loadSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow < 0) return;
        
        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        currentItem = itemManager.findItemById(itemId);
        
        if (currentItem == null) return;
        
        // Load basic attributes
        txtItemId.setText(String.valueOf(currentItem.getItemId()));
        txtName.setText(currentItem.getName());
        txtType.setText(currentItem.getType() != null ? currentItem.getType() : "");
        
        // Load sets
        setsTableModel.setRowCount(0);
        if (currentItem.getSets() != null) {
            for (ItemSet set : currentItem.getSets()) {
                setsTableModel.addRow(new Object[]{set.getName(), set.getVal()});
            }
        }
        
        // Load stats
        statsTableModel.setRowCount(0);
        if (currentItem.getStats() != null && currentItem.getStats().getStats() != null) {
            for (ItemStat stat : currentItem.getStats().getStats()) {
                statsTableModel.addRow(new Object[]{stat.getType(), stat.getValue()});
            }
        }
        
        // Load skills
        skillsTableModel.setRowCount(0);
        if (currentItem.getSkills() != null && currentItem.getSkills().getSkills() != null) {
            for (ItemSkill skill : currentItem.getSkills().getSkills()) {
                skillsTableModel.addRow(new Object[]{skill.getId(), skill.getLevel()});
            }
        }
    }
    
    private void saveItemChanges() {
        if (currentItem == null) {
            JOptionPane.showMessageDialog(this, "No item selected!");
            return;
        }
        
        // Save basic attributes
        currentItem.setName(txtName.getText());
        currentItem.setType(emptyToNull(txtType.getText()));
        
        // Salvar sets
        List<ItemSet> newSets = new ArrayList<>();
        for (int i = 0; i < setsTableModel.getRowCount(); i++) {
            String name = (String) setsTableModel.getValueAt(i, 0);
            String value = (String) setsTableModel.getValueAt(i, 1);
            if (name != null && !name.trim().isEmpty()) {
                ItemSet set = new ItemSet();
                set.setName(name);
                set.setVal(value != null ? value : "");
                newSets.add(set);
            }
        }
        currentItem.setSets(newSets);
        
        // Save stats
        ItemStats stats = new ItemStats();
        List<ItemStat> newStats = new ArrayList<>();
        for (int i = 0; i < statsTableModel.getRowCount(); i++) {
            String type = (String) statsTableModel.getValueAt(i, 0);
            String value = (String) statsTableModel.getValueAt(i, 1);
            if (type != null && !type.trim().isEmpty()) {
                ItemStat stat = new ItemStat();
                stat.setType(type);
                stat.setValue(value != null ? value : "");
                newStats.add(stat);
            }
        }
        stats.setStats(newStats);
        currentItem.setStats(stats);
        
        // Save skills
        ItemSkills skills = new ItemSkills();
        List<ItemSkill> newSkills = new ArrayList<>();
        for (int i = 0; i < skillsTableModel.getRowCount(); i++) {
            try {
                int id = Integer.parseInt(skillsTableModel.getValueAt(i, 0).toString());
                int level = Integer.parseInt(skillsTableModel.getValueAt(i, 1).toString());
                ItemSkill skill = new ItemSkill();
                skill.setId(id);
                skill.setLevel(level);
                newSkills.add(skill);
            } catch (Exception e) {
          }
        }
        skills.setSkills(newSkills);
        currentItem.setSkills(skills);
        
        JOptionPane.showMessageDialog(this, "Item updated successfully!");
        refreshItemTable(itemManager.getItems());
        updateStatus("Item " + currentItem.getItemId() + " updated");
    }
    
    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
    
    private void newItem() {
        int newId = itemManager.getNextItemId();
        Item item = new Item();
        item.setItemId(newId);
        item.setName("New Item");
        item.setType("Weapon");
        
        itemManager.addItem(item);
        refreshItemTable(itemManager.getItems());
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 0) == newId) {
                itemTable.setRowSelectionInterval(i, i);
                break;
            }
        }
        
        updateStatus("New item created with ID: " + newId);
    }
    
    private void cloneItem() {
        if (currentItem == null) {
            JOptionPane.showMessageDialog(this, "Select an item to clone!");
            return;
        }
        
        Item clone = new Item();
        clone.setItemId(itemManager.getNextItemId());
        clone.setName(currentItem.getName() + " (Clone)");
        clone.setType(currentItem.getType());
        
        itemManager.addItem(clone);
        refreshItemTable(itemManager.getItems());
        updateStatus("Item cloned with ID: " + clone.getItemId());
    }
    
    private void deleteItem() {
        if (currentItem == null) {
            JOptionPane.showMessageDialog(this, "Select an item to delete!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete item: " + currentItem.getName() + " (ID: " + currentItem.getItemId() + ")?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            itemManager.removeItem(currentItem);
            refreshItemTable(itemManager.getItems());
            currentItem = null;
            clearEditor();
            updateStatus("Item deleted");
        }
    }
    
    private void addSet() {
        String name = JOptionPane.showInputDialog(this, "Enter property name:");
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
    
    private void addStat() {
        String type = JOptionPane.showInputDialog(this, "Enter stat type (e.g., pAtk, mAtk):");
        if (type != null && !type.trim().isEmpty()) {
            statsTableModel.addRow(new Object[]{type, ""});
        }
    }
    
    private void removeStat() {
        int selectedRow = statsTable.getSelectedRow();
        if (selectedRow >= 0) {
            statsTableModel.removeRow(selectedRow);
        }
    }
    
    private void editStat() {
        int selectedRow = statsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String type = (String) statsTableModel.getValueAt(selectedRow, 0);
            String value = (String) statsTableModel.getValueAt(selectedRow, 1);
            String newValue = JOptionPane.showInputDialog(this, "Edit value for " + type + ":", value);
            if (newValue != null) {
                statsTableModel.setValueAt(newValue, selectedRow, 1);
            }
        }
    }
    
    private void addSkill() {
        try {
            String idStr = JOptionPane.showInputDialog(this, "Enter skill ID:");
            if (idStr == null || idStr.trim().isEmpty()) return;
            
            String levelStr = JOptionPane.showInputDialog(this, "Enter skill level:");
            if (levelStr == null || levelStr.trim().isEmpty()) return;
            
            int id = Integer.parseInt(idStr);
            int level = Integer.parseInt(levelStr);
            
            skillsTableModel.addRow(new Object[]{id, level});
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format!");
        }
    }
    
    private void removeSkill() {
        int selectedRow = skillsTable.getSelectedRow();
        if (selectedRow >= 0) {
            skillsTableModel.removeRow(selectedRow);
        }
    }
    
    private void editSkill() {
        int selectedRow = skillsTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int id = (int) skillsTableModel.getValueAt(selectedRow, 0);
                int level = (int) skillsTableModel.getValueAt(selectedRow, 1);
                
                String idStr = JOptionPane.showInputDialog(this, "Edit skill ID:", id);
                if (idStr == null) return;
                
                String levelStr = JOptionPane.showInputDialog(this, "Edit skill level:", level);
                if (levelStr == null) return;
                
                skillsTableModel.setValueAt(Integer.parseInt(idStr), selectedRow, 0);
                skillsTableModel.setValueAt(Integer.parseInt(levelStr), selectedRow, 1);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format!");
            }
        }
    }
    
    private void clearEditor() {
        txtItemId.setText("");
        txtName.setText("");
        txtType.setText("");
        setsTableModel.setRowCount(0);
        statsTableModel.setRowCount(0);
        skillsTableModel.setRowCount(0);
    }
    
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText("✅ " + message);
        }
    }
}