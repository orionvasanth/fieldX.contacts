package com.example.vavadive.contactmanager.db;

import com.example.vavadive.contactmanager.common.IMType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by vavadive on 12/21/2016.
 */
@DatabaseTable(tableName = "im_details")
public class IM {
    public Long getId() {
        return _id;
    }

    @DatabaseField(generatedId = true)
    private Long _id;

    public IMType getType() {
        return type;
    }

    public void setType(IMType type) {
        this.type = type;
    }

    @DatabaseField
    private IMType type;

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    @DatabaseField
    private String im;

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
        if(o instanceof IM) {
            IM im = (IM) o;

            if((getId().equals(im.getId())) &&
                    getLastModified().equals(im.getLastModified())) {
                return true;
            }
        }
        return false;
    }
}
