package com.example.vavadive.contactmanager.common;

/**
 * Created by vavadive on 7/9/2016.
 */
public enum PhoneType {
    Mobile("Mobile"), Home("Home"), Work("Work");

    private String displayedName;

    private PhoneType(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getDisplayedName() {
        return displayedName;
    }
}
