package com.example.vavadive.contactmanager;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.vavadive.contactmanager.adapter.ContactAdapter;
import com.example.vavadive.contactmanager.db.Contact;
import com.example.vavadive.contactmanager.db.DatabaseHelper;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by vavadive on 12/12/2016.
 */
public class SearchableActivity extends AppCompatActivity {

    public static final int REQUEST_EDIT = 2;
    private ListView contacts_list;
    private ContactAdapter contactAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        databaseHelper = new DatabaseHelper(this);

        setAdapter();
    }

    private void setAdapter() {
        contacts_list = (ListView) findViewById(R.id.contactslistView);

        contactAdapter = new ContactAdapter(this, getCursor(), 0, databaseHelper);
        contacts_list.setAdapter(contactAdapter);
        registerForContextMenu(contacts_list);

        final Intent editContact = new Intent(this, EditContactActivity.class);
        contacts_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editContact.putExtra(getResources().getString(R.string.key), l);
                startActivityForResult(editContact, REQUEST_EDIT);
            }
        });
    }

    private Cursor getCursor() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            try {
                final QueryBuilder<Contact, Long> queryBuilder =
                        databaseHelper.getContactDao().queryBuilder();
                queryBuilder.where().like("firstName", query + "%");
                queryBuilder.orderBy("firstName", true);
                String raw_query = queryBuilder.prepareStatementString();

                Cursor contact_cursor = db.rawQuery(raw_query, null);
                return contact_cursor;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Cursor contact_cursor = db.rawQuery("SELECT * from contact", null);
        return contact_cursor;
    }
}
