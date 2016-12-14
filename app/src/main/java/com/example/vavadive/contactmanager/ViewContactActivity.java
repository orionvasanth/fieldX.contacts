package com.example.vavadive.contactmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.example.vavadive.contactmanager.common.EmailType;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_dynamic);
        databaseHelper = new DatabaseHelper(this);

        populate();
        addListener();
    }

    private void populatePhone(Phone phone, View phoneRow) {

        Spinner phoneType = (Spinner) phoneRow.findViewById(R.id.spinner_phone_types);
        phoneType.setSelection(phone.getType().ordinal());
        phoneType.setFocusable(false);

        EditText phoneNo = (EditText) phoneRow.findViewById(R.id.editText_phone);
        phoneNo.setText(phone.getPhone());
        phoneNo.setFocusable(false);
    }

    private void populateEmail(Email email, View emailRow) {

        Spinner emailType = (Spinner) emailRow.findViewById(R.id.spinner_email_types);
        emailType.setSelection(email.getType().ordinal());

        EditText emailId = (EditText) emailRow.findViewById(R.id.editText_email);
        emailId.setText(email.getEmail());
    }

    private void populatePhones(Collection<Phone> phones) {
        TableLayout phoneTable = (TableLayout) findViewById(R.id.phone_tableLayout);

        if(phones.size() > 1) {
            Object[] phoneObjects = phones.toArray();
            View phoneRow = phoneTable.getChildAt(1);

            populatePhone((Phone) phoneObjects[0], phoneRow);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for(int i = 1; i < phones.size(); i++) {
                View row = inflater.inflate(R.layout.phone_table_row, null);
                Phone phone = ((Phone)phoneObjects[i]);

                Spinner phoneType = (Spinner) row.findViewById(R.id.spinner_phone_types);
                ArrayAdapter<PhoneType> phoneTypeArrayAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, PhoneType.values());
                phoneType.setAdapter(phoneTypeArrayAdapter);
                phoneType.setSelection(phone.getType().ordinal());
                phoneType.setFocusable(false);

                EditText phoneNo = (EditText) row.findViewById(R.id.editText_phone);
                phoneNo.setText(phone.getPhone());
                phoneNo.setFocusable(false);

                phoneTable.addView(row);
            }
        } else {
            Phone phone = (Phone) phones.toArray()[0];
            View phoneRow = phoneTable.getChildAt(1);

            populatePhone(phone, phoneRow);
        }
    }

    private void populateEmails(Collection<Email> emails) {
        TableLayout emailTable = (TableLayout) findViewById(R.id.email_tableLayout);

        if(emails.size() > 1) {
            Object[] emailObjects = emails.toArray();
            View emailRow = emailTable.getChildAt(1);

            populateEmail((Email) emailObjects[0], emailRow);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for(int i = 1; i < emails.size(); i++) {
                View row = inflater.inflate(R.layout.email_table_row, null);
                Email email = ((Email)emailObjects[i]);

                Spinner emailType = (Spinner) row.findViewById(R.id.spinner_email_types);
                ArrayAdapter<EmailType> emailTypeArrayAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, EmailType.values());
                emailType.setAdapter(emailTypeArrayAdapter);
                emailType.setSelection(email.getType().ordinal());
                emailType.setFocusable(false);

                EditText emailId = (EditText) row.findViewById(R.id.editText_email);
                emailId.setText(email.getEmail());
                emailId.setFocusable(false);

                emailTable.addView(row);
            }
        } else {
            Email email = (Email) emails.toArray()[0];
            View emailRow = emailTable.getChildAt(1);

            populateEmail(email, emailRow);
        }
    }

    private void populateAddresses(Collection<Address> addresses) {
        TableLayout addressTable = (TableLayout) findViewById(R.id.address_tableLayout);

        Address address = (Address) addresses.toArray()[0];
        View addressRow = addressTable.getChildAt(1);

        Spinner addressType = (Spinner) addressRow.findViewById(R.id.spinner_address_types);
        addressType.setSelection(address.getType().ordinal());

        EditText addressVal = (EditText) addressRow.findViewById(R.id.editText_address);
        addressVal.setText(address.getAddress());

        addressVal.setFocusable(false);
    }

    private void populateContact(Contact contact) {
        EditText firstName = (EditText) findViewById(R.id.editText_firstName);
        firstName.setText(contact.getFirstName());
        firstName.setFocusable(false);

        EditText middleName = (EditText) findViewById(R.id.editText_middleName);
        middleName.setText(contact.getMiddleName());
        middleName.setFocusable(false);

        EditText lastName = (EditText) findViewById(R.id.editText_lastName);
        lastName.setText(contact.getLastName());
        lastName.setFocusable(false);

        Spinner contactType = (Spinner)findViewById(R.id.spinner_contact_types);
        contactType.setSelection(contact.getContactType().ordinal());
        contactType.setFocusable(false);
    }

    private void populate() {
        long id = getIntent().getLongExtra(getResources().getString(R.string.key), 0);
        if(id == 0)
            return;

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

    private void addListener() {
        Button cancel_button = (Button) findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button save_button = (Button) findViewById(R.id.add_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO  save updated fields
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
