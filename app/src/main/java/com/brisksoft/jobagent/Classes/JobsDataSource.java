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
import android.util.Log;

public class JobsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_TITLE,
            SQLiteHelper.COLUMN_DESCRIPTION,
            SQLiteHelper.COLUMN_LINK,
            SQLiteHelper.COLUMN_TYPE,
            SQLiteHelper.COLUMN_DATE, 
            SQLiteHelper.COLUMN_STATUS, 
            SQLiteHelper.COLUMN_PAY, 
            SQLiteHelper.COLUMN_LOCATION };

    public JobsDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Job createJob(Job job) {
        System.out.println("Job created");
        // insert values into Jobs table
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TITLE, job.getTitle());
        values.put(SQLiteHelper.COLUMN_DESCRIPTION, job.getDescription());
        values.put(SQLiteHelper.COLUMN_LINK, job.getLink());
        values.put(SQLiteHelper.COLUMN_LOCATION, job.getLocation());
        values.put(SQLiteHelper.COLUMN_TYPE, job.getType());
        values.put(SQLiteHelper.COLUMN_DATE, job.getDate());
        values.put(SQLiteHelper.COLUMN_STATUS, job.getStatus());
        values.put(SQLiteHelper.COLUMN_PAY, job.getPay());

    	long jobId = database.insert(SQLiteHelper.TABLE_JOBS, null, values);

    	// default query to get job data w/o company or contact
    	String selectQuery = "SELECT * FROM " +SQLiteHelper.TABLE_JOBS+ " WHERE " +SQLiteHelper.COLUMN_ID+ " = " + jobId;

    	if (job.getCompany() != null && !job.getCompany().isEmpty()) {
            System.out.println("Create job+company "+job.getCompany());
            // insert record into JOB_COMPANY table
            long companyId = createJobCompany(jobId, job.getCompany());
        	// get job data with associated company and contact names
            selectQuery = "SELECT * FROM " +SQLiteHelper.TABLE_JOBS+ " j left outer join " +SQLiteHelper.TABLE_JOB_COMPANY+
            		" jc on j._id = jc.job_id left outer join " + SQLiteHelper.TABLE_COMPANIES + " co on co._id = jc.company_id left outer join " +SQLiteHelper.TABLE_JOB_CONTACT + " jp on j._id = jp.job_id left outer join " + SQLiteHelper.TABLE_CONTACTS + 
            		" person on person._id = jp.contact_id  where j._id = " + jobId +"";
    	} 

        System.out.println("SQL query = "+selectQuery);
		
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Job newJob = cursorToJob(cursor);
        
        cursor.close();
        return newJob;
    }

    
    long createJobCompany(long jobId, String company) {
    	
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
        values.put(SQLiteHelper.COLUMN_JOB_ID, jobId);
        values.put(SQLiteHelper.COLUMN_COMPANY_ID, companyId);
 
        return database.insert(SQLiteHelper.TABLE_JOB_COMPANY, null, values);

    }

    void createJobContact(long jobId, String contact) {
    	
    	// Get contact ID
        String selectQuery = "SELECT  * FROM " +SQLiteHelper.TABLE_CONTACTS+ " WHERE contact = '" + contact + "'";          
        Cursor cursor = database.rawQuery(selectQuery, null);     
        if (cursor != null)
            cursor.moveToFirst();
         
        long contactId;
        assert cursor != null;
        if (cursor.getCount() > 0) {
        	// contact exists
        	contactId = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_ID));
        } else {
        	// insert new contact in to CONTACTS table
            ContentValues valuesCo = new ContentValues();
            valuesCo.put(SQLiteHelper.COLUMN_CONTACT, contact);     
            contactId = database.insert(SQLiteHelper.TABLE_CONTACTS, null, valuesCo);        	
        }

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_JOB_ID, jobId);
        values.put(SQLiteHelper.COLUMN_CONTACT_ID, contactId);
 
        database.insert(SQLiteHelper.TABLE_JOB_CONTACT, null, values);
    }
    
    public void deleteJob(long jobId) {
        // delete from jobs table
        database.delete(SQLiteHelper.TABLE_JOBS, SQLiteHelper.COLUMN_ID
                + " = " + jobId, null);
        // delete from job_company table
        database.delete(SQLiteHelper.TABLE_JOB_COMPANY, SQLiteHelper.COLUMN_JOB_ID
                + " = " + jobId, null);
        // delete from job_contact table
        database.delete(SQLiteHelper.TABLE_JOB_CONTACT, SQLiteHelper.COLUMN_JOB_ID
                + " = " + jobId, null);
    }


    public void updateJob(Job job) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TITLE, job.getTitle());
        values.put(SQLiteHelper.COLUMN_DESCRIPTION, job.getDescription());
        values.put(SQLiteHelper.COLUMN_LINK, job.getLink());
        values.put(SQLiteHelper.COLUMN_LOCATION, job.getLocation());
        values.put(SQLiteHelper.COLUMN_TYPE, job.getType());        
        values.put(SQLiteHelper.COLUMN_DATE, job.getDate());
        values.put(SQLiteHelper.COLUMN_STATUS, job.getStatus());
        values.put(SQLiteHelper.COLUMN_PAY, job.getPay());

    	if (job.getCompany() != null && !job.getCompany().isEmpty()) {
    		createJobCompany(job.getId(), job.getCompany()); // update Company & Job_Company tables
    	}
    	if (job.getContact() != null && !job.getContact().isEmpty()) {    	
            Log.d("job", "save contact = "+job.getContact());
    		createJobContact(job.getId(), job.getContact()); // update Contact & Job_Contact tables
    	}
        
        database.update(SQLiteHelper.TABLE_JOBS, values, SQLiteHelper.COLUMN_ID + " = " + job.getId(), null);
    }

    public List<Job> getAllJobs() {
        List<Job> Jobs = new ArrayList<Job>();

        String selectQuery = "SELECT * FROM " +SQLiteHelper.TABLE_JOBS+ " j left outer join " +SQLiteHelper.TABLE_JOB_COMPANY+
        		" jc on j._id = jc.job_id left outer join " + SQLiteHelper.TABLE_COMPANIES + " co on co._id = jc.company_id left outer join "
        		+SQLiteHelper.TABLE_JOB_CONTACT + " jp on j._id = jp.job_id left outer join " + SQLiteHelper.TABLE_CONTACTS + 
        		" people on people._id = jp.contact_id";

        Cursor cursor = database.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Job Job = cursorToJob(cursor);
            Jobs.add(Job);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return Jobs;
    }


    private Job cursorToJob(Cursor cursor) {
        Job Job = new Job();
        Job.setId(cursor.getLong(0));
        Job.setTitle(cursor.getString(1));
        Job.setDescription(cursor.getString(2));
        Job.setLink(cursor.getString(3));
        Job.setLocation(cursor.getString(4));
        Job.setType(cursor.getString(5));
        Job.setDate(cursor.getString(6));        
        Job.setStatus(cursor.getString(7));
        Job.setPay(cursor.getString(8));

        // if company info returned, set that as well
        if (cursor.getColumnCount() > 10) {
            String company = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_COMPANY));
            if (company != null) {
            	Job.setCompany(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_COMPANY)));
            }
            String contact = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_CONTACT));
            if (contact != null) {
            	Job.setContact(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_CONTACT)));
            }
        }

        return Job;
    }
} 
