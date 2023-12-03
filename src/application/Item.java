//Students: Yousef Elsonbaty (A00138), Mariam Hussein (A00039) & Amr Osman (A00112)

package application;

import java.time.LocalDate;

import javafx.beans.property.SimpleStringProperty;

public class Item {
    private SimpleStringProperty itemName;
    private LocalDate itemDate;
    private SimpleStringProperty itemLocation;
    private SimpleStringProperty itemStatus;
    private SimpleStringProperty itemDescription;

    //Components to be filled on the system for the missing or found item
    public Item(String itemName1, LocalDate itemDate1, String itemLocation1, String itemStatus1, String itemDescription1) {
        this.itemName = new SimpleStringProperty(itemName1);
        this.itemDate = itemDate1;
        this.itemLocation = new SimpleStringProperty(itemLocation1);
        this.itemStatus = new SimpleStringProperty(itemStatus1);
        this.itemDescription = new SimpleStringProperty(itemDescription1);
    }
    
    //Getters & Setters for each component
    
    public String getItemName() {
        return itemName.get();
    }

    public void setItemName(String itemName1) {
    	itemName.set(itemName1);
    }

    public LocalDate getItemDate() {
        return itemDate;
    }

    public void setItemDate(LocalDate itemDate1) {
        this.itemDate = itemDate1;
    }

    public String getItemLocation() {
        return itemLocation.get();
    }

    public void setItemLocation(String itemLocation1) {
        itemLocation.set(itemLocation1);;
    }

    public String getItemStatus() {
        return itemStatus.get();
    }

    public void setItemStatus(String itemStatus1) {
        itemStatus.set(itemStatus1);;
    }

    public String getItemDescription() {
        return itemDescription.get();
    }

    public void setItemDescription(String itemDescription1) {
        itemDescription.set(itemDescription1);;
    }
}
