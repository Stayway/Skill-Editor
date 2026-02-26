# Lineage Skill Editor 

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Java](https://img.shields.io/badge/Java-25-orange)
![License](https://img.shields.io/badge/license-MIT-green)

A professional graphical tool for editing Lineage 2 server skills. Developed in Java, it allows you to load, view, edit, create, and export skills intuitively.

## How compile?

### **Pré-requirements**
- Java 25 ou superior
- Maven 3.9 ou superior

### **Compile**

- **1. Right click pom.xml
- **2. Runs As --- Maven Build
- **Configure the Goals and click in Run. In Fileld Goals digite: clean compile assembly:single

## Features

- **Load XML** - Opens skill files in standard L2J/aCis format
- **Real-time search** - Filters skills by ID or name as you type
- **Full editing** - Modifies all skill attributes
- **Create new skills** - Adds skills with default values
- **Clone skills** - Duplicates existing skills for easier creation
- **Remove skills** - Deletes unwanted skills
- **Data validation** - Checks if values are valid
- **Save XML** - Generates server-compatible XML files
- **Export CSV** - Exports data to spreadsheets
- **Modern interface** - FlatLaf theme with intuitive layout

## Supported XML Structure

```xml
<skill>
    <skill_id>101</skill_id>
    <level>1</level>
    <name>Skill Name</name>
    <operate_type>OP_PASSIVE</operate_type>
    <magic_level>1</magic_level>
    <mp_consume>10</mp_consume>
    <hp_consume>0</hp_consume>
    <item_consume>0</item_consume>
    <cast_range>400</cast_range>
    <effect_range>900</effect_range>
    <skill_time>10</skill_time>
    <reuse_delay>5000</reuse_delay>
    <attribute>0</attribute>
    <target>target_self</target>
    <skill_type>BUFF</skill_type>
    <magic_critical>false</magic_critical>
</skill>

