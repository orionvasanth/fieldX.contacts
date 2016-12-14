package com.example.vavadive.contactmanager.common;

/**
 * Created by vavadive on 7/9/2016.
 */
public enum AddressType {
    Residence("Residence"), Work("Work");

    private String displayedName;
    private AddressType(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getDisplayName() {
        return displayedName;
    }
}
