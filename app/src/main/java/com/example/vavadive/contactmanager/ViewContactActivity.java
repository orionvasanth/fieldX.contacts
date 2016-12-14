package com.example.vavadive.contactmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.example.vavadive.contactmanager.common.AddressType;
import com.example.vavadive.contactmanager.common.ContactType;
import com.example.vavadive.contactmanager.common.EmailType;
import com.example.vavadive.contactmanager.common.Mode;
import com.example.vavadive.contactmanager.common.PhoneType;
import com.example.vavadive.contactmanager.db.Address;
import com.example.vavadive.contactmanager.db.Contact;
import com.example.vavadive.contactmanager.db.DatabaseHelper;
import com.example.vavadive.contactmanager.db.Email;
import com.example.vavadive.contactmanager.db.Phone;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by vavadive on 6/22/2016.
 */
public class ViewContactActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Mode mode;
    private Long CONTACT_ID = 0l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);

        mode = Mode.values()[getIntent().getIntExtra("mode", 0)];

        if(mode.equals(Mode.VIEW)) {
            long id = getIntent().getLongExtra(getResources().getString(R.string.key), 0);
            if(id != 0) {
                CONTACT_ID = id;
                populate(id);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_contact, menu);

        MenuItem edit = menu.findItem(R.id.edit_button);
        edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent editContact = new Intent(ViewContactActivity.this, AddContactActivity.class);
                editContact.putExtra(getResources().getString(R.string.key), CONTACT_ID);
                editContact.putExtra("mode", Mode.EDIT.ordinal());
                startActivityForResult(editContact, MainActivity.REQUEST_EDIT);

                return true;
            }
        });

        return true;
    }

    private void populatePhone(Phone phone, View phoneRow) {
        Spinner phoneType = (Spinner) phoneRow.findViewById(R.id.spinner_phone_types);
        ArrayAdapter<PhoneType> phoneTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, PhoneType.values());
        phoneType.setAdapter(phoneTypeArrayAdapter);
        phoneType.setSelection(phone.getType().ordinal());
        phoneType.setEnabled(false);

        EditText phoneNo = (EditText) phoneRow.findViewById(R.id.editText_phone);
        phoneNo.setText(phone.getPhone());
        phoneNo.setInputType(InputType.TYPE_NULL);
    }

    private void populateEmail(Email email, View emailRow) {
        Spinner emailType = (Spinner) emailRow.findViewById(R.id.spinner_email_types);
        ArrayAdapter<EmailType> emailTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, EmailType.values());
        emailType.setAdapter(emailTypeArrayAdapter);
        emailType.setSelection(email.getType().ordinal());
        emailType.setEnabled(false);

        EditText emailId = (EditText) emailRow.findViewById(R.id.editText_email);
        emailId.setText(email.getEmail());
        emailId.setInputType(InputType.TYPE_NULL);
    }

    private void populatePhones(Collection<Phone> phones) {
        if(!phones.isEmpty()) {
            TableLayout phoneTable = (TableLayout) findViewById(R.id.phone_tableLayout);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.phone_table_header, phoneTable, false);
            phoneTable.addView(row);

            Object[] phoneObjects = phones.toArray();

            for (int i = 0; i < phones.size(); i++) {
                row = inflater.inflate(R.layout.phone_table_row_view, phoneTable, false);

                Phone phone = ((Phone) phoneObjects[i]);
                populatePhone(phone, row);

                phoneTable.addView(row);
            }
        }
    }

    private void populateEmails(Collection<Email> emails) {
        TableLayout emailTable = (TableLayout) findViewById(R.id.email_tableLayout);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.email_table_header, emailTable, false);
        emailTable.addView(row);

        Object[] emailObjects = emails.toArray();

        for(int i = 0; i < emails.size(); i++) {
            row = inflater.inflate(R.layout.email_table_row_view, emailTable, false);

            Email email = ((Email)emailObjects[i]);
            populateEmail(email, row);

            emailTable.addView(row);
        }
    }

    private void populateAddresses(Collection<Address> addresses) {
        if(!addresses.isEmpty()) {
            TableLayout addressTable = (TableLayout) findViewById(R.id.address_tableLayout);

            Address address = (Address) addresses.toArray()[0];
            View addressRow = addressTable.getChildAt(1);

            Spinner addressType = (Spinner) addressRow.findViewById(R.id.spinner_address_types);
            ArrayAdapter<AddressType> addressTypeArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, AddressType.values());
            addressType.setAdapter(addressTypeArrayAdapter);
            addressType.setSelection(address.getType().ordinal());
            addressType.setEnabled(false);

            EditText addressVal = (EditText) addressRow.findViewById(R.id.editText_address);
            addressVal.setText(address.getAddress());
            addressVal.setInputType(InputType.TYPE_NULL);
        }
    }

    private void populateContact(Contact contact) {
        EditText firstName = (EditText) findViewById(R.id.editText_firstName);
        firstName.setText(contact.getFirstName());
        firstName.setInputType(InputType.TYPE_NULL);

        EditText middleName = (EditText) findViewById(R.id.editText_middleName);
        middleName.setText(contact.getMiddleName());
        middleName.setInputType(InputType.TYPE_NULL);

        EditText lastName = (EditText) findViewById(R.id.editText_lastName);
        lastName.setText(contact.getLastName());
        lastName.setInputType(InputType.TYPE_NULL);

        Spinner contactType = (Spinner)findViewById(R.id.spinner_contact_types);
        ArrayAdapter<ContactType> contactTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ContactType.values());
        contactTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactType.setAdapter(contactTypeArrayAdapter);
        contactType.setSelection(contact.getContactType().ordinal());
        contactType.setEnabled(false);
    }

    private void populate(long id) {
        try {
            Contact contact = databaseHelper.getContactDao().queryForId(id);
            if(contact != null) {
                populateContact(contact);
                populatePhones(contact.getPhones());
                populateEmails(contact.getEmails());
                populateAddresses(contact.getAddresses());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
