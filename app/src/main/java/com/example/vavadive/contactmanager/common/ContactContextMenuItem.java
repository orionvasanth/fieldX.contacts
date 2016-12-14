package com.example.vavadive.contactmanager.common;

/**
 * Created by vavadive on 7/17/2016.
 */
public enum ContactContextMenuItem {
    Delete("Delete"), Edit("Edit");

    private String displayedName;

    private ContactContextMenuItem(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getDisplayedName() {
        return displayedName;
    }
}
