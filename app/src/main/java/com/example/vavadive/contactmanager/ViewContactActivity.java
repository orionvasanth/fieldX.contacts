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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.vavadive.contactmanager.common.Mode;
import com.example.vavadive.contactmanager.db.Address;
import com.example.vavadive.contactmanager.db.Contact;
import com.example.vavadive.contactmanager.db.DatabaseHelper;
import com.example.vavadive.contactmanager.db.Email;
import com.example.vavadive.contactmanager.db.IM;
import com.example.vavadive.contactmanager.db.Phone;
import com.example.vavadive.contactmanager.db.Website;
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
                startActivityForResult(editContact, MainActivity.REQUEST_EDIT);

                return true;
            }
        });

        return true;
    }

    private void populatePhone(Phone phone, View phoneRow) {
        final String toCall = phone.getPhone();

        TextView phoneNo = (TextView) phoneRow.findViewById(R.id.editText_phone);
        phoneNo.setText(toCall);

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

        TableRow row = (TableRow) phoneRow;

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View divider = inflater.inflate(R.layout.vertical_divider, row, false);
        row.addView(divider);

        ImageView sms = new ImageView(getApplicationContext());
        sms.setImageResource(R.drawable.ic_send_sms);
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri number = Uri.parse("smsto:" + toCall);
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, number);

                int permissionCheck = ContextCompat.checkSelfPermission(ViewContactActivity.this,
                        Manifest.permission.SEND_SMS);
                if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    startActivity(smsIntent);
                }
            }
        });
        row.addView(sms);
    }

    private void populateEmail(Email email, View emailRow) {
        TextView emailId = (TextView) emailRow.findViewById(R.id.editText_email);
        emailId.setText(email.getEmail());

        TableRow row = (TableRow) emailRow;

        final String id = email.getEmail();
        ImageView send_email = new ImageView(getApplicationContext());
        send_email.setImageResource(R.drawable.ic_send_email);
        send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{id});
                emailIntent.setType("message/rfc822");
                startActivity(emailIntent);
            }
        });
        row.addView(send_email);
    }

    private void populateIM(IM im, View imRow) {
        TextView imId = (TextView) imRow.findViewById(R.id.im);
        if(imId != null) {
            imId.setText(im.getIm());
        }
    }

    private void populateIMs(Collection<IM> ims) {
        if(!ims.isEmpty()) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            TableLayout imTable = (TableLayout) findViewById((R.id.im_tableLayout));
            View row = inflater.inflate(R.layout.im_table_header_view, imTable, false);
            imTable.addView(row);

            Object[] imObjects = ims.toArray();
            for(int i = 0; i < imObjects.length; i++) {
                if(i != 0) {
                    row = inflater.inflate(R.layout.horizontal_divider_table_row, imTable, false);
                    imTable.addView(row);
                }

                IM im = (IM) imObjects[i];

                row = inflater.inflate(R.layout.field_type_row, imTable, false);
                populateType(row, im.getType().getDisplayedName());
                imTable.addView(row);

                row = inflater.inflate(R.layout.im_table_row_view, imTable, false);
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
                if(i != 0) {
                    row = inflater.inflate(R.layout.horizontal_divider_table_row, phoneTable, false);
                    phoneTable.addView(row);
                }

                Phone phone = ((Phone) phoneObjects[i]);

                row = inflater.inflate(R.layout.field_type_row, phoneTable, false);
                populateType(row, phone.getType().getDisplayedName());
                phoneTable.addView(row);

                row = inflater.inflate(R.layout.phone_table_row_view, phoneTable, false);
                populatePhone(phone, row);
                phoneTable.addView(row);
            }
        }
    }

    private void populateType(View row, String type) {
        TextView type_view = (TextView) row.findViewById(R.id.field_type);
        type_view.setText(type);
    }

    private void populateEmails(Collection<Email> emails) {
        if(!emails.isEmpty()) {
            TableLayout emailTable = (TableLayout) findViewById(R.id.email_tableLayout);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.email_table_header, emailTable, false);
            emailTable.addView(row);

            Object[] emailObjects = emails.toArray();

            for (int i = 0; i < emails.size(); i++) {
                if(i != 0) {
                    row = inflater.inflate(R.layout.horizontal_divider_table_row, emailTable, false);
                    emailTable.addView(row);
                }

                Email email = ((Email) emailObjects[i]);

                row = inflater.inflate(R.layout.field_type_row, emailTable, false);
                populateType(row, email.getType().getDisplayedName());
                emailTable.addView(row);

                row = inflater.inflate(R.layout.email_table_row_view, emailTable, false);
                populateEmail(email, row);
                emailTable.addView(row);
            }
        }
    }

    private void populateWebsites(Collection<Website> websites) {
        if(!websites.isEmpty()) {
            TableLayout websiteTable = (TableLayout) findViewById(R.id.website_tableLayout);

            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.table_header_wo_action, websiteTable, false);
            TextView fieldName = (TextView) row.findViewById(R.id.field_name);
            fieldName.setText(getResources().getString(R.string.website));
            websiteTable.addView(row);

            Object[] websiteObjects = websites.toArray();
            for(int i = 0; i < websites.size(); i++) {
                if(i != 0) {
                    row = inflater.inflate(R.layout.horizontal_divider_table_row, websiteTable, false);
                    websiteTable.addView(row);
                }

                Website website = (Website) websiteObjects[i];

                row = inflater.inflate(R.layout.table_row_wo_action, websiteTable, false);
                TextView value = (TextView) row.findViewById(R.id.value);
                value.setText(website.getUrl());
                websiteTable.addView(row);
            }
        }
    }

    private void populateAddresses(Collection<Address> addresses) {
        if(!addresses.isEmpty()) {
            TableLayout addressTable = (TableLayout) findViewById(R.id.address_tableLayout);

            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.table_header_wo_action, addressTable, false);
            TextView fieldName = (TextView) row.findViewById(R.id.field_name);
            if(fieldName != null) {
                fieldName.setText(getResources().getString(R.string.address));
            }
            addressTable.addView(row);

            Object[] addressObjects = addresses.toArray();

            for(int i = 0; i < addresses.size(); i++) {
                if(i != 0) {
                    row = inflater.inflate(R.layout.horizontal_divider_table_row, addressTable, false);
                    addressTable.addView(row);
                }

                Address address = (Address) addressObjects[i];

                row = inflater.inflate(R.layout.field_type_row, addressTable, false);
                populateType(row, address.getType().getDisplayName());
                addressTable.addView(row);

                row = inflater.inflate(R.layout.table_row_wo_action, addressTable, false);
                TextView value = (TextView) row.findViewById(R.id.value);
                value.setText(address.getAddress());
                addressTable.addView(row);
            }
        }
    }

    private void populateContact(Contact contact) {
        if(contact != null) {
            StringBuilder fullName = new StringBuilder();
            if(!StringUtil.isNull(contact.getFirstName())) {
                fullName.append(contact.getFirstName() + " ");
            }

            if(!StringUtil.isNull(contact.getMiddleName())) {
                fullName.append(contact.getMiddleName() + " ");
            }

            if(!StringUtil.isNull(contact.getLastName())) {
                fullName.append(contact.getLastName());
            }
            getSupportActionBar().setTitle(fullName.toString().trim());

            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            TableLayout nameTable =
                    (TableLayout) findViewById(R.id.name_tableLayout);

            View row = inflater.inflate(R.layout.table_header_wo_action, nameTable, false);
            TextView fieldName = (TextView) row.findViewById(R.id.field_name);
            fieldName.setText(getResources().getString(R.string.contactType));
            nameTable.addView(row);

            row = inflater.inflate(R.layout.table_row_wo_action, nameTable, false);
            TextView value = (TextView) row.findViewById(R.id.value);
            value.setText(contact.getContactType().getDisplayedName());
            nameTable.addView(row);

            if(!StringUtil.isNull(contact.getCompany())
                    || !StringUtil.isNull(contact.getJobTitle())) {
                TableLayout organizationTable =
                        (TableLayout) findViewById(R.id.organization_tableLayout);

                organizationTable.removeAllViews();

                row = inflater.inflate(R.layout.table_header_wo_action, organizationTable, false);
                fieldName = (TextView) row.findViewById(R.id.field_name);
                fieldName.setText(getResources().getString(R.string.organization));
                organizationTable.addView(row);

                row = inflater.inflate(R.layout.table_row_wo_action, organizationTable, false);
                value = (TextView) row.findViewById(R.id.value);

                StringBuilder position = new StringBuilder();
                if(!StringUtil.isNull(contact.getJobTitle())) {
                    position.append(contact.getJobTitle() + ", ");
                }

                if(!StringUtil.isNull(contact.getCompany())) {
                    position.append(contact.getCompany());
                }
                value.setText(position.toString());

                organizationTable.addView(row);
            }

            if(!StringUtil.isNull(contact.getNotes())) {
                TableLayout notesTable =
                        (TableLayout) findViewById(R.id.notes_tableLayout);

                notesTable.removeAllViews();

                row = inflater.inflate(R.layout.table_header_wo_action, notesTable, false);
                fieldName = (TextView) row.findViewById(R.id.field_name);
                fieldName.setText(getResources().getString(R.string.notes));
                notesTable.addView(row);

                row = inflater.inflate(R.layout.table_row_wo_action, notesTable, false);
                TextView fieldValue = (TextView) row.findViewById(R.id.value);
                fieldValue.setText(contact.getNotes());
                notesTable.addView(row);
            }

            if(!StringUtil.isNull(contact.getNickname())) {
                TableLayout nicknameTable =
                        (TableLayout) findViewById(R.id.nickname_tableLayout);

                nicknameTable.removeAllViews();

                row = inflater.inflate(R.layout.table_header_wo_action, nicknameTable, false);
                fieldName = (TextView) row.findViewById(R.id.field_name);
                fieldName.setText(getResources().getString(R.string.nickname));
                nicknameTable.addView(row);

                row = inflater.inflate(R.layout.table_row_wo_action, nicknameTable, false);
                TextView fieldValue = (TextView) row.findViewById(R.id.value);
                fieldValue.setText(contact.getNickname());
                nicknameTable.addView(row);
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
            populateWebsites(contact.getWebsites());
            populateIMs(contact.getIms());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateContact(Contact contact) {
        TableLayout nameTable = (TableLayout) findViewById(R.id.name_tableLayout);
        if(nameTable != null) {
            nameTable.removeAllViews();
        }

        populateContact(contact);
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
        TableLayout addressTable = (TableLayout) findViewById(R.id.address_tableLayout);
        if(addressTable != null) {
            addressTable.removeAllViews();
        }

        populateAddresses(addresses);
    }

    private void updateWebsites(Collection<Website> websites) {
        TableLayout websiteTable = (TableLayout) findViewById(R.id.website_tableLayout);
        if(websiteTable != null) {
            websiteTable.removeAllViews();
        }

        populateWebsites(websites);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            Contact modified_contact = databaseHelper.getContactDao().queryForId(CONTACT_ID);

            updateContact(modified_contact);

            updatePhones(modified_contact.getPhones());
            updateEmails(modified_contact.getEmails());
            updateAddresses(modified_contact.getAddresses());
            updateIMs(modified_contact.getIms());
            updateWebsites(modified_contact.getWebsites());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
