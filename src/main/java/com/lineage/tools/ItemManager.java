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

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemManager {
    
    @XmlElement(name = "item")
    private List<Item> items = new ArrayList<>();
    
    public ItemManager() {}
    
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
    
    public void addItem(Item item) {
        items.add(item);
    }
    
    public void removeItem(Item item) {
        items.remove(item);
    }
    
    public Item findItemById(int itemId) {
        return items.stream()
                .filter(i -> i.getItemId() == itemId)
                .findFirst()
                .orElse(null);
    }
    
    public List<Item> searchItems(String searchTerm) {
        String term = searchTerm.toLowerCase();
        return items.stream()
                .filter(i -> String.valueOf(i.getItemId()).contains(term) ||
                            i.getName().toLowerCase().contains(term) ||
                            (i.getType() != null && i.getType().toLowerCase().contains(term)))
                .collect(Collectors.toList());
    }
    
    public int getNextItemId() {
        return items.stream()
                .mapToInt(Item::getItemId)
                .max()
                .orElse(0) + 1;
    }
    
    public void loadFromFile(File file) throws Exception {
        JAXBContext context = JAXBContext.newInstance(ItemManager.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        ItemManager loaded = (ItemManager) unmarshaller.unmarshal(file);
        this.items = loaded.getItems();
        System.out.println("Loaded " + items.size() + " items!");
    }
    
    public void saveToFile(File file) throws Exception {
        JAXBContext context = JAXBContext.newInstance(ItemManager.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(this, file);
    }
}