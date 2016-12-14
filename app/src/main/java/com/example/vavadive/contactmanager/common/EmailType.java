package com.example.vavadive.contactmanager.common;

import com.example.vavadive.contactmanager.db.Email;

/**
 * Created by vavadive on 7/9/2016.
 */
public enum EmailType {
    Home("Home"), Work("Work"), Business("Business");

    private String displayedName;

    private EmailType(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getDisplayedName() {
        return displayedName;
    }
}
