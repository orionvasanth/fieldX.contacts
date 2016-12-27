package com.example.vavadive.contactmanager.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by vavadive on 12/26/2016.
 */
@DatabaseTable(tableName = "website_details")
public class Website {
    public Long getId() {
        return _id;
    }

    @DatabaseField(generatedId = true)
    private Long _id;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @DatabaseField
    private String url;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Contact contact;

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
        if(o instanceof Website) {
            Website url = (Website) o;

            if((getId().equals(url.getId())) &&
                    getLastModified().equals(url.getLastModified())) {
                return true;
            }
        }
        return false;
    }
}
