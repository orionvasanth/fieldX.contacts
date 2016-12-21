package com.example.vavadive.contactmanager.common;

/**
 * Created by vavadive on 12/21/2016.
 */

public enum IMType {
    AIM("AIM"), Yahoo("Yahoo") , Skype("Skype"), Hangouts("Hangouts");

    private String displayedName;

    private IMType(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getDisplayedName() {
        return displayedName;
    }
}
