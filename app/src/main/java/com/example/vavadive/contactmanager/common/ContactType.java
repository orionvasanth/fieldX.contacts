package com.example.vavadive.contactmanager.common;

/**
 * Created by vavadive on 7/9/2016.
 */
public enum ContactType {

    Customer("Customer"), Colleague("Colleague") , Partner("Partner");

    private String displayedName;

    private ContactType(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getDisplayedName() {
        return displayedName;
    }
}
