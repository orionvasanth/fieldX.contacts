package com.example.vavadive.contactmanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.example.vavadive.contactmanager.common.AddressType;
import com.example.vavadive.contactmanager.common.ContactType;
import com.example.vavadive.contactmanager.common.EmailType;
import com.example.vavadive.contactmanager.common.IMType;
import com.example.vavadive.contactmanager.common.Mode;
import com.example.vavadive.contactmanager.common.PhoneType;
import com.example.vavadive.contactmanager.db.Address;
import com.example.vavadive.contactmanager.db.Contact;
import com.example.vavadive.contactmanager.db.DatabaseHelper;
import com.example.vavadive.contactmanager.db.Email;
import com.example.vavadive.contactmanager.db.IM;
import com.example.vavadive.contactmanager.db.Phone;
import com.example.vavadive.contactmanager.util.StringUtil;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by vavadive on 6/22/2016.
 */
public class ViewContactActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Mode mode;
    private Long CONTACT_ID = 0L;

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
                Intent editContact = new Intent(ViewContactActivity.this, EditContactActivity.class);
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

        final String toCall = phone.getPhone();

        EditText phoneNo = (EditText) phoneRow.findViewById(R.id.editText_phone);
        phoneNo.setText(toCall);
        phoneNo.setInputType(InputType.TYPE_NULL);

        ImageView call = (ImageView) phoneRow.findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri number = Uri.parse("tel:" + toCall);
                Intent callIntent = new Intent(Intent.ACTION_CALL, number);

                int permissionCheck = ContextCompat.checkSelfPermission(ViewContactActivity.this,
                        Manifest.permission.CALL_PHONE);
                if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                }
            }
        });
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

    private void populateIM(IM im, View imRow) {
        Spinner imType = (Spinner) imRow.findViewById(R.id.spinner_im_types);
        if(imType != null) {
            ArrayAdapter<IMType> emailTypeArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, IMType.values());
            imType.setAdapter(emailTypeArrayAdapter);
            imType.setSelection(im.getType().ordinal());
            imType.setEnabled(false);
        }

        EditText imId = (EditText) imRow.findViewById(R.id.im);
        if(imId != null) {
            imId.setText(im.getIm());
            imId.setInputType(InputType.TYPE_NULL);
        }
    }

    private void populateIMs(Collection<IM> ims) {
        if(!ims.isEmpty()) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            TableLayout imTable = (TableLayout) findViewById((R.id.im_tableLayout));
            View row = inflater.inflate(R.layout.im_table_header_view, imTable, false);
            imTable.addView(row);

            Object[] imObjects = ims.toArray();
            for(Object object : imObjects) {
                row = inflater.inflate(R.layout.im_table_row_view, imTable, false);

                IM im = (IM) object;
                populateIM(im, row);

                imTable.addView(row);
            }
        }
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
        if(!emails.isEmpty()) {
            TableLayout emailTable = (TableLayout) findViewById(R.id.email_tableLayout);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.email_table_header, emailTable, false);
            emailTable.addView(row);

            Object[] emailObjects = emails.toArray();

            for (int i = 0; i < emails.size(); i++) {
                row = inflater.inflate(R.layout.email_table_row_view, emailTable, false);

                Email email = ((Email) emailObjects[i]);
                populateEmail(email, row);

                emailTable.addView(row);
            }
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
        if(contact != null) {
            EditText firstName = (EditText) findViewById(R.id.editText_firstName);
            firstName.setText(contact.getFirstName());
            firstName.setInputType(InputType.TYPE_NULL);

            EditText middleName = (EditText) findViewById(R.id.editText_middleName);
            middleName.setText(contact.getMiddleName());
            middleName.setInputType(InputType.TYPE_NULL);

            EditText lastName = (EditText) findViewById(R.id.editText_lastName);
            lastName.setText(contact.getLastName());
            lastName.setInputType(InputType.TYPE_NULL);

            Spinner contactType = (Spinner) findViewById(R.id.spinner_contact_types);
            ArrayAdapter<ContactType> contactTypeArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, ContactType.values());
            contactTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            contactType.setAdapter(contactTypeArrayAdapter);
            contactType.setSelection(contact.getContactType().ordinal());
            contactType.setEnabled(false);

            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(!StringUtil.isNull(contact.getCompany())) {
                TableLayout organizationTable =
                        (TableLayout) findViewById(R.id.organization_tableLayout);

                organizationTable.removeAllViews();

                View row = inflater.inflate(R.layout.organization_table_header, organizationTable, false);
                organizationTable.addView(row);

                row = inflater.inflate(R.layout.organization_table_company_row, organizationTable, false);
                EditText company = (EditText) row.findViewById(R.id.company);
                if(company != null) {
                    company.setText(contact.getCompany());
                    company.setEnabled(false);
                }
                organizationTable.addView(row);

                if(!StringUtil.isNull(contact.getJobTitle())) {
                    row = inflater.inflate(R.layout.organization_table_job_row, organizationTable, false);
                    EditText job = (EditText) row.findViewById(R.id.job);
                    if(job != null) {
                        job.setText(contact.getJobTitle());
                        job.setEnabled(false);
                    }
                    organizationTable.addView(row);
                }
            }

            if(!StringUtil.isNull(contact.getNotes())) {
                TableLayout notesTable =
                        (TableLayout) findViewById(R.id.notes_tableLayout);

                notesTable.removeAllViews();

                View row = inflater.inflate(R.layout.notes_table_header, notesTable, false);
                notesTable.addView(row);

                row = inflater.inflate(R.layout.notes_table_row, notesTable, false);
                EditText notes = (EditText) row.findViewById(R.id.notes);
                if(notes != null) {
                    notes.setText(contact.getNotes());
                    notes.setEnabled(false);
                }
                notesTable.addView(row);
            }

            if(!StringUtil.isNull(contact.getNickname())) {
                TableLayout nicknameTable =
                        (TableLayout) findViewById(R.id.nickname_tableLayout);

                nicknameTable.removeAllViews();

                View row = inflater.inflate(R.layout.nickname_table_header, nicknameTable, false);
                nicknameTable.addView(row);

                row = inflater.inflate(R.layout.nickname_table_row, nicknameTable, false);
                EditText nickname = (EditText) row.findViewById(R.id.nickname);
                if(nickname != null) {
                    nickname.setText(contact.getNotes());
                    nickname.setEnabled(false);
                }
                nicknameTable.addView(row);
            }

            if(!StringUtil.isNull(contact.getWebsite())) {
                TableLayout websiteTable =
                        (TableLayout) findViewById(R.id.website_tableLayout);

                websiteTable.removeAllViews();

                View row = inflater.inflate(R.layout.website_table_header, websiteTable, false);
                websiteTable.addView(row);

                row = inflater.inflate(R.layout.website_table_row, websiteTable, false);
                EditText website = (EditText) row.findViewById(R.id.website);
                if(website != null) {
                    website.setText(contact.getWebsite());
                    website.setEnabled(false);
                }
                websiteTable.addView(row);
            }
        }
    }

    private void populate(long id) {
        try {
            Contact contact = databaseHelper.getContactDao().queryForId(id);
            populateContact(contact);
            populatePhones(contact.getPhones());
            populateEmails(contact.getEmails());
            populateAddresses(contact.getAddresses());
            populateIMs(contact.getIms());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePhones(Collection<Phone> phones) {
        TableLayout phoneTable = (TableLayout) findViewById(R.id.phone_tableLayout);
        if(phoneTable != null) {
            phoneTable.removeAllViews();
        }

        populatePhones(phones);
    }

    private void updateEmails(Collection<Email> emails) {
        TableLayout emailTable = (TableLayout) findViewById(R.id.email_tableLayout);
        if(emailTable != null) {
            emailTable.removeAllViews();
        }

        populateEmails(emails);
    }

    private void updateIMs(Collection<IM> ims) {
        TableLayout imTable = (TableLayout) findViewById(R.id.im_tableLayout);
        if(imTable != null) {
            imTable.removeAllViews();
        }

        populateIMs(ims);
    }

    private void updateAddresses(Collection<Address> addresses) {
        populateAddresses(addresses);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            Contact modified_contact = databaseHelper.getContactDao().queryForId(CONTACT_ID);

            populateContact(modified_contact);

            updatePhones(modified_contact.getPhones());
            updateEmails(modified_contact.getEmails());
            updateAddresses(modified_contact.getAddresses());
            updateIMs(modified_contact.getIms());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
