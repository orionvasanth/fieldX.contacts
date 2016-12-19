package com.example.vavadive.contactmanager.db;

import com.example.vavadive.contactmanager.common.AddressType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by vavadive on 7/9/2016.
 */
@DatabaseTable(tableName = "address")
public class Address {

    @DatabaseField(generatedId = true)
    private Long _id;

    @DatabaseField
    private AddressType type;

    @DatabaseField
    private String address;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Contact contact;

    public Long getId() {
        return _id;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        if(o instanceof Address) {
            Address address = (Address) o;

            if((getId().equals(address.getId())) &&
                    getLastModified().equals(address.getLastModified())) {
                return true;
            }
        }
        return false;
    }
}
