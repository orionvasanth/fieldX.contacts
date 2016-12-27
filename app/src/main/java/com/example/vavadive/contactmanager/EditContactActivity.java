package com.example.vavadive.contactmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.vavadive.contactmanager.common.AddressType;
import com.example.vavadive.contactmanager.common.ContactType;
import com.example.vavadive.contactmanager.common.EmailType;
import com.example.vavadive.contactmanager.common.IMType;
import com.example.vavadive.contactmanager.common.PhoneType;
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
import java.util.Date;

/**
 * Created by vavadive on 6/22/2016.
 */
public class EditContactActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseHelper databaseHelper;
    private Contact displayedContact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_dynamic);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Spinner contactType = (Spinner) findViewById(R.id.spinner_contact_types);
        ArrayAdapter<ContactType> contactTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ContactType.values());

        if (contactTypeArrayAdapter != null) {
            contactTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            contactType.setAdapter(contactTypeArrayAdapter);
        }

        Spinner phoneType = (Spinner) findViewById(R.id.spinner_phone_types);
        ArrayAdapter<PhoneType> phoneTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, PhoneType.values());
        if (phoneTypeArrayAdapter != null) {
            phoneTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            phoneType.setAdapter(phoneTypeArrayAdapter);
        }

        Spinner emailType = (Spinner) findViewById(R.id.spinner_email_types);
        ArrayAdapter<EmailType> emailTypeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, EmailType.values());
        if (emailTypeArrayAdapter != null) {
            emailTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            emailType.setAdapter(emailTypeArrayAdapter);
        }

        databaseHelper = new DatabaseHelper(this);

        long id = getIntent().getLongExtra(getResources().getString(R.string.key), 0);
        if (id != 0) {
            populate(id);
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
        if (phoneTable != null) {
            int count = phoneTable.getChildCount() - 1;
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
        if (emailTable != null) {
            int count = emailTable.getChildCount() - 1;
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

    private void saveIMs(Contact contact) {
        Collection<IM> ims = contact.getIms();

        try {
            databaseHelper.getImDao().delete(ims);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TableLayout imTable = (TableLayout) findViewById(R.id.im_tableLayout);
        if (imTable != null && imTable.getChildCount() != 0) {
            int count = imTable.getChildCount() - 1;

            for (int i = 1; i < count; i++) {
                View imRow = imTable.getChildAt(i);
                Spinner imType = (Spinner) imRow.findViewById(R.id.spinner_im_types);
                EditText imText = (EditText) imRow.findViewById(R.id.im);

                if (imType != null && imText != null) {
                    String id = imText.getText().toString();
                    if (!StringUtil.isNull(id)) {
                        IM im = new IM();

                        im.setIm(id);
                        im.setType((IMType) imType.getSelectedItem());
                        im.setLastModified(new Date().getTime());
                        im.setContact(contact);

                        try {
                            databaseHelper.getImDao().createIfNotExists(im);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void saveAddress(Contact contact) {
        Collection<Address> addresses = contact.getAddresses();
        try {
            databaseHelper.getAddressDao().delete(addresses);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TableLayout addressTable = (TableLayout) findViewById(R.id.address_tableLayout);
        if (addressTable != null && addressTable.getChildCount() > 1) {
            int count = addressTable.getChildCount() - 1;
            for (int i = 1; i < count; i++) {
                View addressRow = addressTable.getChildAt(i);
                Spinner addressType = (Spinner) addressRow.findViewById(R.id.spinner);
                EditText address = (EditText) addressRow.findViewById(R.id.value);
                if (addressType != null && address != null) {
                    if (!StringUtil.isNull(address.getText().toString())) {
                        try {
                            Address lAddress = new Address();
                            lAddress.setAddress(address.getText().toString());
                            lAddress.setType((AddressType) addressType.getSelectedItem());
                            lAddress.setContact(contact);

                            databaseHelper.getAddressDao().createIfNotExists(lAddress);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void saveWebsites(Contact contact) {
        TableLayout websiteTable = (TableLayout) findViewById(R.id.website_tableLayout);
        if (websiteTable != null && websiteTable.getChildCount() > 1) {
            int count = websiteTable.getChildCount() - 1;
            for (int i = 1; i < count; i++) {
                View websiteRow = websiteTable.getChildAt(i);
                EditText url = (EditText) websiteRow.findViewById(R.id.website);
                if (url != null) {
                    String value = url.getText().toString();
                    if (!StringUtil.isNull(value)) {
                        Website website = new Website();
                        website.setUrl(value);
                        website.setContact(contact);

                        try {
                            databaseHelper.getWebsiteDao().createIfNotExists(website);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void save() {
        if (displayedContact != null) {
            Spinner contactType = (Spinner) findViewById(R.id.spinner_contact_types);
            if (contactType != null) {
                displayedContact.setContactType((ContactType) contactType.getSelectedItem());
            }

            EditText company = (EditText) findViewById(R.id.company);
            if (company != null) {
                displayedContact.setCompany(company.getText().toString());
            }

            EditText job = (EditText) findViewById(R.id.job);
            if (job != null) {
                displayedContact.setJobTitle(job.getText().toString());
            }

            EditText nickname = (EditText) findViewById(R.id.nickname);
            if (nickname != null) {
                displayedContact.setNickname(nickname.getText().toString());
            }

            EditText notes = (EditText) findViewById(R.id.notes);
            if (notes != null) {
                displayedContact.setNotes(notes.getText().toString());
            }

            try {
                if (displayedContact.getFirstName().length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    setResult(RESULT_OK);
                    displayedContact.setLastModified(new Date().getTime());
                    databaseHelper.getContactDao().update(displayedContact);

                    savePhones(displayedContact);
                    saveEmails(displayedContact);
                    saveIMs(displayedContact);
                    saveAddress(displayedContact);
                    saveWebsites(displayedContact);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        finish();
    }

    public void addListener() {
        final Button add_field_button = (Button) findViewById(R.id.add_field);

        if (add_field_button != null) {
            add_field_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater inflater =
                            (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View popup = inflater.inflate(R.layout.add_another_field_popup, null);

                    final PopupWindow add_another_field_popup = new PopupWindow(
                            popup,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

                    Button cancel_button = (Button) popup.findViewById(R.id.cancel_button);
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            add_another_field_popup.dismiss();
                        }
                    });

                    Button ok_button = (Button) popup.findViewById(R.id.ok_button);
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addSelectedFields(v);
                            add_another_field_popup.dismiss();
                        }
                    });

                    add_another_field_popup.showAtLocation(findViewById(R.id.add_contact),
                            Gravity.CENTER, 0, 0);
                }
            });
        }

        ImageView add_email = (ImageView) findViewById(R.id.add_email);
        if (add_email != null) {
            add_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addEmail();
                }
            });
        }

        ImageView add_phone = (ImageView) findViewById(R.id.add_phone);
        if (add_phone != null) {
            add_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addPhone();
                }
            });
        }

        ImageView remove_email = (ImageView) findViewById(R.id.remove_email);
        if (remove_email != null) {
            remove_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeField(view);
                }
            });
        }

        ImageView remove_phone = (ImageView) findViewById(R.id.remove_phone);
        if (remove_phone != null) {
            remove_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeField(view);
                }
            });
        }

        final EditText name = (EditText) findViewById(R.id.editText_name);
        if (name != null) {
            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    updateName(editable.toString());
                }
            });
        }
    }

    private void updateName(String fullName) {

        if (!StringUtil.isNull(fullName)) {
            String[] names = fullName.split(" ");
            if (names.length < 3) {
                switch (names.length) {
                    case 1:
                        displayedContact.setFirstName(names[0]);
                        displayedContact.setMiddleName("");
                        displayedContact.setLastName("");
                        break;

                    case 2:
                        displayedContact.setFirstName(names[0]);
                        displayedContact.setLastName(names[1]);
                        displayedContact.setMiddleName("");

                        break;
                }
            } else if (names.length == 3) {
                displayedContact.setFirstName(names[0]);
                displayedContact.setMiddleName(names[1]);
                displayedContact.setLastName(names[2]);
            } else if (names.length > 3) {
                displayedContact.setLastName(names[names.length - 1]);
                displayedContact.setMiddleName(names[names.length - 2]);

                StringBuilder firstName = new StringBuilder();
                for (int i = 0; i < names.length - 2; i++) {
                    firstName.append(names[i] + " ");
                }
                displayedContact.setFirstName(firstName.toString().trim());
            }
        }
    }

    private void expandName() {
        LinearLayout name_linearLayout =
                (LinearLayout) findViewById(R.id.name_linearLayout);
        name_linearLayout.removeAllViews();

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View name_expanded = inflater.inflate(R.layout.name_expanded,
                name_linearLayout, false);
        EditText firstName = (EditText) name_expanded.findViewById(R.id.editText_firstName);
        EditText middleName = (EditText) name_expanded.findViewById(R.id.editText_middleName);
        EditText lastName = (EditText) name_expanded.findViewById(R.id.editText_lastName);

        if (firstName == null || middleName == null || lastName == null) {
            return;
        }

        firstName.setText(displayedContact.getFirstName());
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                displayedContact.setFirstName(editable.toString());
            }
        });

        middleName.setText(displayedContact.getMiddleName());
        middleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                displayedContact.setMiddleName(editable.toString());
            }
        });

        lastName.setText(displayedContact.getLastName());
        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                displayedContact.setLastName(editable.toString());
            }
        });

        name_linearLayout.addView(name_expanded);
    }

    private void collapseName() {
        StringBuilder fullName = new StringBuilder();
        if (!StringUtil.isNull(displayedContact.getFirstName())) {
            fullName.append(displayedContact.getFirstName() + " ");
        }

        if (!StringUtil.isNull(displayedContact.getMiddleName())) {
            fullName.append(displayedContact.getMiddleName() + " ");
        }

        if (!StringUtil.isNull(displayedContact.getLastName())) {
            fullName.append(displayedContact.getLastName());
        }

        LinearLayout name_linearLayout =
                (LinearLayout) findViewById(R.id.name_linearLayout);
        name_linearLayout.removeAllViews();

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View name_collapsed = inflater.inflate(R.layout.name_collapsed,
                name_linearLayout, false);
        EditText name = (EditText) name_collapsed.findViewById(R.id.editText_name);
        if (name != null) {
            name.setText(fullName.toString().trim());
            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    updateName(editable.toString());
                }
            });
        }

        name_linearLayout.addView(name_collapsed);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.expand_name:
                expandName();
                break;

            case R.id.collapse_name:
                collapseName();
                break;

            case R.id.remove:
                removeField(v);
                break;

            case R.id.add_notes:
                addNotes();
                break;

            case R.id.add_nickname:
                addNickname();
                break;
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

            int count = phoneTable.getChildCount();
            if(count < 2) {

            } else if(count == 2) {
                phoneTable.addView(row, 1);
            } else if(count > 2) {
                phoneTable.addView(row, count - 1);
            }
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

            int count = emailTable.getChildCount();
            if(count < 2) {

            } else if(count == 2) {
                emailTable.addView(row, 1);
            } else if(count > 2) {
                emailTable.addView(row, count - 1);
            }
        }
    }

    private void addIM() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TableLayout imTable = (TableLayout) findViewById(R.id.im_tableLayout);
        if(imTable != null) {
            View row = inflater.inflate(R.layout.im_table_row, imTable, false);

            Spinner imType = (Spinner) row.findViewById(R.id.spinner_im_types);
            ArrayAdapter<IMType> imTypeArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, IMType.values());
            imTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            imType.setAdapter(imTypeArrayAdapter);

            ImageView remove_im = (ImageView) row.findViewById(R.id.remove_im);
            remove_im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeField(view);
                }
            });

            int count = imTable.getChildCount();
            if(count < 2) {

            } else if(count == 2) {
                imTable.addView(row, 1);
            } else if(count > 2) {
                imTable.addView(row, count - 1);
            }
        }
    }

    private void removeField(View view) {
        View row = (View) view.getParent();
        ViewGroup container = ((ViewGroup) row.getParent());
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
                save();

                return true;
            }
        });

        return true;
    }

    private void populate(long id) {
        try {
            displayedContact = databaseHelper.getContactDao().queryForId(id);
            if (displayedContact != null) {
                populateContact(displayedContact);
                populatePhones(displayedContact.getPhones());
                populateEmails(displayedContact.getEmails());
                populateAddresses(displayedContact.getAddresses());
                populateIMs(displayedContact.getIms());
                populateWebsites(displayedContact.getWebsites());
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
        if (!phones.isEmpty()) {
            TableLayout phoneTable = (TableLayout) findViewById(R.id.phone_tableLayout);

            if (phoneTable != null) {
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

                        ImageView remove_phone = (ImageView) row.findViewById(R.id.remove_phone);
                        remove_phone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeField(view);
                            }
                        });

                        int count = phoneTable.getChildCount();
                        if(count < 2) {

                        } else if(count == 2) {
                            phoneTable.addView(row, 1);
                        } else if(count > 2) {
                            phoneTable.addView(row, count - 1);
                        }
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
        if (!emails.isEmpty()) {
            TableLayout emailTable = (TableLayout) findViewById(R.id.email_tableLayout);
            if (emailTable != null) {
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

                        ImageView remove_email = (ImageView) row.findViewById(R.id.remove_email);
                        remove_email.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeField(view);
                            }
                        });

                        int count = emailTable.getChildCount();
                        if(count < 2) {

                        } else if(count == 2) {
                            emailTable.addView(row, 1);
                        } else if(count > 2) {
                            emailTable.addView(row, count - 1);
                        }
                    }
                } else {
                    Email email = (Email) emails.toArray()[0];
                    View emailRow = emailTable.getChildAt(1);

                    populateEmail(email, emailRow);
                }
            }
        }
    }

    private void populateWebsites(Collection<Website> websites) {
        if (!websites.isEmpty()) {
            TableLayout websiteTable = (TableLayout) findViewById(R.id.website_tableLayout);

            if (websiteTable != null) {
                LayoutInflater inflater =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View row = inflater.inflate(R.layout.website_table_header, websiteTable, false);

                ImageView add = (ImageView) row.findViewById(R.id.add);
                if (add != null) {
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addWebsite();
                        }
                    });
                }
                websiteTable.addView(row);

                row = inflater.inflate(R.layout.horizontal_divider_table_row, websiteTable, false);
                websiteTable.addView(row);

                Object[] websiteObjects = websites.toArray();
                for (int i = 0; i < websites.size(); i++) {
                    Website website = (Website) websiteObjects[i];
                    row = inflater.inflate(R.layout.website_table_row, websiteTable, false);

                    EditText value = (EditText) row.findViewById(R.id.website);
                    value.setText(website.getUrl());

                    int count = websiteTable.getChildCount();
                    if(count < 2) {

                    } else if(count == 2) {
                        websiteTable.addView(row, 1);
                    } else if(count > 2) {
                        websiteTable.addView(row, count - 1);
                    }
                }
            }
        }
    }

    private void populateAddresses(Collection<Address> addresses) {
        if (!addresses.isEmpty()) {
            TableLayout addressTable = (TableLayout) findViewById(R.id.address_tableLayout);

            if (addressTable != null) {
                LayoutInflater inflater =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View row = inflater.inflate(R.layout.table_header_with_action, addressTable, false);

                TextView title = (TextView) row.findViewById(R.id.field_name);
                title.setText(getResources().getString(R.string.address));

                ImageView add = (ImageView) row.findViewById(R.id.add);
                if (add != null) {
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addAddress();
                        }
                    });
                }
                addressTable.addView(row);

                row = inflater.inflate(R.layout.horizontal_divider_table_row, addressTable, false);
                addressTable.addView(row);

                Object[] addressObjects = addresses.toArray();

                for (int i = 0; i < addresses.size(); i++) {
                    Address address = (Address) addressObjects[i];

                    row = inflater.inflate(R.layout.table_row_with_action, addressTable, false);
                    Spinner addressType = (Spinner) row.findViewById(R.id.spinner);
                    ArrayAdapter<AddressType> addressTypeArrayAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, AddressType.values());
                    addressTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    addressType.setAdapter(addressTypeArrayAdapter);

                    EditText value = (EditText) row.findViewById(R.id.value);
                    value.setText(address.getAddress());
                    int count = addressTable.getChildCount();
                    if(count < 2) {

                    } else if(count == 2) {
                        addressTable.addView(row, 1);
                    } else if(count > 2) {
                        addressTable.addView(row, count - 1);
                    }
                }
            }
        }
    }

    private void populateContact(Contact contact) {
        EditText name = (EditText) findViewById(R.id.editText_name);
        if (name == null) {
            EditText firstName = (EditText) findViewById(R.id.editText_firstName);
            firstName.setText(contact.getFirstName());

            EditText middleName = (EditText) findViewById(R.id.editText_middleName);
            middleName.setText(contact.getMiddleName());

            EditText lastName = (EditText) findViewById(R.id.editText_lastName);
            lastName.setText(contact.getLastName());
        } else {
            StringBuilder fullName = new StringBuilder();
            if (!StringUtil.isNull(contact.getFirstName())) {
                fullName.append(contact.getFirstName() + " ");
            }

            if (!StringUtil.isNull(contact.getMiddleName())) {
                fullName.append(contact.getMiddleName() + " ");
            }

            if (!StringUtil.isNull(contact.getLastName())) {
                fullName.append(contact.getLastName());
            }

            name.setText(fullName.toString().trim());
        }

        Spinner contactType = (Spinner) findViewById(R.id.spinner_contact_types);
        contactType.setSelection(contact.getContactType().ordinal());

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (!StringUtil.isNull(contact.getCompany())) {
            TableLayout organizationTable =
                    (TableLayout) findViewById(R.id.organization_tableLayout);

            View row = inflater.inflate(R.layout.organization_table_header, organizationTable, false);
            organizationTable.addView(row);

            row = inflater.inflate(R.layout.organization_table_company_row, organizationTable, false);
            EditText company = (EditText) row.findViewById(R.id.company);
            if (company != null) {
                company.setText(contact.getCompany());
            }
            organizationTable.addView(row);

            if (!StringUtil.isNull(contact.getJobTitle())) {
                row = inflater.inflate(R.layout.organization_table_job_row, organizationTable, false);
                EditText job = (EditText) row.findViewById(R.id.job);
                if (job != null) {
                    job.setText(contact.getJobTitle());
                }
                organizationTable.addView(row);

                row = inflater.inflate(R.layout.horizontal_divider_table_row, organizationTable, false);
                organizationTable.addView(row);
            }
        }

        if (!StringUtil.isNull(contact.getNotes())) {
            TableLayout notesTable =
                    (TableLayout) findViewById(R.id.notes_tableLayout);

            View row = inflater.inflate(R.layout.notes_table_header, notesTable, false);
            notesTable.addView(row);

            row = inflater.inflate(R.layout.notes_table_row, notesTable, false);
            EditText notes = (EditText) row.findViewById(R.id.notes);
            if (notes != null) {
                notes.setText(contact.getNotes());
            }
            notesTable.addView(row);

            row = inflater.inflate(R.layout.horizontal_divider_table_row, notesTable, false);
            notesTable.addView(row);
        }

        if (!StringUtil.isNull(contact.getNickname())) {
            TableLayout nicknameTable =
                    (TableLayout) findViewById(R.id.nickname_tableLayout);

            View row = inflater.inflate(R.layout.nickname_table_header, nicknameTable, false);
            nicknameTable.addView(row);

            row = inflater.inflate(R.layout.nickname_table_row, nicknameTable, false);
            EditText nickname = (EditText) row.findViewById(R.id.nickname);
            if (nickname != null) {
                nickname.setText(contact.getNickname());
            }
            nicknameTable.addView(row);

            row = inflater.inflate(R.layout.horizontal_divider_table_row, nicknameTable, false);
            nicknameTable.addView(row);
        }
    }

    private void populateIM(IM im, View imRow) {
        Spinner imType = (Spinner) imRow.findViewById(R.id.spinner_im_types);
        if (imType != null) {
            ArrayAdapter<IMType> emailTypeArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, IMType.values());
            imType.setAdapter(emailTypeArrayAdapter);
            imType.setSelection(im.getType().ordinal());
        }

        EditText imId = (EditText) imRow.findViewById(R.id.im);
        if (imId != null) {
            imId.setText(im.getIm());
        }

        ImageView remove_im = (ImageView) imRow.findViewById(R.id.remove_im);
        if (remove_im != null) {
            remove_im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeField(view);
                }
            });
        }
    }

    private void populateIMs(Collection<IM> ims) {
        if (!ims.isEmpty()) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            TableLayout imTable = (TableLayout) findViewById((R.id.im_tableLayout));
            View row = inflater.inflate(R.layout.im_table_header, imTable, false);
            ImageView add_im = (ImageView) row.findViewById(R.id.add_im);
            if (add_im != null) {
                add_im.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addIM();
                    }
                });
            }
            imTable.addView(row);

            row = inflater.inflate(R.layout.horizontal_divider_table_row, imTable, false);
            imTable.addView(row);

            Object[] imObjects = ims.toArray();
            for (Object object : imObjects) {
                row = inflater.inflate(R.layout.im_table_row, imTable, false);

                IM im = (IM) object;
                populateIM(im, row);

                int count = imTable.getChildCount();
                if(count < 2) {

                } else if(count == 2) {
                    imTable.addView(row, 1);
                } else if(count > 2) {
                    imTable.addView(row, count - 1);
                }
            }
        }
    }

    private void addSelectedFields(View view) {
        View row = (View) view.getParent();
        ViewGroup container = ((ViewGroup)row.getParent());

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        CheckBox organization_cb = (CheckBox) container.findViewById(R.id.organization_cb);
        if(organization_cb.isChecked()) {
            TableLayout organizationTable =
                    (TableLayout) findViewById(R.id.organization_tableLayout);

            if(organizationTable.getChildCount() == 0) {
                row = inflater.inflate(R.layout.horizontal_divider_table_row, organizationTable, false);
                organizationTable.addView(row, 0);

                row = inflater.inflate(R.layout.organization_table_job_row, organizationTable, false);
                organizationTable.addView(row, 0);

                row = inflater.inflate(R.layout.organization_table_company_row, organizationTable, false);
                organizationTable.addView(row, 0);

                row = inflater.inflate(R.layout.organization_table_header, organizationTable, false);
                organizationTable.addView(row, 0);
            }
        }

        CheckBox im_cb = (CheckBox) container.findViewById(R.id.im_cb);
        if(im_cb.isChecked()) {
            TableLayout imTable =
                    (TableLayout) findViewById(R.id.im_tableLayout);

            if(imTable.getChildCount() == 0) {

                row = inflater.inflate(R.layout.im_table_header, imTable, false);

                ImageView add_im = (ImageView) row.findViewById(R.id.add_im);
                if(add_im != null) {
                    add_im.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addIM();
                        }
                    });
                }
                imTable.addView(row);

                row = inflater.inflate(R.layout.im_table_row, imTable, false);

                Spinner imType = (Spinner) row.findViewById(R.id.spinner_im_types);
                ArrayAdapter<IMType> imTypeArrayAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, IMType.values());
                imTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                imType.setAdapter(imTypeArrayAdapter);

                ImageView remove_im = (ImageView) row.findViewById(R.id.remove_im);
                remove_im.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeField(view);
                    }
                });
                imTable.addView(row);

                row = inflater.inflate(R.layout.horizontal_divider_table_row, imTable, false);
                imTable.addView(row);
            }
        }

        CheckBox address_cb = (CheckBox) container.findViewById(R.id.address_cb);
        if(address_cb.isChecked()) {
            TableLayout addressTable =
                    (TableLayout) findViewById(R.id.address_tableLayout);

            if(addressTable.getChildCount() == 0) {
                row = inflater.inflate(R.layout.table_header_with_action, addressTable, false);

                TextView title = (TextView) row.findViewById(R.id.field_name);
                title.setText(getResources().getString(R.string.address));

                ImageView add = (ImageView) row.findViewById(R.id.add);
                if(add != null) {
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addAddress();
                        }
                    });
                }
                addressTable.addView(row);

                row = inflater.inflate(R.layout.table_row_with_action, addressTable, false);

                Spinner addressType = (Spinner) row.findViewById(R.id.spinner);
                ArrayAdapter<AddressType> addressTypeArrayAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, AddressType.values());
                addressTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                addressType.setAdapter(addressTypeArrayAdapter);

                EditText value = (EditText) row.findViewById(R.id.value);
                value.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);

                addressTable.addView(row);

                row = inflater.inflate(R.layout.horizontal_divider_table_row, addressTable, false);
                addressTable.addView(row);
            }
        }

        CheckBox notes_cb = (CheckBox) container.findViewById(R.id.notes_cb);
        if(notes_cb.isChecked()) {
            addNotes();
        }

        CheckBox nickname_cb = (CheckBox) container.findViewById(R.id.nickname_cb);
        if(nickname_cb.isChecked()) {
            addNickname();
        }

        CheckBox website_cb = (CheckBox) container.findViewById(R.id.website_cb);
        if(website_cb.isChecked()) {
            TableLayout websiteTable =
                    (TableLayout) findViewById(R.id.website_tableLayout);

            if(websiteTable.getChildCount() == 0) {
                if(websiteTable.getChildCount() == 0) {
                    row = inflater.inflate(R.layout.website_table_header, websiteTable, false);

                    ImageView add = (ImageView) row.findViewById(R.id.add);
                    if(add != null) {
                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addWebsite();
                            }
                        });
                    }
                    websiteTable.addView(row);

                    row = inflater.inflate(R.layout.website_table_row, websiteTable, false);
                    websiteTable.addView(row);

                    row = inflater.inflate(R.layout.horizontal_divider_table_row, websiteTable, false);
                    websiteTable.addView(row);
                }
            }
        }
    }

    private void addAddress() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TableLayout addressTable = (TableLayout) findViewById(R.id.address_tableLayout);
        if(addressTable != null) {
            View row = inflater.inflate(R.layout.table_row_with_action, addressTable, false);

            Spinner addressType = (Spinner) row.findViewById(R.id.spinner);
            ArrayAdapter<AddressType> addressTypeArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, AddressType.values());
            addressTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            addressType.setAdapter(addressTypeArrayAdapter);

            int count = addressTable.getChildCount();

            if(count < 2) {

            } else if(count == 2) {
                addressTable.addView(row, 1);
            } else if(count > 2) {
                addressTable.addView(row, count - 1);
            }
        }
    }

    private void addWebsite() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TableLayout websiteTable = (TableLayout) findViewById(R.id.website_tableLayout);
        if(websiteTable != null) {
            View row = inflater.inflate(R.layout.website_table_row, websiteTable, false);

            int count = websiteTable.getChildCount();
            if(count < 2) {

            } else if(count == 2) {
                websiteTable.addView(row, 1);
            } else if(count > 2) {
                websiteTable.addView(row, count - 1);
            }
        }
    }

    private void addNickname() {
        TableLayout nicknameTable =
                (TableLayout) findViewById(R.id.nickname_tableLayout);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int count = nicknameTable.getChildCount();
        if(count == 0) {
            View row = inflater.inflate(R.layout.nickname_table_header, nicknameTable, false);
            nicknameTable.addView(row);

            row = inflater.inflate(R.layout.nickname_table_row, nicknameTable, false);
            nicknameTable.addView(row);

            row = inflater.inflate(R.layout.horizontal_divider_table_row, nicknameTable, false);
            nicknameTable.addView(row);
        } else if(count == 2) {
            View row = inflater.inflate(R.layout.nickname_table_row, nicknameTable, false);
            nicknameTable.addView(row, 1);
        }
    }

    private void addNotes() {
        TableLayout notesTable =
                (TableLayout) findViewById(R.id.notes_tableLayout);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int count = notesTable.getChildCount();
        if(count == 0) {
            View row = inflater.inflate(R.layout.notes_table_header, notesTable, false);
            notesTable.addView(row);

            row = inflater.inflate(R.layout.notes_table_row, notesTable, false);
            notesTable.addView(row);

            row = inflater.inflate(R.layout.horizontal_divider_table_row, notesTable, false);
            notesTable.addView(row);
        } else if(count == 2) {
            View row = inflater.inflate(R.layout.notes_table_row, notesTable, false);
            notesTable.addView(row, 1);
        }
    }
}
