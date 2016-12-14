package com.example.vavadive.contactmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.example.vavadive.contactmanager.common.AddFieldMenuItem;
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
 * Created by vavadive on 6/21/2016.
 */
public class AddContactActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Mode mode;
    private Long CONTACT_ID = 0l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_dynamic);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner contactType = (Spinner)findViewById(R.id.spinner_contact_types);
        ArrayAdapter<ContactType> contactTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ContactType.values());
        contactTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactType.setAdapter(contactTypeArrayAdapter);

        Spinner phoneType = (Spinner) findViewById(R.id.spinner_phone_types);
        ArrayAdapter<PhoneType> phoneTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, PhoneType.values());
        phoneTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneType.setAdapter(phoneTypeArrayAdapter);

        Spinner emailType = (Spinner) findViewById(R.id.spinner_email_types);
        ArrayAdapter<EmailType> emailTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, EmailType.values());
        emailTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emailType.setAdapter(emailTypeArrayAdapter);

        Spinner addressType = (Spinner) findViewById(R.id.spinner_address_types);
        ArrayAdapter<AddressType> addressTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, AddressType.values());
        addressTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressType.setAdapter(addressTypeArrayAdapter);

        databaseHelper = new DatabaseHelper(this);

        mode = Mode.values()[getIntent().getIntExtra("mode", 0)];

        if(mode.equals(Mode.EDIT)) {
            long id = getIntent().getLongExtra(getResources().getString(R.string.key), 0);
            if(id != 0) {
                CONTACT_ID = id;
                populate(id);
            }
        }
        addListener();
    }

    private void savePhones(Contact contact) {

        Collection<Phone> phones = contact.getPhones();
        try {
            databaseHelper.getPhoneDao().delete(phones);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TableLayout phoneTable = (TableLayout) findViewById(R.id.phone_tableLayout);

        int count = phoneTable.getChildCount();
        for(int i = 1; i < count; i++) {
            View phoneRow = phoneTable.getChildAt(i);
            Spinner phoneType = (Spinner) phoneRow.findViewById(R.id.spinner_phone_types);
            EditText phone = (EditText) phoneRow.findViewById(R.id.editText_phone);
            String phoneNo = phone.getText().toString();

            if(phoneNo != null && phoneNo.length() != 0) {
                Phone lPhone = new Phone();
                lPhone.setPhone(phoneNo);
                lPhone.setType((PhoneType) phoneType.getSelectedItem());
                lPhone.setContact(contact);

                try {
                    databaseHelper.getPhoneDao().createIfNotExists(lPhone);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveEmails(Contact contact) {
        Collection<Email> emails = contact.getEmails();
        try {
            databaseHelper.getEmailDao().delete(emails);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TableLayout emailTable = (TableLayout) findViewById(R.id.email_tableLayout);

        int count = emailTable.getChildCount();
        for(int i = 1; i < count; i++) {
            View emailRow = emailTable.getChildAt(i);
            Spinner emailType = (Spinner) emailRow.findViewById(R.id.spinner_email_types);
            EditText email = (EditText) emailRow.findViewById(R.id.editText_email);
            String emailId = email.getText().toString();

            if(emailId != null && emailId.length() != 0) {
                Email lemail = new Email();
                lemail.setEmail(emailId);
                lemail.setType((EmailType) emailType.getSelectedItem());
                lemail.setContact(contact);

                try {
                    databaseHelper.getEmailDao().createIfNotExists(lemail);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void save(View view) {
        EditText firstName = (EditText) findViewById(R.id.editText_firstName);
        EditText middleName = (EditText) findViewById(R.id.editText_middleName);
        EditText lastName = (EditText) findViewById(R.id.editText_lastName);
        Spinner contactType = (Spinner)findViewById(R.id.spinner_contact_types);

        Contact lContact = null;
        if(mode.equals(Mode.CREATE))
        {
            lContact = new Contact();
        } else if(mode.equals(Mode.EDIT)) {
            try {
                lContact = databaseHelper.getContactDao().queryForId(CONTACT_ID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        lContact.setFirstName(firstName.getText().toString());
        lContact.setMiddleName(middleName.getText().toString());
        lContact.setLastName(lastName.getText().toString());
        lContact.setContactType((ContactType) contactType.getSelectedItem());

        if(firstName.isDirty() || middleName.isDirty() || lastName.isDirty()) {
            try {
                lContact = databaseHelper.getContactDao().createIfNotExists(lContact);

                savePhones(lContact);
                saveEmails(lContact);

                Spinner addressType = (Spinner) findViewById(R.id.spinner_address_types);
                Address lAddress = new Address();
                EditText address = (EditText) findViewById(R.id.editText_address);
                if (addressType.isDirty() || address.isDirty()) {
                    databaseHelper.getAddressDao().delete(lContact.getAddresses());

                    lAddress.setAddress(address.getText().toString());
                    lAddress.setType((AddressType) addressType.getSelectedItem());
                    lAddress.setContact(lContact);

                    lAddress = databaseHelper.getAddressDao().createIfNotExists(lAddress);
                }

                setResult(RESULT_OK);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {

        }

        finish();
    }

    public void addListener() {
        /*Button add_button = (Button) findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(view);
            }
        });

        Button home_button = (Button) findViewById(R.id.cancel_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });*/

        final Button add_field_button = (Button) findViewById(R.id.add_field);
        add_field_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu add_field_popup = new PopupMenu(AddContactActivity.this,
                        add_field_button);
                Menu add_field_popup_menu = add_field_popup.getMenu();

                AddFieldMenuItem[] values = AddFieldMenuItem.values();
                for(int i = 0; i < values.length; i++) {
                    add_field_popup_menu.add(values[i].getDisplayedName());
                }

                add_field_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (AddFieldMenuItem.valueOf(item.getTitle().toString())) {
                            case Phone:
                                addPhone();
                                break;
                            case Email:
                                addEmail();
                                break;
                        }
                        return false;
                    }
                });

                add_field_popup.show();
            }
        });

        ImageView add_email = (ImageView) findViewById(R.id.add_email);
        add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEmail();
            }
        });

        ImageView add_phone = (ImageView) findViewById(R.id.add_phone);
        add_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhone();
            }
        });

        ImageView remove_email = (ImageView) findViewById(R.id.remove_email);
        remove_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeField(view);
            }
        });

        ImageView remove_phone = (ImageView) findViewById(R.id.remove_phone);
        remove_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeField(view);
            }
        });
    }

    private void addPhone() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TableLayout phoneTable = (TableLayout) findViewById(R.id.phone_tableLayout);
        View row = inflater.inflate(R.layout.phone_table_row, phoneTable, false);

        Spinner phoneType = (Spinner) row.findViewById(R.id.spinner_phone_types);
        ArrayAdapter<PhoneType> phoneTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, PhoneType.values());
        phoneTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneType.setAdapter(phoneTypeArrayAdapter);

        ImageView remove_phone = (ImageView) row.findViewById(R.id.remove_phone);
        remove_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeField(view);
            }
        });

        phoneTable.addView(row);
    }

    private void addEmail() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TableLayout emailTable = (TableLayout) findViewById(R.id.email_tableLayout);
        View row = inflater.inflate(R.layout.email_table_row, emailTable, false);

        Spinner emailType = (Spinner) row.findViewById(R.id.spinner_email_types);
        ArrayAdapter<EmailType> emailTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, EmailType.values());
        emailTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emailType.setAdapter(emailTypeArrayAdapter);

        ImageView remove_email = (ImageView) row.findViewById(R.id.remove_email);
        remove_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeField(view);
            }
        });

        emailTable.addView(row);
    }

    private void removeField(View view) {
        View row = (View) view.getParent();
        ViewGroup container = ((ViewGroup)row.getParent());
        container.removeView(row);
        container.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_contact_dynamic, menu);

        MenuItem cancel = menu.findItem(R.id.cancel_button);
        cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                setResult(RESULT_CANCELED);
                finish();

                return true;
            }
        });

        MenuItem add = menu.findItem(R.id.add_button);
        add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                save(null);

                return true;
            }
        });

        return true;
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

    private void populatePhone(Phone phone, View phoneRow) {

        Spinner phoneType = (Spinner) phoneRow.findViewById(R.id.spinner_phone_types);
        phoneType.setSelection(phone.getType().ordinal());

        EditText phoneNo = (EditText) phoneRow.findViewById(R.id.editText_phone);
        phoneNo.setText(phone.getPhone());
    }

    private void populateEmail(Email email, View emailRow) {

        Spinner emailType = (Spinner) emailRow.findViewById(R.id.spinner_email_types);
        emailType.setSelection(email.getType().ordinal());

        EditText emailId = (EditText) emailRow.findViewById(R.id.editText_email);
        emailId.setText(email.getEmail());
    }

    private void populatePhones(Collection<Phone> phones) {
        if(!phones.isEmpty()) {
            TableLayout phoneTable = (TableLayout) findViewById(R.id.phone_tableLayout);

            if (phones.size() > 1) {
                Object[] phoneObjects = phones.toArray();
                View phoneRow = phoneTable.getChildAt(1);

                populatePhone((Phone) phoneObjects[0], phoneRow);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                for (int i = 1; i < phones.size(); i++) {
                    View row = inflater.inflate(R.layout.phone_table_row, null);
                    Phone phone = ((Phone) phoneObjects[i]);

                    Spinner phoneType = (Spinner) row.findViewById(R.id.spinner_phone_types);
                    ArrayAdapter<PhoneType> phoneTypeArrayAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, PhoneType.values());
                    phoneType.setAdapter(phoneTypeArrayAdapter);
                    phoneType.setSelection(phone.getType().ordinal());

                    EditText phoneNo = (EditText) row.findViewById(R.id.editText_phone);
                    phoneNo.setText(phone.getPhone());

                    phoneTable.addView(row);
                }
            } else {
                Phone phone = (Phone) phones.toArray()[0];
                View phoneRow = phoneTable.getChildAt(1);

                populatePhone(phone, phoneRow);
            }
        }
    }

    private void populateEmails(Collection<Email> emails) {
        if(!emails.isEmpty()) {
            TableLayout emailTable = (TableLayout) findViewById(R.id.email_tableLayout);

            if (emails.size() > 1) {
                Object[] emailObjects = emails.toArray();
                View emailRow = emailTable.getChildAt(1);

                populateEmail((Email) emailObjects[0], emailRow);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                for (int i = 1; i < emails.size(); i++) {
                    View row = inflater.inflate(R.layout.email_table_row, null);
                    Email email = ((Email) emailObjects[i]);

                    Spinner emailType = (Spinner) row.findViewById(R.id.spinner_email_types);
                    ArrayAdapter<EmailType> emailTypeArrayAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, EmailType.values());
                    emailType.setAdapter(emailTypeArrayAdapter);
                    emailType.setSelection(email.getType().ordinal());

                    EditText emailId = (EditText) row.findViewById(R.id.editText_email);
                    emailId.setText(email.getEmail());

                    emailTable.addView(row);
                }
            } else {
                Email email = (Email) emails.toArray()[0];
                View emailRow = emailTable.getChildAt(1);

                populateEmail(email, emailRow);
            }
        }
    }

    private void populateAddresses(Collection<Address> addresses) {
        if(!addresses.isEmpty()) {
            TableLayout addressTable = (TableLayout) findViewById(R.id.address_tableLayout);

            Address address = (Address) addresses.toArray()[0];
            View addressRow = addressTable.getChildAt(1);

            Spinner addressType = (Spinner) addressRow.findViewById(R.id.spinner_address_types);
            addressType.setSelection(address.getType().ordinal());

            EditText adressVal = (EditText) addressRow.findViewById(R.id.editText_address);
            adressVal.setText(address.getAddress());
        }
    }

    private void populateContact(Contact contact) {
        EditText firstName = (EditText) findViewById(R.id.editText_firstName);
        firstName.setText(contact.getFirstName());

        EditText middleName = (EditText) findViewById(R.id.editText_middleName);
        middleName.setText(contact.getMiddleName());

        EditText lastName = (EditText) findViewById(R.id.editText_lastName);
        lastName.setText(contact.getLastName());

        Spinner contactType = (Spinner)findViewById(R.id.spinner_contact_types);
        contactType.setSelection(contact.getContactType().ordinal());
    }
}
