package com.example.vavadive.contactmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import java.util.Date;

/**
 * Created by vavadive on 6/22/2016.
 */
public class EditContactActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Mode mode;
    private Long CONTACT_ID = 0L;
    private Contact displayedContact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_dynamic);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Spinner contactType = (Spinner)findViewById(R.id.spinner_contact_types);
        ArrayAdapter<ContactType> contactTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ContactType.values());

        if(contactTypeArrayAdapter != null) {
            contactTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            contactType.setAdapter(contactTypeArrayAdapter);
        }

        Spinner phoneType = (Spinner) findViewById(R.id.spinner_phone_types);
        ArrayAdapter<PhoneType> phoneTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, PhoneType.values());
        if(phoneTypeArrayAdapter != null) {
            phoneTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            phoneType.setAdapter(phoneTypeArrayAdapter);
        }

        Spinner emailType = (Spinner) findViewById(R.id.spinner_email_types);
        ArrayAdapter<EmailType> emailTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, EmailType.values());
        if(emailTypeArrayAdapter != null) {
            emailTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            emailType.setAdapter(emailTypeArrayAdapter);
        }

        Spinner addressType = (Spinner) findViewById(R.id.spinner_address_types);
        ArrayAdapter<AddressType> addressTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, AddressType.values());
        if(addressTypeArrayAdapter != null) {
            addressTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            addressType.setAdapter(addressTypeArrayAdapter);
        }

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
        if(phoneTable != null) {
            int count = phoneTable.getChildCount();
            for (int i = 1; i < count; i++) {
                View phoneRow = phoneTable.getChildAt(i);
                Spinner phoneType = (Spinner) phoneRow.findViewById(R.id.spinner_phone_types);
                EditText phone = (EditText) phoneRow.findViewById(R.id.editText_phone);
                String phoneNo = phone.getText().toString();

                if (phoneNo != null && phoneNo.length() != 0) {
                    Phone lPhone = new Phone();
                    lPhone.setPhone(phoneNo);
                    lPhone.setType((PhoneType) phoneType.getSelectedItem());
                    lPhone.setLastModified(new Date().getTime());
                    lPhone.setContact(contact);

                    try {
                        databaseHelper.getPhoneDao().createIfNotExists(lPhone);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
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
        if(emailTable != null) {
            int count = emailTable.getChildCount();
            for (int i = 1; i < count; i++) {
                View emailRow = emailTable.getChildAt(i);
                Spinner emailType = (Spinner) emailRow.findViewById(R.id.spinner_email_types);
                EditText email = (EditText) emailRow.findViewById(R.id.editText_email);
                String emailId = email.getText().toString();

                if (emailId != null && emailId.length() != 0) {
                    Email lemail = new Email();
                    lemail.setEmail(emailId);
                    lemail.setType((EmailType) emailType.getSelectedItem());
                    lemail.setLastModified(new Date().getTime());
                    lemail.setContact(contact);

                    try {
                        databaseHelper.getEmailDao().createIfNotExists(lemail);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void save(View view) {
        if(displayedContact != null) {
            EditText firstNameView = (EditText) findViewById(R.id.editText_firstName);
            if(firstNameView != null) {
                String firstName = firstNameView.getText().toString();
                if(firstName.length() > 0 && !firstName.equals(displayedContact.getFirstName()))
                    displayedContact.setFirstName(firstName);
            }

            EditText middleNameView = (EditText) findViewById(R.id.editText_middleName);
            if(middleNameView != null) {
                String middleName = middleNameView.getText().toString();
                if(middleName.length() > 0 && !middleName.equals(displayedContact.getMiddleName()))
                    displayedContact.setMiddleName(middleName);
            }

            EditText lastNameView = (EditText) findViewById(R.id.editText_lastName);
            if(lastNameView != null) {
                String lastName = lastNameView.getText().toString();
                if(lastName.length() > 0 && !lastName.equals(displayedContact.getLastName()))
                    displayedContact.setLastName(lastName);
            }

            Spinner contactType = (Spinner)findViewById(R.id.spinner_contact_types);
            if(contactType != null) {
                displayedContact.setContactType((ContactType) contactType.getSelectedItem());
            }

            try {
                if(displayedContact.getFirstName().length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    setResult(RESULT_OK);
                    displayedContact.setLastModified(new Date().getTime());
                    databaseHelper.getContactDao().update(displayedContact);

                    savePhones(displayedContact);
                    saveEmails(displayedContact);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        finish();
    }

    public void addListener() {
        final Button add_field_button = (Button) findViewById(R.id.add_field);

        if(add_field_button != null) {
            add_field_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu add_field_popup = new PopupMenu(EditContactActivity.this,
                            add_field_button);
                    Menu add_field_popup_menu = add_field_popup.getMenu();

                    AddFieldMenuItem[] values = AddFieldMenuItem.values();
                    for (AddFieldMenuItem value : values) {
                        add_field_popup_menu.add(value.getDisplayedName());
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
        }

        ImageView add_email = (ImageView) findViewById(R.id.add_email);
        if(add_email != null) {
            add_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addEmail();
                }
            });
        }

        ImageView add_phone = (ImageView) findViewById(R.id.add_phone);
        if(add_phone != null) {
            add_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addPhone();
                }
            });
        }

        ImageView remove_email = (ImageView) findViewById(R.id.remove_email);
        if(remove_email != null) {
            remove_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeField(view);
                }
            });
        }

        ImageView remove_phone = (ImageView) findViewById(R.id.remove_phone);
        if(remove_phone != null) {
            remove_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeField(view);
                }
            });
        }
    }

    private void addPhone() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TableLayout phoneTable = (TableLayout) findViewById(R.id.phone_tableLayout);
        if(phoneTable != null) {
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
    }

    private void addEmail() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TableLayout emailTable = (TableLayout) findViewById(R.id.email_tableLayout);
        if(emailTable != null) {
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
            displayedContact = databaseHelper.getContactDao().queryForId(id);
            if(displayedContact != null) {
                populateContact(displayedContact);
                populatePhones(displayedContact.getPhones());
                populateEmails(displayedContact.getEmails());
                populateAddresses(displayedContact.getAddresses());
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

            if(phoneTable != null) {
                if (phones.size() > 1) {
                    Object[] phoneObjects = phones.toArray();
                    View phoneRow = phoneTable.getChildAt(1);

                    populatePhone((Phone) phoneObjects[0], phoneRow);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    for (int i = 1; i < phones.size(); i++) {
                        View row = inflater.inflate(R.layout.phone_table_row, phoneTable, false);
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
    }

    private void populateEmails(Collection<Email> emails) {
        if(!emails.isEmpty()) {
            TableLayout emailTable = (TableLayout) findViewById(R.id.email_tableLayout);
            if(emailTable != null) {
                if (emails.size() > 1) {
                    Object[] emailObjects = emails.toArray();
                    View emailRow = emailTable.getChildAt(1);

                    populateEmail((Email) emailObjects[0], emailRow);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    for (int i = 1; i < emails.size(); i++) {
                        View row = inflater.inflate(R.layout.email_table_row, emailTable, false);
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
    }

    private void populateAddresses(Collection<Address> addresses) {
        if(!addresses.isEmpty()) {
            TableLayout addressTable = (TableLayout) findViewById(R.id.address_tableLayout);

            if(addressTable != null) {
                Address address = (Address) addresses.toArray()[0];
                View addressRow = addressTable.getChildAt(1);

                Spinner addressType = (Spinner) addressRow.findViewById(R.id.spinner_address_types);
                addressType.setSelection(address.getType().ordinal());

                EditText adressVal = (EditText) addressRow.findViewById(R.id.editText_address);
                adressVal.setText(address.getAddress());
            }
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
