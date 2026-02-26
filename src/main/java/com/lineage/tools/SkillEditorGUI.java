package com.lineage.tools;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

@SuppressWarnings("unused")
public class SkillEditorGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private SkillManager skillManager;
    private JTable skillTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JPanel editorPanel;
    private Skill currentSkill;
    
    // Componentes do editor
    private JTextField txtSkillId, txtLevel, txtName, txtMagicLevel, txtMpConsume;
    private JTextField txtHpConsume, txtItemConsume, txtCastRange, txtEffectRange;
    private JTextField txtSkillTime, txtReuseDelay, txtAttribute;
    private JComboBox<String> cbOperateType, cbTarget, cbSkillType;
    private JCheckBox chkMagicCritical;
    
    public SkillEditorGUI() {
        setTitle("Lineage Skill Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        
        skillManager = new SkillManager();
        initComponents();
    }
    
    private void initComponents() {
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem openItem = new JMenuItem("Open XML");
        openItem.addActionListener(e -> loadXml());
        
        JMenuItem saveItem = new JMenuItem("Save XML");
        saveItem.addActionListener(e -> saveXml());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu toolsMenu = new JMenu("Tools");
        
        JMenuItem validateItem = new JMenuItem("Validate Skills");
        validateItem.addActionListener(e -> validateSkills());
        
        JMenuItem exportItem = new JMenuItem("Export to CSV");
        exportItem.addActionListener(e -> exportToCsv());
        
        toolsMenu.add(validateItem);
        toolsMenu.add(exportItem);
        
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        setJMenuBar(menuBar);
        
        // Painel principal
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(500);
        
        // Painel esquerdo - Lista de Skills
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Skills List"));
        
        // Painel de busca
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterSkills(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterSkills(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterSkills(); }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Tabela de skills
        String[] columns = {"ID", "Level", "Name", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        skillTable = new JTable(tableModel);
        skillTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedSkill();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(skillTable);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnNew = new JButton("New");
        btnNew.addActionListener(e -> newSkill());
        
        JButton btnClone = new JButton("Clone");
        btnClone.addActionListener(e -> cloneSkill());
        
        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(e -> deleteSkill());
        
        buttonPanel.add(btnNew);
        buttonPanel.add(btnClone);
        buttonPanel.add(btnDelete);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        splitPane.setLeftComponent(leftPanel);
        
        // Painel direito - Editor
        editorPanel = createEditorPanel();
        splitPane.setRightComponent(editorPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createEditorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Skill Editor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        String[] operateTypes = {"OP_PASSIVE", "OP_ACTIVE", "OP_TOGGLE"};
        String[] targets = {"target_self", "target_one", "target_corpse", "target_undead", "target_party", "target_clan"};
        String[] skillTypes = {"BUFF", "DEBUFF", "HEAL", "DAMAGE", "SUMMON", "RESURRECT"};
        
        int row = 0;
        
        // Campos de texto
        txtSkillId = addField(panel, "Skill ID:", gbc, row++);
        txtLevel = addField(panel, "Level:", gbc, row++);
        txtName = addField(panel, "Name:", gbc, row++);
        
        // Comboboxes
        cbOperateType = addComboBox(panel, "Operate Type:", operateTypes, gbc, row++);
        txtMagicLevel = addField(panel, "Magic Level:", gbc, row++);
        txtMpConsume = addField(panel, "MP Consume:", gbc, row++);
        txtHpConsume = addField(panel, "HP Consume:", gbc, row++);
        txtItemConsume = addField(panel, "Item Consume:", gbc, row++);
        txtCastRange = addField(panel, "Cast Range:", gbc, row++);
        txtEffectRange = addField(panel, "Effect Range:", gbc, row++);
        txtSkillTime = addField(panel, "Skill Time:", gbc, row++);
        txtReuseDelay = addField(panel, "Reuse Delay:", gbc, row++);
        txtAttribute = addField(panel, "Attribute:", gbc, row++);
        
        cbTarget = addComboBox(panel, "Target:", targets, gbc, row++);
        cbSkillType = addComboBox(panel, "Skill Type:", skillTypes, gbc, row++);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Magic Critical:"), gbc);
        gbc.gridx = 1;
        chkMagicCritical = new JCheckBox();
        panel.add(chkMagicCritical, gbc);
        row++;
        
        // Botão Save
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JButton btnSave = new JButton("Save Changes");
        btnSave.addActionListener(e -> saveSkillChanges());
        btnSave.setPreferredSize(new Dimension(200, 40));
        panel.add(btnSave, gbc);
        
        return panel;
    }
    
    private JTextField addField(JPanel panel, String label, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        JTextField field = new JTextField(20);
        panel.add(field, gbc);
        
        return field;
    }
    
    private JComboBox<String> addComboBox(JPanel panel, String label, String[] items, 
                                          GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> comboBox = new JComboBox<>(items);
        panel.add(comboBox, gbc);
        
        return comboBox;
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
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving XML: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshSkillTable(List<Skill> skills) {
        tableModel.setRowCount(0);
        for (Skill skill : skills) {
            tableModel.addRow(new Object[]{
                skill.getSkillId(),
                skill.getLevel(),
                skill.getName(),
                skill.getSkillType()
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
        if (selectedRow >= 0) {
            int skillId = (int) tableModel.getValueAt(selectedRow, 0);
            int level = (int) tableModel.getValueAt(selectedRow, 1);
            
            currentSkill = skillManager.findSkillByIdAndLevel(skillId, level);
            if (currentSkill != null) {
                updateEditorFields();
            }
        }
    }
    
    private void updateEditorFields() {
        txtSkillId.setText(String.valueOf(currentSkill.getSkillId()));
        txtLevel.setText(String.valueOf(currentSkill.getLevel()));
        txtName.setText(currentSkill.getName());
        cbOperateType.setSelectedItem(currentSkill.getOperateType());
        txtMagicLevel.setText(String.valueOf(currentSkill.getMagicLevel()));
        txtMpConsume.setText(String.valueOf(currentSkill.getMpConsume()));
        txtHpConsume.setText(String.valueOf(currentSkill.getHpConsume()));
        txtItemConsume.setText(String.valueOf(currentSkill.getItemConsume()));
        txtCastRange.setText(String.valueOf(currentSkill.getCastRange()));
        txtEffectRange.setText(String.valueOf(currentSkill.getEffectRange()));
        txtSkillTime.setText(String.valueOf(currentSkill.getSkillTime()));
        txtReuseDelay.setText(String.valueOf(currentSkill.getReuseDelay()));
        txtAttribute.setText(String.valueOf(currentSkill.getAttribute()));
        cbTarget.setSelectedItem(currentSkill.getTarget());
        cbSkillType.setSelectedItem(currentSkill.getSkillType());
        chkMagicCritical.setSelected(currentSkill.isMagicCritical());
    }
    
    private void saveSkillChanges() {
        if (currentSkill == null) {
            JOptionPane.showMessageDialog(this, "No skill selected!");
            return;
        }
        
        try {
            currentSkill.setSkillId(Integer.parseInt(txtSkillId.getText()));
            currentSkill.setLevel(Integer.parseInt(txtLevel.getText()));
            currentSkill.setName(txtName.getText());
            currentSkill.setOperateType((String) cbOperateType.getSelectedItem());
            currentSkill.setMagicLevel(Integer.parseInt(txtMagicLevel.getText()));
            currentSkill.setMpConsume(Integer.parseInt(txtMpConsume.getText()));
            currentSkill.setHpConsume(Integer.parseInt(txtHpConsume.getText()));
            currentSkill.setItemConsume(Integer.parseInt(txtItemConsume.getText()));
            currentSkill.setCastRange(Integer.parseInt(txtCastRange.getText()));
            currentSkill.setEffectRange(Integer.parseInt(txtEffectRange.getText()));
            currentSkill.setSkillTime(Integer.parseInt(txtSkillTime.getText()));
            currentSkill.setReuseDelay(Integer.parseInt(txtReuseDelay.getText()));
            currentSkill.setAttribute(Integer.parseInt(txtAttribute.getText()));
            currentSkill.setTarget((String) cbTarget.getSelectedItem());
            currentSkill.setSkillType((String) cbSkillType.getSelectedItem());
            currentSkill.setMagicCritical(chkMagicCritical.isSelected());
            
            refreshSkillTable(skillManager.getSkills());
            JOptionPane.showMessageDialog(this, "Skill updated successfully!");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid number format!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void newSkill() {
        int newId = skillManager.getNextSkillId();
        Skill skill = new Skill(newId, 1, "New Skill");
        skillManager.addSkill(skill);
        refreshSkillTable(skillManager.getSkills());
        
        // Selecionar a nova skill
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 0) == newId) {
                skillTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }
    
    private void cloneSkill() {
        if (currentSkill == null) {
            JOptionPane.showMessageDialog(this, "No skill selected to clone!");
            return;
        }
        
        try {
            Skill clone = new Skill();
            // Copiar todos os campos
            clone.setSkillId(skillManager.getNextSkillId());
            clone.setLevel(currentSkill.getLevel());
            clone.setName(currentSkill.getName() + " (Clone)");
            clone.setOperateType(currentSkill.getOperateType());
            clone.setMagicLevel(currentSkill.getMagicLevel());
            clone.setMpConsume(currentSkill.getMpConsume());
            clone.setHpConsume(currentSkill.getHpConsume());
            clone.setItemConsume(currentSkill.getItemConsume());
            clone.setCastRange(currentSkill.getCastRange());
            clone.setEffectRange(currentSkill.getEffectRange());
            clone.setSkillTime(currentSkill.getSkillTime());
            clone.setReuseDelay(currentSkill.getReuseDelay());
            clone.setAttribute(currentSkill.getAttribute());
            clone.setTarget(currentSkill.getTarget());
            clone.setSkillType(currentSkill.getSkillType());
            clone.setMagicCritical(currentSkill.isMagicCritical());
            
            skillManager.addSkill(clone);
            refreshSkillTable(skillManager.getSkills());
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error cloning skill: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSkill() {
        if (currentSkill == null) {
            JOptionPane.showMessageDialog(this, "No skill selected!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete skill: " + currentSkill.getName() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            skillManager.removeSkill(currentSkill);
            refreshSkillTable(skillManager.getSkills());
            currentSkill = null;
            clearEditorFields();
        }
    }
    
    private void clearEditorFields() {
        txtSkillId.setText("");
        txtLevel.setText("");
        txtName.setText("");
        cbOperateType.setSelectedIndex(0);
        txtMagicLevel.setText("");
        txtMpConsume.setText("");
        txtHpConsume.setText("");
        txtItemConsume.setText("");
        txtCastRange.setText("");
        txtEffectRange.setText("");
        txtSkillTime.setText("");
        txtReuseDelay.setText("");
        txtAttribute.setText("");
        cbTarget.setSelectedIndex(0);
        cbSkillType.setSelectedIndex(0);
        chkMagicCritical.setSelected(false);
    }
    
    private void validateSkills() {
        StringBuilder report = new StringBuilder("Validation Report:\n\n");
        int errors = 0;
        
        for (Skill skill : skillManager.getSkills()) {
            if (skill.getSkillId() <= 0) {
                report.append("❌ Skill ").append(skill).append(": Invalid ID\n");
                errors++;
            }
            if (skill.getName() == null || skill.getName().trim().isEmpty()) {
                report.append("❌ Skill ID ").append(skill.getSkillId()).append(": Empty name\n");
                errors++;
            }
            if (skill.getMpConsume() < 0 || skill.getHpConsume() < 0) {
                report.append("❌ Skill ").append(skill).append(": Negative consume values\n");
                errors++;
            }
            if (skill.getReuseDelay() < 0) {
                report.append("❌ Skill ").append(skill).append(": Negative reuse delay\n");
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
                // Escrever cabeçalho
                writer.println("ID,Level,Name,Type,MP,HP,Range,Reuse");
                
                // Escrever dados
                for (Skill skill : skillManager.getSkills()) {
                    writer.printf("%d,%d,\"%s\",%s,%d,%d,%d,%d%n",
                        skill.getSkillId(),
                        skill.getLevel(),
                        skill.getName(),
                        skill.getSkillType(),
                        skill.getMpConsume(),
                        skill.getHpConsume(),
                        skill.getCastRange(),
                        skill.getReuseDelay()
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
    
    public static void main(String[] args) {
        try {
            // Tema moderno
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize theme");
        }
        
        SwingUtilities.invokeLater(() -> {
            new SkillEditorGUI().setVisible(true);
        });
    }
}