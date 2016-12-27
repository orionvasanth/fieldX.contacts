package com.example.vavadive.contactmanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.vavadive.contactmanager.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by vavadive on 7/9/2016.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "FieldX.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Contact, Long> contactDao;
    private Dao<Phone, Long> phoneDao;
    private Dao<Email, Long> emailDao;
    private Dao<Address, Long> addressDao;
    private Dao<IM, Long> imDao;
    private Dao<Website, Long> websiteDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Contact.class);
            TableUtils.createTable(connectionSource, Address.class);
            TableUtils.createTable(connectionSource, Email.class);
            TableUtils.createTable(connectionSource, Phone.class);
            TableUtils.createTable(connectionSource, IM.class);
            TableUtils.createTable(connectionSource, Website.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Contact.class, false);
            TableUtils.dropTable(connectionSource, Address.class, false);
            TableUtils.dropTable(connectionSource, Email.class, false);
            TableUtils.dropTable(connectionSource, Phone.class, false);
            TableUtils.dropTable(connectionSource, IM.class, false);
            TableUtils.dropTable(connectionSource, Website.class, false);

            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Contact, Long> getContactDao() throws SQLException {
        if(contactDao == null) {
            contactDao = getDao(Contact.class);
        }
        return contactDao;
    }

    public Dao<Phone, Long> getPhoneDao() throws SQLException {
        if(phoneDao == null) {
            phoneDao = getDao(Phone.class);
        }
        return phoneDao;
    }

    public Dao<Email, Long> getEmailDao() throws SQLException {
        if(emailDao == null) {
            emailDao = getDao(Email.class);
        }
        return emailDao;
    }

    public Dao<Address, Long> getAddressDao() throws SQLException {
        if(addressDao == null) {
            addressDao = getDao(Address.class);
        }
        return addressDao;
    }

    public Dao<IM, Long> getImDao() throws SQLException {
        if(imDao == null) {
            imDao = getDao(IM.class);
        }
        return imDao;
    }

    public Dao<Website, Long> getWebsiteDao() throws SQLException {
        if(websiteDao == null) {
            websiteDao = getDao(Website.class);
        }
        return websiteDao;
    }
}
