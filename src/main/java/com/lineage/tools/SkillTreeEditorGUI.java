package com.lineage.tools;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class SkillTreeEditorGUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private SkillTreeManager treeManager;
    private JComboBox<String> classSelector;
    private JTable skillTable;
    private DefaultTableModel tableModel;
    private SkillTreeClass currentClass;
    private SkillTreeEntry currentSkill;
    private ResourceBundle messages;
    
    // Componentes do editor
    private JTextField txtSkillId, txtSkillName, txtSkillLevel;
    private JTextField txtGetLevel, txtLevelUpSp;
    private JCheckBox chkLearnedByNpc;
    private JLabel lblClassId, lblParentClassId;
    
    public SkillTreeEditorGUI() {
        this(Locale.ENGLISH);
    }
    
    public SkillTreeEditorGUI(Locale locale) {
        messages = ResourceBundle.getBundle("Messages", locale);
        
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        treeManager = new SkillTreeManager();
        initComponents();
    }
    
    private String getMsg(String key) {
        try {
            return messages.getString(key);
        } catch (Exception e) {
            return key;
        }
    }
    
    private void initComponents() {
        setTitle(getMsg("tree.title"));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu(getMsg("menu.file"));
        
        JMenuItem openItem = new JMenuItem(getMsg("menu.file.open"));
        openItem.addActionListener(e -> loadXml());
        fileMenu.add(openItem);
        
        JMenuItem saveItem = new JMenuItem(getMsg("menu.file.save"));
        saveItem.addActionListener(e -> saveXml());
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(getMsg("menu.file.close"))).addActionListener(e -> dispose());
        
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        // Painel principal
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(500);
        
        // ===== PAINEL ESQUERDO =====
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder(getMsg("tree.panel.skills")));
        
        // Painel de seleção de classe
        JPanel classPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        classPanel.add(new JLabel(getMsg("tree.label.class") + ": "));
        classSelector = new JComboBox<>();
        classSelector.setPreferredSize(new Dimension(200, 25));
        classSelector.addActionListener(e -> loadSelectedClass());
        classPanel.add(classSelector);
        
        JButton btnRefresh = new JButton(getMsg("tree.button.refresh"));
        btnRefresh.addActionListener(e -> loadSelectedClass());
        classPanel.add(btnRefresh);
        
        leftPanel.add(classPanel, BorderLayout.NORTH);
        
        // Tabela de skills
        String[] columns = {
            getMsg("tree.table.skillName"),
            getMsg("tree.table.id"),
            getMsg("tree.table.level"),
            getMsg("tree.table.getLevel"),
            getMsg("tree.table.sp"),
            getMsg("tree.table.npc")
        };
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
        JButton btnAdd = new JButton(getMsg("tree.button.add"));
        btnAdd.addActionListener(e -> addSkill());
        
        JButton btnClone = new JButton(getMsg("tree.button.clone"));
        btnClone.addActionListener(e -> cloneSkill());
        
        JButton btnDelete = new JButton(getMsg("tree.button.delete"));
        btnDelete.addActionListener(e -> deleteSkill());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClone);
        buttonPanel.add(btnDelete);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainSplit.setLeftComponent(leftPanel);
        
        // ===== PAINEL DIREITO =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder(getMsg("tree.panel.details")));
        
        // Painel de informações da classe
        JPanel classInfoPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        classInfoPanel.setBorder(BorderFactory.createTitledBorder(getMsg("tree.panel.classInfo")));
        classInfoPanel.add(new JLabel(getMsg("tree.label.classId") + ":"));
        lblClassId = new JLabel("-");
        classInfoPanel.add(lblClassId);
        classInfoPanel.add(new JLabel(getMsg("tree.label.parentClassId") + ":"));
        lblParentClassId = new JLabel("-");
        classInfoPanel.add(lblParentClassId);
        
        // Painel de edição da skill
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder(getMsg("tree.panel.editor")));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel(getMsg("tree.label.skillId") + ":"), gbc);
        gbc.gridx = 1;
        txtSkillId = new JTextField(15);
        editPanel.add(txtSkillId, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel(getMsg("tree.label.skillName") + ":"), gbc);
        gbc.gridx = 1;
        txtSkillName = new JTextField(15);
        editPanel.add(txtSkillName, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel(getMsg("tree.label.skillLevel") + ":"), gbc);
        gbc.gridx = 1;
        txtSkillLevel = new JTextField(15);
        editPanel.add(txtSkillLevel, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel(getMsg("tree.label.getLevel") + ":"), gbc);
        gbc.gridx = 1;
        txtGetLevel = new JTextField(15);
        editPanel.add(txtGetLevel, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel(getMsg("tree.label.sp") + ":"), gbc);
        gbc.gridx = 1;
        txtLevelUpSp = new JTextField(15);
        editPanel.add(txtLevelUpSp, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel(getMsg("tree.label.npc") + ":"), gbc);
        gbc.gridx = 1;
        chkLearnedByNpc = new JCheckBox();
        editPanel.add(chkLearnedByNpc, gbc);
        row++;
        
        // Botão Save
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JButton btnSave = new JButton(getMsg("tree.button.save"));
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
        statusBar.add(new JLabel(getMsg("tree.status.ready")));
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
                    getMsg("tree.success.loaded") + " " + treeManager.getClassTrees().size());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    getMsg("tree.error.load") + " " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void saveXml() {
        if (treeManager.getClassTrees().isEmpty()) {
            JOptionPane.showMessageDialog(this, getMsg("tree.error.noData"));
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
                JOptionPane.showMessageDialog(this, getMsg("tree.success.saved"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    getMsg("tree.error.save") + " " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateClassSelector() {
        classSelector.removeAllItems();
        for (SkillTreeClass treeClass : treeManager.getClassTrees()) {
            classSelector.addItem(getMsg("tree.class") + " " + treeClass.getClassId() + 
                " (" + treeClass.getType() + ")");
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
        
        lblClassId.setText(String.valueOf(currentClass.getClassId()));
        lblParentClassId.setText(String.valueOf(currentClass.getParentClassId()));
        
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
                skill.isLearnedByNpc() ? getMsg("tree.yes") : getMsg("tree.no")
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
            JOptionPane.showMessageDialog(this, getMsg("tree.error.noSkill"));
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
            JOptionPane.showMessageDialog(this, getMsg("tree.success.updated"));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, getMsg("tree.error.invalidNumber"));
        }
    }
    
    private void addSkill() {
        if (currentClass == null) return;
        
        SkillTreeEntry newSkill = new SkillTreeEntry();
        newSkill.setSkillName(getMsg("tree.newSkill"));
        newSkill.setSkillId(0);
        newSkill.setSkillLevel(1);
        newSkill.setGetLevel(1);
        newSkill.setLevelUpSp(0);
        newSkill.setLearnedByNpc(true);
        
        currentClass.addSkill(newSkill);
        refreshSkillTable(currentClass.getSkills());
        
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
            getMsg("tree.confirm.delete"),
            getMsg("tree.confirm.title"),
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
}