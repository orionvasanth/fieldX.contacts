package com.example.vavadive.contactmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

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
import com.example.vavadive.contactmanager.util.StringUtil;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

/**
 * Created by vavadive on 6/21/2016.
 */
public class AddContactActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseHelper databaseHelper;

    private Contact save_contact = new Contact();

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

        addListener();
    }

    private void saveAddress(Contact contact) {
        Spinner addressType = (Spinner) findViewById(R.id.spinner_address_types);
        EditText address = (EditText) findViewById(R.id.editText_address);
        if(addressType != null && address != null) {
            if (!StringUtil.isNull(address.getText().toString())) {
                try {
                    databaseHelper.getAddressDao().delete(contact.getAddresses());

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

    private void saveIMs(Contact contact) {
        Collection<IM> ims = contact.getIms();

        try {
            databaseHelper.getImDao().delete(ims);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TableLayout imTable = (TableLayout) findViewById(R.id.im_tableLayout);
        if(imTable != null && imTable.getChildCount() != 0) {
            int count = imTable.getChildCount();

            for(int i = 1; i < count; i++) {
                View imRow = imTable.getChildAt(i);
                Spinner imType = (Spinner) imRow.findViewById(R.id.spinner_im_types);
                EditText imText = (EditText) imRow.findViewById(R.id.im);

                if(imType != null && imText != null) {
                    String id = imText.getText().toString();
                    if(!StringUtil.isNull(id)) {
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

    private void save() {

        Spinner contactType = (Spinner)findViewById(R.id.spinner_contact_types);
        if(contactType != null) {
            save_contact.setContactType((ContactType) contactType.getSelectedItem());
        }

        EditText company = (EditText) findViewById(R.id.company);
        if(company != null) {
            save_contact.setCompany(company.getText().toString());
        }

        EditText job = (EditText) findViewById(R.id.job);
        if(job != null) {
            save_contact.setJobTitle(job.getText().toString());
        }

        EditText nickname = (EditText) findViewById(R.id.nickname);
        if(nickname != null) {
            save_contact.setNickname(nickname.getText().toString());
        }

        EditText notes = (EditText) findViewById(R.id.notes);
        if(notes != null) {
            save_contact.setNotes(notes.getText().toString());
        }

        EditText website = (EditText) findViewById(R.id.website);
        if(website != null) {
            save_contact.setWebsite(website.getText().toString());
        }

        save_contact.setLastModified(new Date().getTime());

        if (validateCreation(save_contact)) {
            try {
                save_contact = databaseHelper.getContactDao().createIfNotExists(save_contact);

                savePhones(save_contact);
                saveEmails(save_contact);
                saveIMs(save_contact);
                saveAddress(save_contact);

                setResult(RESULT_OK);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.cancelled),
                    Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private boolean validateCreation(Contact contact) {
        return !StringUtil.isNull(contact.getFirstName());
    }

    public void addListener() {
        final Button add_field_button = (Button) findViewById(R.id.add_field);

        if(add_field_button != null) {
            add_field_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater inflater =
                            (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View popup = inflater.inflate(R.layout.add_another_field_popup, null);

                    final PopupWindow add_another_field_popup = new PopupWindow(
                            popup,
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT
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

        final EditText name = (EditText) findViewById(R.id.editText_name);
        if(name != null) {
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

        if(!StringUtil.isNull(fullName)) {
            String[] names = fullName.split(" ");
            if (names.length < 3) {
                switch (names.length) {
                    case 1:
                        save_contact.setFirstName(names[0]);
                        save_contact.setMiddleName("");
                        save_contact.setLastName("");
                        break;

                    case 2:
                        save_contact.setFirstName(names[0]);
                        save_contact.setLastName(names[1]);
                        save_contact.setMiddleName("");

                        break;
                }
            } else if (names.length == 3) {
                save_contact.setFirstName(names[0]);
                save_contact.setMiddleName(names[1]);
                save_contact.setLastName(names[2]);
            } else if (names.length > 3) {
                save_contact.setLastName(names[names.length - 1]);
                save_contact.setMiddleName(names[names.length - 2]);

                StringBuilder firstName = new StringBuilder();
                for(int i = 0; i < names.length - 2; i++) {
                    firstName.append(names[i] + " ");
                }
                save_contact.setFirstName(firstName.toString().trim());
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

        if(firstName == null || middleName == null || lastName == null) {
            return;
        }

        firstName.setText(save_contact.getFirstName());
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                save_contact.setFirstName(editable.toString());
            }
        });

        middleName.setText(save_contact.getMiddleName());
        middleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                save_contact.setMiddleName(editable.toString());
            }
        });

        lastName.setText(save_contact.getLastName());
        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                save_contact.setLastName(editable.toString());
            }
        });

        name_linearLayout.addView(name_expanded);
    }

    private void collapseName() {
        StringBuilder fullName = new StringBuilder();
        if(!StringUtil.isNull(save_contact.getFirstName())) {
            fullName.append(save_contact.getFirstName() + " ");
        }

        if(!StringUtil.isNull(save_contact.getMiddleName())) {
            fullName.append(save_contact.getMiddleName() + " ");
        }

        if(!StringUtil.isNull(save_contact.getLastName())) {
            fullName.append(save_contact.getLastName());
        }

        LinearLayout name_linearLayout =
                (LinearLayout) findViewById(R.id.name_linearLayout);
        name_linearLayout.removeAllViews();

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View name_collapsed = inflater.inflate(R.layout.name_collapsed,
                name_linearLayout, false);
        EditText name = (EditText) name_collapsed.findViewById(R.id.editText_name);
        if(name != null) {
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

    private void addSelectedFields(View view) {
        View row = (View) view.getParent();
        ViewGroup container = ((ViewGroup)row.getParent());

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        CheckBox organization_cb = (CheckBox) container.findViewById(R.id.organization_cb);
        if(organization_cb.isChecked()) {
            TableLayout organizationTable =
                    (TableLayout) findViewById(R.id.organization_tableLayout);

            if(organizationTable.getChildCount() == 0) {
                row = inflater.inflate(R.layout.organization_table_header, organizationTable, false);
                organizationTable.addView(row);

                row = inflater.inflate(R.layout.organization_table_company_row, organizationTable, false);
                organizationTable.addView(row);

                row = inflater.inflate(R.layout.organization_table_job_row, organizationTable, false);
                organizationTable.addView(row);
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
            }
        }

        CheckBox notes_cb = (CheckBox) container.findViewById(R.id.notes_cb);
        if(notes_cb.isChecked()) {
            TableLayout notesTable =
                    (TableLayout) findViewById(R.id.notes_tableLayout);

            if(notesTable.getChildCount() == 0) {
                row = inflater.inflate(R.layout.notes_table_header, notesTable, false);
                notesTable.addView(row);

                row = inflater.inflate(R.layout.notes_table_row, notesTable, false);
                notesTable.addView(row);
            }
        }

        CheckBox nickname_cb = (CheckBox) container.findViewById(R.id.nickname_cb);
        if(nickname_cb.isChecked()) {
            TableLayout nicknameTable =
                    (TableLayout) findViewById(R.id.nickname_tableLayout);

            if(nicknameTable.getChildCount() == 0) {
                row = inflater.inflate(R.layout.nickname_table_header, nicknameTable, false);
                nicknameTable.addView(row);

                row = inflater.inflate(R.layout.nickname_table_row, nicknameTable, false);
                nicknameTable.addView(row);
            }
        }

        CheckBox website_cb = (CheckBox) container.findViewById(R.id.website_cb);
        if(website_cb.isChecked()) {
            TableLayout websiteTable =
                    (TableLayout) findViewById(R.id.website_tableLayout);

            if(websiteTable.getChildCount() == 0) {
                row = inflater.inflate(R.layout.website_table_header, websiteTable, false);
                websiteTable.addView(row);

                row = inflater.inflate(R.layout.website_table_row, websiteTable, false);
                websiteTable.addView(row);
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

            emailTable.addView(row);
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

            imTable.addView(row);
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
                save();

                return true;
            }
        });

        return true;
    }
}
