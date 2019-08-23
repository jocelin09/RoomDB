package com.example.jocelin.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.jocelin.roomdb.entity.Contacts;

import java.util.List;

@Dao
public interface ContactDao {

    //when new row is added room returns the primary key and since primary key
    // data type is long so return type of this method is set to long
    @Insert
    public long addContacts(Contacts contacts);

    @Update
    public void updateContact(Contacts contacts);

    @Delete
    public void deleteContact(Contacts contacts);

    @Query("Select * from contacts")
    public List<Contacts> getAllContacts();

    @Query("Select * from contacts where contact_id ==:id")
    public Contacts getAllContactsById(long id);
}
