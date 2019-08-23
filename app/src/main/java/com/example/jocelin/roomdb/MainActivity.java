package com.example.jocelin.roomdb;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.jocelin.roomdb.entity.Contacts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ContactsAdapter contactsAdapter;
    private ArrayList<Contacts> contactArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactsDatabase contactsAppDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" Contacts Manager ");

        recyclerView = findViewById(R.id.recycler_view_contacts);
       // contactsAppDatabase= Room.databaseBuilder(getApplicationContext(),ContactsDatabase.class,"ContactDB").addCallback(callback).build();
        contactsAppDatabase= Room.databaseBuilder(getApplicationContext(),ContactsDatabase.class,"ContactDB").build();

        new getContactsAsyncClass().execute();

        contactsAdapter = new ContactsAdapter(this, contactArrayList, MainActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditContacts(false, null, -1);
            }
        });
    }

    public void addAndEditContacts(final boolean isUpdate,final Contacts contact,final int i) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.layout_add_contact, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        TextView contactTitle = view.findViewById(R.id.new_contact_title);
        final EditText newContact = view.findViewById(R.id.name);
        final EditText contactEmail = view.findViewById(R.id.email);

        contactTitle.setText(!isUpdate ? "Add New Contact" : "Edit Contact");

        if (isUpdate && contact != null) {
            newContact.setText(contact.getName());
            contactEmail.setText(contact.getEmail());
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                                if (isUpdate) {

                                    deleteContact(contact, i);
                                } else {

                                    dialogBox.cancel();

                                }

                            }
                        })

                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(newContact.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter contact name!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }


                if (isUpdate && contact != null) {

                    updateContact(newContact.getText().toString(), contactEmail.getText().toString(), i);
                } else {

                    createContact(newContact.getText().toString(), contactEmail.getText().toString());
                }
            }
        });
    }

    private void updateContact(String name, String email, int pos) {
        Contacts contact = contactArrayList.get(pos);

        contact.setName(name);
        contact.setEmail(email);

       // contactsAppDatabase.getContactsDao().updateContact(contact);
        new updateContactAsyncTask().execute(contact);

        contactArrayList.set(pos, contact);




    }

    private void createContact(String name, String email) {

      /*  long id = contactsAppDatabase.getContactsDao().addContacts(new Contacts(name, email,0));

        Contacts contact = contactsAppDatabase.getContactsDao().getAllContactsById(id);

        if (contact != null) {

            contactArrayList.add(0, contact);
            contactsAdapter.notifyDataSetChanged();

        }*/

      new createContactAsyncTask().execute(new Contacts(name,email,0));
    }

    private void deleteContact(Contacts contact, int position) {
        contactArrayList.remove(position);
        new deleteContactAsyncTask().execute(contact);
       // contactsAppDatabase.getContactsDao().deleteContact(contact);
        //contactsAdapter.notifyDataSetChanged();
    }

    private class getContactsAsyncClass extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            contactArrayList.addAll(contactsAppDatabase.getContactsDao().getAllContacts());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            contactsAdapter.notifyDataSetChanged();

        }

    }


    private class createContactAsyncTask extends AsyncTask<Contacts,Void,Void>
    {

        @Override
        protected Void doInBackground(Contacts... contacts) {
            long id = contactsAppDatabase.getContactsDao().addContacts(contacts[0]);

            Contacts contact = contactsAppDatabase.getContactsDao().getAllContactsById(id);

            if (contact != null) {

                contactArrayList.add(0, contact);


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            contactsAdapter.notifyDataSetChanged();
        }
    }

    private class updateContactAsyncTask extends AsyncTask<Contacts,Void,Void>
    {

        @Override
        protected Void doInBackground(Contacts... contacts) {
            contactsAppDatabase.getContactsDao().updateContact(contacts[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactsAdapter.notifyDataSetChanged();

        }
    }

    private class deleteContactAsyncTask extends AsyncTask<Contacts,Void,Void>
    {

        @Override
        protected Void doInBackground(Contacts... contacts) {
            contactsAppDatabase.getContactsDao().deleteContact(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            contactsAdapter.notifyDataSetChanged();
        }
    }

    RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            System.out.println("onCreate called");
//            createContact("jos","jos@gmail.com");
//            createContact("jos1","jos1@gmail.com");
//            createContact("jos2","jos2@gmail.com");
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            System.out.println("onOpen called");
        }
    };
}
