package com.brisksoft.jobagent.Classes;

/**
 * Created by usexbrwe on 12/8/13.
 */

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

class SQLiteHelper extends SQLiteOpenHelper {

	// table names
    public static final String TABLE_JOBS = "jobs";
    public static final String TABLE_COMPANIES = "companies";
    public static final String TABLE_CONTACTS = "contacts";
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_JOB_COMPANY = "job_company";
    public static final String TABLE_JOB_CONTACT = "job_contact";
    public static final String TABLE_COMPANY_CONTACT = "company_contact";

	// Common column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PAY = "pay";

    public static final String COLUMN_JOB_ID = "job_id";
    public static final String COLUMN_COMPANY_ID = "company_id";
    public static final String COLUMN_CONTACT_ID = "contact_id";

    public static final String COLUMN_COMPANY = "company";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_JOB = "job";
    public static final String COLUMN_DUE = "due";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_PRIORITY = "priority";

    private static final String DATABASE_NAME = "jobagent.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statements
    private static final String DATABASE_CREATE_JOBS = "create table "
            + TABLE_JOBS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null,"  + COLUMN_DESCRIPTION
            + " text," + COLUMN_LINK
            + " text," + COLUMN_LOCATION
            + " text," + COLUMN_TYPE
            + " text," + COLUMN_DATE
            + " text," + COLUMN_STATUS
            + " text," + COLUMN_PAY
            + " text); ";
    private static final String DATABASE_CREATE_COMPANIES = "create table "
            +  TABLE_COMPANIES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_COMPANY
            + " text not null,"  + COLUMN_DESCRIPTION
            + " text," + COLUMN_TYPE
            + " text);";
    private static final String DATABASE_CREATE_JOB_COMPANY = "create table "
            +  TABLE_JOB_COMPANY + "(" + COLUMN_JOB_ID
            + " integer not null,"  + COLUMN_COMPANY_ID
            + " integer not null, UNIQUE("  + COLUMN_JOB_ID
            + ", "  + COLUMN_COMPANY_ID
            + ") ON CONFLICT REPLACE)";
    // CREATE TABLE a (i INT, j INT, UNIQUE(i, j) ON CONFLICT REPLACE);
    private static final String DATABASE_CREATE_CONTACTS = "create table "
            +  TABLE_CONTACTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_CONTACT
            + " text not null,"  + COLUMN_TITLE
            + " text," + COLUMN_PHONE
            + " text," + COLUMN_EMAIL
            + " text);";
    private static final String DATABASE_CREATE_JOB_CONTACT = "create table "
            +  TABLE_JOB_CONTACT + "(" + COLUMN_JOB_ID
            + " integer not null,"  + COLUMN_CONTACT_ID
            + " integer not null, UNIQUE("  + COLUMN_JOB_ID
            + ", "  + COLUMN_CONTACT_ID
            + ") ON CONFLICT REPLACE);";
    private static final String DATABASE_CREATE_COMPANY_CONTACT = "create table "
            +  TABLE_COMPANY_CONTACT + "(" + COLUMN_COMPANY_ID
            + " integer not null,"  + COLUMN_CONTACT_ID
            + " integer not null, UNIQUE("  + COLUMN_COMPANY_ID
            + ", "  + COLUMN_CONTACT_ID
            + ") ON CONFLICT REPLACE);";

    private static final String DATABASE_CREATE_TASKS = "create table "
            +  TABLE_TASKS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null,"  + COLUMN_DATE
            + " text," + COLUMN_DESCRIPTION
            + " text," + COLUMN_STATUS
            + " text," + COLUMN_PRIORITY
            + " text," + COLUMN_COMPANY
            + " text," + COLUMN_CONTACT
            + " text," + COLUMN_JOB
            + " text);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_JOBS);
        database.execSQL(DATABASE_CREATE_COMPANIES);
        database.execSQL(DATABASE_CREATE_CONTACTS);
        database.execSQL(DATABASE_CREATE_TASKS);
        // translation tables
        database.execSQL(DATABASE_CREATE_JOB_COMPANY);
        database.execSQL(DATABASE_CREATE_COMPANY_CONTACT);
        database.execSQL(DATABASE_CREATE_JOB_CONTACT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion);
        String upgradeJobs1 = "ALTER TABLE " + TABLE_JOBS + " ADD COLUMN " +COLUMN_STATUS+ " TEXT";
        String upgradeJobs2 = "ALTER TABLE " + TABLE_JOBS + " ADD COLUMN " +COLUMN_PAY+ " TEXT";
        String upgradeJobs3 = "BEGIN TRANSACTION;CREATE TEMPORARY TABLE contacts_backup(firstname,lastname,title,phone,email);INSERT INTO contacts_backup SELECT * FROM contacts;DROP TABLE contacts;CREATE TABLE contacts(contact,title,phone,email);INSERT INTO contacts SELECT firstname + \" \" + lastname as contact,title,phone,email FROM contacts_backup;DROP TABLE contacts_backup;COMMIT;";
        
        if (oldVersion == 1 && newVersion == 2)
            db.execSQL(upgradeJobs1);
        	db.execSQL(upgradeJobs2);
        	db.execSQL(upgradeJobs3);
    }

}