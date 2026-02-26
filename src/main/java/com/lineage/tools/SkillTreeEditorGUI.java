package com.lineage.tools;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class SkillTreeEditorGUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private SkillTreeManager treeManager;
    private JComboBox<String> classSelector;
    private JTable skillTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private SkillTreeClass currentClass;
    private SkillTreeEntry currentSkill;
    
    // Componentes do editor
    private JTextField txtSkillId, txtSkillName, txtSkillLevel;
    private JTextField txtGetLevel, txtLevelUpSp;
    private JCheckBox chkLearnedByNpc;
    private JLabel lblClassId, lblParentClassId;
    
    public SkillTreeEditorGUI() {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        treeManager = new SkillTreeManager();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Lineage Skill Tree Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem openItem = new JMenuItem("Open Skill Tree");
        openItem.addActionListener(e -> loadXml());
        
        JMenuItem saveItem = new JMenuItem("Save Skill Tree");
        saveItem.addActionListener(e -> saveXml());
        
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem("Close")).addActionListener(e -> dispose());
        
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        // Painel principal
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(500);
        
        // ===== PAINEL ESQUERDO =====
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Skill Tree"));
        
        // Painel de seleção de classe
        JPanel classPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        classPanel.add(new JLabel("Class: "));
        classSelector = new JComboBox<>();
        classSelector.setPreferredSize(new Dimension(200, 25));
        classSelector.addActionListener(e -> loadSelectedClass());
        classPanel.add(classSelector);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadSelectedClass());
        classPanel.add(btnRefresh);
        
        leftPanel.add(classPanel, BorderLayout.NORTH);
        
        // Tabela de skills
        String[] columns = {"Skill Name", "ID", "Level", "Get Level", "SP", "NPC"};
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
        
        JScrollPane scrollPane = new JScrollPane(skillTable);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Botões de ação
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Skill");
        btnAdd.addActionListener(e -> addSkill());
        
        JButton btnClone = new JButton("Clone");
        btnClone.addActionListener(e -> cloneSkill());
        
        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(e -> deleteSkill());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClone);
        buttonPanel.add(btnDelete);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainSplit.setLeftComponent(leftPanel);
        
        // ===== PAINEL DIREITO =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Skill Details"));
        
        // Painel de informações da classe
        JPanel classInfoPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        classInfoPanel.setBorder(BorderFactory.createTitledBorder("Class Info"));
        classInfoPanel.add(new JLabel("Class ID:"));
        lblClassId = new JLabel("-");
        classInfoPanel.add(lblClassId);
        classInfoPanel.add(new JLabel("Parent Class ID:"));
        lblParentClassId = new JLabel("-");
        classInfoPanel.add(lblParentClassId);
        
        // Painel de edição da skill
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder("Skill Editor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Skill ID:"), gbc);
        gbc.gridx = 1;
        txtSkillId = new JTextField(15);
        editPanel.add(txtSkillId, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Skill Name:"), gbc);
        gbc.gridx = 1;
        txtSkillName = new JTextField(15);
        editPanel.add(txtSkillName, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Skill Level:"), gbc);
        gbc.gridx = 1;
        txtSkillLevel = new JTextField(15);
        editPanel.add(txtSkillLevel, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Get Level:"), gbc);
        gbc.gridx = 1;
        txtGetLevel = new JTextField(15);
        editPanel.add(txtGetLevel, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Level Up SP:"), gbc);
        gbc.gridx = 1;
        txtLevelUpSp = new JTextField(15);
        editPanel.add(txtLevelUpSp, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Learned by NPC:"), gbc);
        gbc.gridx = 1;
        chkLearnedByNpc = new JCheckBox();
        editPanel.add(chkLearnedByNpc, gbc);
        row++;
        
        // Botão Save
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JButton btnSave = new JButton("Save Changes");
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(200, 35));
        btnSave.addActionListener(e -> saveSkillChanges());
        editPanel.add(btnSave, gbc);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(classInfoPanel, BorderLayout.NORTH);
        centerPanel.add(editPanel, BorderLayout.CENTER);
        
        rightPanel.add(centerPanel, BorderLayout.CENTER);
        
        mainSplit.setRightComponent(rightPanel);
        
        add(mainSplit, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(new JLabel("Ready"));
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void loadXml() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML files", "xml"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                treeManager.loadFromFile(file);
                updateClassSelector();
                JOptionPane.showMessageDialog(this, 
                    "Loaded " + treeManager.getClassTrees().size() + " class trees!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading XML: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void saveXml() {
        if (treeManager.getClassTrees().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to save!");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".xml")) {
                    file = new File(file.getAbsolutePath() + ".xml");
                }
                treeManager.saveToFile(file);
                JOptionPane.showMessageDialog(this, "XML saved successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving XML: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateClassSelector() {
        classSelector.removeAllItems();
        for (SkillTreeClass treeClass : treeManager.getClassTrees()) {
            classSelector.addItem("Class " + treeClass.getClassId() + " (" + treeClass.getType() + ")");
        }
        if (classSelector.getItemCount() > 0) {
            classSelector.setSelectedIndex(0);
            loadSelectedClass();
        }
    }
    
    private void loadSelectedClass() {
        int index = classSelector.getSelectedIndex();
        if (index < 0 || index >= treeManager.getClassTrees().size()) return;
        
        currentClass = treeManager.getClassTrees().get(index);
        
        // Atualizar info da classe
        lblClassId.setText(String.valueOf(currentClass.getClassId()));
        lblParentClassId.setText(String.valueOf(currentClass.getParentClassId()));
        
        // Atualizar tabela
        refreshSkillTable(currentClass.getSkills());
    }
    
    private void refreshSkillTable(List<SkillTreeEntry> skills) {
        tableModel.setRowCount(0);
        for (SkillTreeEntry skill : skills) {
            tableModel.addRow(new Object[]{
                skill.getSkillName(),
                skill.getSkillId(),
                skill.getSkillLevel(),
                skill.getGetLevel(),
                skill.getLevelUpSp(),
                skill.isLearnedByNpc() ? "Yes" : "No"
            });
        }
    }
    
    private void loadSelectedSkill() {
        int selectedRow = skillTable.getSelectedRow();
        if (selectedRow < 0 || currentClass == null) return;
        
        int index = skillTable.convertRowIndexToModel(selectedRow);
        currentSkill = currentClass.getSkills().get(index);
        
        txtSkillId.setText(String.valueOf(currentSkill.getSkillId()));
        txtSkillName.setText(currentSkill.getSkillName());
        txtSkillLevel.setText(String.valueOf(currentSkill.getSkillLevel()));
        txtGetLevel.setText(String.valueOf(currentSkill.getGetLevel()));
        txtLevelUpSp.setText(String.valueOf(currentSkill.getLevelUpSp()));
        chkLearnedByNpc.setSelected(currentSkill.isLearnedByNpc());
    }
    
    private void saveSkillChanges() {
        if (currentSkill == null || currentClass == null) {
            JOptionPane.showMessageDialog(this, "No skill selected!");
            return;
        }
        
        try {
            currentSkill.setSkillId(Integer.parseInt(txtSkillId.getText()));
            currentSkill.setSkillName(txtSkillName.getText());
            currentSkill.setSkillLevel(Integer.parseInt(txtSkillLevel.getText()));
            currentSkill.setGetLevel(Integer.parseInt(txtGetLevel.getText()));
            currentSkill.setLevelUpSp(Integer.parseInt(txtLevelUpSp.getText()));
            currentSkill.setLearnedByNpc(chkLearnedByNpc.isSelected());
            
            refreshSkillTable(currentClass.getSkills());
            JOptionPane.showMessageDialog(this, "Skill updated!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format!");
        }
    }
    
    private void addSkill() {
        if (currentClass == null) return;
        
        SkillTreeEntry newSkill = new SkillTreeEntry();
        newSkill.setSkillName("New Skill");
        newSkill.setSkillId(0);
        newSkill.setSkillLevel(1);
        newSkill.setGetLevel(1);
        newSkill.setLevelUpSp(0);
        newSkill.setLearnedByNpc(true);
        
        currentClass.addSkill(newSkill);
        refreshSkillTable(currentClass.getSkills());
        
        // Selecionar a nova skill
        int lastRow = tableModel.getRowCount() - 1;
        if (lastRow >= 0) {
            skillTable.setRowSelectionInterval(lastRow, lastRow);
        }
    }
    
    private void cloneSkill() {
        if (currentSkill == null || currentClass == null) return;
        
        SkillTreeEntry clone = new SkillTreeEntry();
        clone.setSkillId(currentSkill.getSkillId());
        clone.setSkillName(currentSkill.getSkillName() + " (Clone)");
        clone.setSkillLevel(currentSkill.getSkillLevel());
        clone.setGetLevel(currentSkill.getGetLevel());
        clone.setLevelUpSp(currentSkill.getLevelUpSp());
        clone.setLearnedByNpc(currentSkill.isLearnedByNpc());
        
        currentClass.addSkill(clone);
        refreshSkillTable(currentClass.getSkills());
    }
    
    private void deleteSkill() {
        if (currentSkill == null || currentClass == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this skill?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            currentClass.removeSkill(currentSkill);
            refreshSkillTable(currentClass.getSkills());
            clearEditor();
        }
    }
    
    private void clearEditor() {
        txtSkillId.setText("");
        txtSkillName.setText("");
        txtSkillLevel.setText("");
        txtGetLevel.setText("");
        txtLevelUpSp.setText("");
        chkLearnedByNpc.setSelected(false);
        currentSkill = null;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new SkillTreeEditorGUI().setVisible(true);
        });
    }
}