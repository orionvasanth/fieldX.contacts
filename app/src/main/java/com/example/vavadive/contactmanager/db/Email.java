package com.example.vavadive.contactmanager.db;

import com.example.vavadive.contactmanager.common.EmailType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by vavadive on 7/9/2016.
 */
@DatabaseTable(tableName = "email")
public class Email {

    @DatabaseField(generatedId = true)
    private Long _id;

    @DatabaseField
    private EmailType type;

    @DatabaseField
    private String email;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Contact contact;

    public EmailType getType() {
        return type;
    }

    public void setType(EmailType type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
