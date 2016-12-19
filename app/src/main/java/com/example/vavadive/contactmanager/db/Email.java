package com.example.vavadive.contactmanager.db;

import com.example.vavadive.contactmanager.common.EmailType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

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

    public Long getId() {
        return _id;
    }

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

    @DatabaseField
    private Long lastModified;


    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Email) {
            Email email = (Email) o;

            if((getId().equals(email.getId())) &&
                    getLastModified().equals(email.getLastModified())) {
                return true;
            }
        }
        return false;
    }
}
