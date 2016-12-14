package com.example.vavadive.contactmanager.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.vavadive.contactmanager.R;
import com.example.vavadive.contactmanager.db.Contact;
import com.example.vavadive.contactmanager.db.DatabaseHelper;
import com.example.vavadive.contactmanager.db.Phone;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by vavadive on 7/12/2016.
 */
public class ContactAdapter extends CursorAdapter {

    private DatabaseHelper databaseHelper;

    public ContactAdapter(Context context, Cursor c, int flags, DatabaseHelper databaseHelper) {
        super(context, c, flags);
        this.databaseHelper = databaseHelper;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.contacts_view, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.nameView);
        TextView numberView = (TextView) view.findViewById(R.id.numberView);

        Long _id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        try {
            Contact contact = databaseHelper.getContactDao().queryForId(_id);

            StringBuilder name = new StringBuilder();
            name.append(contact.getFirstName());
            String middleName = contact.getMiddleName();
            if(middleName != null && !middleName.equals(""))
            {
                name.append(" ").append(middleName);
            }
            name.append(" ").append(contact.getLastName());

            nameView.setText(name);

            Iterator<Phone> itr = contact.getPhones().iterator();
            if(itr.hasNext())
            {
                Phone phone = itr.next();
                numberView.setText(phone.getPhone());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
