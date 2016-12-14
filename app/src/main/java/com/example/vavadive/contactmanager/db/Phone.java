package com.example.vavadive.contactmanager.db;

import com.example.vavadive.contactmanager.common.PhoneType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by vavadive on 7/9/2016.
 */
@DatabaseTable(tableName = "phone_details")
public class Phone {

    @DatabaseField(generatedId = true)
    private Long _id;

    @DatabaseField
    private PhoneType type;

    @DatabaseField
    private String phone;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Contact contact;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public PhoneType getType() {
        return type;
    }

    public void setType(PhoneType type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
