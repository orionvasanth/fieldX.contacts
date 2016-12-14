package com.example.vavadive.contactmanager.common;

/**
 * Created by vavadive on 7/28/2016.
 */
public enum AddFieldMenuItem {
    Phone("Phone"), Email("Email");

    private String displayedName;

    private AddFieldMenuItem(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getDisplayedName() {
        return displayedName;
    }
}
