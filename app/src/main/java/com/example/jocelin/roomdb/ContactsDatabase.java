package com.example.jocelin.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.jocelin.roomdb.entity.Contacts;

@Database(entities = {Contacts.class},version =  1)
public abstract class ContactsDatabase extends RoomDatabase {

    public abstract ContactDao getContactsDao();

}
