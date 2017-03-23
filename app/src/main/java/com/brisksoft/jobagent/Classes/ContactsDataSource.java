package com.brisksoft.jobagent.Classes;

/**
 * Created by usexbrwe on 12/8/13.
 */

        import java.util.ArrayList;
import java.util.List;

        import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ContactsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_CONTACT,
            SQLiteHelper.COLUMN_TITLE,
            SQLiteHelper.COLUMN_PHONE,
            SQLiteHelper.COLUMN_EMAIL };

    public ContactsDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Contact createContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_CONTACT, contact.getContact());
        values.put(SQLiteHelper.COLUMN_TITLE, contact.getTitle());
        values.put(SQLiteHelper.COLUMN_PHONE, contact.getPhone());
        values.put(SQLiteHelper.COLUMN_EMAIL, contact.getEmail());
        long contactId = database.insert(SQLiteHelper.TABLE_CONTACTS, null,
                values);
        
    	// just get job data by default
    	String selectQuery = "SELECT * FROM " +SQLiteHelper.TABLE_CONTACTS+ " WHERE " +SQLiteHelper.COLUMN_ID+ " = " + contactId;

    	if (contact.getCompany() != null && !contact.getCompany().isEmpty()) {
            System.out.println("Create contact+company "+contact.getCompany());
            // insert record into JOB_COMPANY table
            long companyId = createContactCompany(contactId, contact.getCompany());
        	// get contact & company data
            selectQuery = "SELECT * FROM " +SQLiteHelper.TABLE_CONTACTS+ " c left outer join " +SQLiteHelper.TABLE_COMPANY_CONTACT+
            		" cc on c._id = cc.contact_id left outer join " + SQLiteHelper.TABLE_COMPANIES + " co on co._id = cc.company_id  where c._id = " + contactId + "";
    	} 

        System.out.println("SQL query = "+selectQuery);
		
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Contact newContact = cursorToContact(cursor);
        cursor.close();
        return newContact;
    }

    long createContactCompany(long contactId, String company) {
    	
    	// Get company ID
    	// should check if company exists
    	// add logic to check for similar names
        String selectQuery = "SELECT  * FROM " +SQLiteHelper.TABLE_COMPANIES+ " WHERE company = '" + company + "'";          
        Cursor cursor = database.rawQuery(selectQuery, null);     
        if (cursor != null)
            cursor.moveToFirst();
         
        long companyId;
        assert cursor != null;
        if (cursor.getCount() > 0) {
        	// company exists
        	companyId = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_ID));
        } else {
        	// insert new company in to COMPANIES table
            ContentValues valuesCo = new ContentValues();
            valuesCo.put(SQLiteHelper.COLUMN_COMPANY, company);     
            companyId = database.insert(SQLiteHelper.TABLE_COMPANIES, null, valuesCo);        	
        }

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_CONTACT_ID, contactId);
        values.put(SQLiteHelper.COLUMN_COMPANY_ID, companyId);
 
        return database.insert(SQLiteHelper.TABLE_COMPANY_CONTACT, null, values);
    }

    public void deleteContact(long contactId) {
        System.out.println("Contact deleted with id: " + contactId);
        database.delete(SQLiteHelper.TABLE_CONTACTS, SQLiteHelper.COLUMN_ID
                + " = " + contactId, null);
        // delete from job_company table
        database.delete(SQLiteHelper.TABLE_COMPANY_CONTACT, SQLiteHelper.COLUMN_CONTACT_ID
                + " = " + contactId, null);
        // delete from job_contact table
        database.delete(SQLiteHelper.TABLE_JOB_CONTACT, SQLiteHelper.COLUMN_CONTACT_ID
                + " = " + contactId, null);
    }

    public void updateContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_CONTACT, contact.getContact());
        values.put(SQLiteHelper.COLUMN_TITLE, contact.getTitle());
        values.put(SQLiteHelper.COLUMN_PHONE, contact.getPhone());
        values.put(SQLiteHelper.COLUMN_EMAIL, contact.getEmail());

        createContactCompany(contact.getId(), contact.getCompany()); // update Company & Contact_Company tables

        database.update(SQLiteHelper.TABLE_CONTACTS, values, SQLiteHelper.COLUMN_ID + " = " + contact.getId(), null);
    }

    public List<Contact> getAllContacts() {
        List<Contact> Contacts = new ArrayList<Contact>();

        String selectQuery = "SELECT * FROM " +SQLiteHelper.TABLE_CONTACTS+ " c left outer join " +SQLiteHelper.TABLE_COMPANY_CONTACT+
        		" cc on c._id = cc.contact_id left outer join " + SQLiteHelper.TABLE_COMPANIES + " co on co._id = cc.company_id";
        
        Cursor cursor = database.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Contact Contact = cursorToContact(cursor);
            Contacts.add(Contact);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return Contacts;
    }


    private Contact cursorToContact(Cursor cursor) {
    	Contact Contact = new Contact();
        Contact.setId(cursor.getLong(0));
    	Contact.setContact(cursor.getString(1));
    	Contact.setTitle(cursor.getString(2));
    	Contact.setPhone(cursor.getString(3));
    	Contact.setEmail(cursor.getString(4));
    	
        // if company info returned, set that as well
        if (cursor.getColumnCount() > 5) {
            System.out.println("Company name : " + cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_COMPANY)));
        	Contact.setCompany(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_COMPANY)));
        }

        return Contact;
    }
} 
