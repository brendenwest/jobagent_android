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

public class TasksDataSource {

    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;
    private final String[] allColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_TITLE,
            SQLiteHelper.COLUMN_DATE,
            SQLiteHelper.COLUMN_STATUS,
            SQLiteHelper.COLUMN_PRIORITY,
            SQLiteHelper.COLUMN_DESCRIPTION,
            SQLiteHelper.COLUMN_COMPANY,
            SQLiteHelper.COLUMN_CONTACT,
            SQLiteHelper.COLUMN_JOB };

    public TasksDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long createTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TITLE, task.getTitle());
        values.put(SQLiteHelper.COLUMN_DATE, task.getDate());
        values.put(SQLiteHelper.COLUMN_STATUS, task.getStatus());
        values.put(SQLiteHelper.COLUMN_PRIORITY, task.getPriority());
        values.put(SQLiteHelper.COLUMN_DESCRIPTION, task.getDescription());
        values.put(SQLiteHelper.COLUMN_COMPANY, task.getCompany());
        values.put(SQLiteHelper.COLUMN_CONTACT, task.getContact());
        values.put(SQLiteHelper.COLUMN_JOB, task.getJob());
        long insertId = database.insert(SQLiteHelper.TABLE_TASKS, null,
                values);
        
        // populate Company, Contact or Job tables
    	if (task.getCompany() != null && !task.getCompany().isEmpty()) {
            createCompany(task.getCompany());
    	}
    	if (task.getContact() != null && !task.getContact().isEmpty()) {
            createContact(task.getContact());
    	}
    	if (task.getJob() != null && !task.getJob().isEmpty()) {
            createJob(task.getJob());
    	}

    	return insertId;
    }

    public void deleteTask(long taskId) {
        System.out.println("Task deleted with title: " + taskId);
        database.delete(SQLiteHelper.TABLE_TASKS, SQLiteHelper.COLUMN_ID
                + " = " + taskId, null);
    }

    public void updateTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TITLE, task.getTitle());
        values.put(SQLiteHelper.COLUMN_DATE, task.getDate());
        values.put(SQLiteHelper.COLUMN_STATUS, task.getStatus());
        values.put(SQLiteHelper.COLUMN_PRIORITY, task.getPriority());
        values.put(SQLiteHelper.COLUMN_DESCRIPTION, task.getDescription());
        values.put(SQLiteHelper.COLUMN_COMPANY, task.getCompany());
        values.put(SQLiteHelper.COLUMN_CONTACT, task.getContact());
        values.put(SQLiteHelper.COLUMN_JOB, task.getJob());

        // populate Company, Contact or Job tables
    	if (task.getCompany() != null && !task.getCompany().isEmpty()) {
            createCompany(task.getCompany());
    	}
    	if (task.getContact() != null && !task.getContact().isEmpty()) {
            createContact(task.getContact());
    	}
    	if (task.getJob() != null && !task.getJob().isEmpty()) {
            createJob(task.getJob());
    	}

         database.update(SQLiteHelper.TABLE_TASKS, values, null, null);
    }

    long createCompany(String item) {
    	
    	// Get company ID
    	// should check if company exists
    	// add logic to check for similar names
        String selectQuery = "SELECT  * FROM " +SQLiteHelper.TABLE_COMPANIES+ " WHERE company = '" + item + "'";          
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
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.COLUMN_COMPANY, item);     
            companyId = database.insert(SQLiteHelper.TABLE_COMPANIES, null, values);        	
        }

        return companyId;
    }
    long createJob(String item) {
    	
    	// Get item ID
    	// should check if item exists
    	// add logic to check for similar names
        String selectQuery = "SELECT  * FROM " +SQLiteHelper.TABLE_JOBS+ " WHERE title = '" + item + "'";          
        Cursor cursor = database.rawQuery(selectQuery, null);     
        if (cursor != null)
            cursor.moveToFirst();
         
        long itemId;
        if (cursor.getCount() > 0) {
        	// company exists
        	itemId = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_ID));
        } else {
        	// insert new company in to COMPANIES table
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.COLUMN_JOB, item);     
            itemId = database.insert(SQLiteHelper.TABLE_JOBS, null, values);        	
        }

        return itemId;
    }

   long createContact(String item) {
    	
    	// Get item ID
    	// should check if item exists
    	// add logic to check for similar names
	   
        String selectQuery = "SELECT  * FROM " +SQLiteHelper.TABLE_CONTACTS+ " WHERE contact = '" + item + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);     
        if (cursor != null)
            cursor.moveToFirst();
         
        long itemId;
       assert cursor != null;
       if (cursor.getCount() > 0) {
        	// task exists
        	itemId = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_ID));
        } else {
        	// insert new contact in to Contacts table
            ContentValues values = new ContentValues();
            // TODO : check for missing first or last name
            values.put(SQLiteHelper.COLUMN_CONTACT, item);     
            itemId = database.insert(SQLiteHelper.TABLE_CONTACTS, null, values);        	
        }

        return itemId;
    }

    public List<Task> getAllTasks() {
        List<Task> Tasks = new ArrayList<Task>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_TASKS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task Task = cursorToTask(cursor);
            Tasks.add(Task);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return Tasks;
    }


    private Task cursorToTask(Cursor cursor) {
    	Task Task = new Task();
        Task.setTitle(cursor.getString(1));
    	Task.setDate(cursor.getString(2));
    	Task.setStatus(cursor.getString(3));
    	Task.setPriority(cursor.getString(4));
    	Task.setDescription(cursor.getString(5));
    	Task.setCompany(cursor.getString(6));
    	Task.setContact(cursor.getString(7));
    	Task.setJob(cursor.getString(8));
        return Task;
    }
} 
