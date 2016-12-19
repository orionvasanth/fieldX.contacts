package com.example.vavadive.contactmanager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vavadive.contactmanager.adapter.ContactAdapter;
import com.example.vavadive.contactmanager.common.ContactContextMenuItem;
import com.example.vavadive.contactmanager.common.Mode;
import com.example.vavadive.contactmanager.db.Contact;
import com.example.vavadive.contactmanager.db.DatabaseHelper;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;
    private ListView contacts_list;
    private ContactAdapter contactAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        databaseHelper = new DatabaseHelper(this);

        addListener();
        setAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        /*final Intent addContact = new Intent(this, AddContactActivity.class);
        MenuItem search = menu.findItem(R.id.add_contact);
        search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivityForResult(addContact, REQUEST_ADD);
                return true;
            }
        });*/
        return true;
    }

    public void addListener() {
        final Intent addContact = new Intent(this, AddContactActivity.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                addContact.putExtra("mode", Mode.CREATE.ordinal());
                startActivityForResult(addContact, REQUEST_ADD);
            }
        });
    }

    private Cursor getCursor() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor contact_cursor = db.rawQuery("SELECT * from contact ORDER BY firstName ASC", null);
        return contact_cursor;
    }

    private void setAdapter() {
        contacts_list = (ListView) findViewById(R.id.contactslistView);

        contactAdapter = new ContactAdapter(this, getCursor(), 0, databaseHelper);
        contacts_list.setAdapter(contactAdapter);
        registerForContextMenu(contacts_list);

        final Intent viewContact = new Intent(this, ViewContactActivity.class);
        contacts_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewContact.putExtra(getResources().getString(R.string.key), l);
                viewContact.putExtra("mode", Mode.VIEW.ordinal());
                startActivityForResult(viewContact, REQUEST_EDIT);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                    ContextMenu.ContextMenuInfo contextMenuInfo) {
        if(view.getId() == R.id.contactslistView) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
            try {
                Contact contact = databaseHelper.getContactDao().queryForId(info.id);
                contextMenu.setHeaderTitle(contact.getFirstName());

                ContactContextMenuItem[] values = ContactContextMenuItem.values();
                for(int i = 0; i < values.length; i++) {
                    contextMenu.add(Menu.NONE, 0, 0, values[i].getDisplayedName());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (ContactContextMenuItem.valueOf(item.getTitle().toString())) {
            case Delete:
                try {
                    databaseHelper.getContactDao().deleteById(info.id);
                    refresh();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case Edit:
                Intent editContact = new Intent(this, EditContactActivity.class);
                editContact.putExtra(getResources().getString(R.string.key), info.id);
                editContact.putExtra("mode", Mode.EDIT.ordinal());
                startActivityForResult(editContact, REQUEST_EDIT);

                break;
        }

        return true;
    }

    private void displayToast(int resultCode) {
        if(resultCode == RESULT_OK) {
            Toast.makeText(this, getResources().getString(R.string.save_success),
                    Toast.LENGTH_SHORT).show();
        }
        else if(resultCode == RESULT_CANCELED) {
            Toast.makeText(this, getResources().getString(R.string.cancelled),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        displayToast(resultCode);
    }

    private void refresh() {
        contactAdapter.changeCursor(getCursor());
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
    }
}
