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

public class CompaniesDataSource {

    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;
    private final String[] allColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_COMPANY,
            SQLiteHelper.COLUMN_DESCRIPTION,
            SQLiteHelper.COLUMN_TYPE };

    public CompaniesDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Company createCompany(Company company) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_COMPANY, company.getName());
        values.put(SQLiteHelper.COLUMN_DESCRIPTION, company.getDescription());
        values.put(SQLiteHelper.COLUMN_TYPE, company.getType());
        long insertId = database.insert(SQLiteHelper.TABLE_COMPANIES, null,
                values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_COMPANIES,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Company newCompany = cursorToCompany(cursor);
        cursor.close();
        return newCompany;
    }

    public void deleteCompany(long companyId) {
        System.out.println("Company deleted with id: " + companyId);
        database.delete(SQLiteHelper.TABLE_COMPANIES, SQLiteHelper.COLUMN_ID
                + " = " + companyId, null);
    }

    public void updateCompany(Company company) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_COMPANY, company.getName());
        values.put(SQLiteHelper.COLUMN_DESCRIPTION, company.getDescription());
        values.put(SQLiteHelper.COLUMN_TYPE, company.getType());
        
        database.update(SQLiteHelper.TABLE_COMPANIES, values, SQLiteHelper.COLUMN_ID + " = " + company.getId(), null);
    }

    public List<Company> getAllCompanies() {
        List<Company> Companies = new ArrayList<Company>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_COMPANIES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Company Company = cursorToCompany(cursor);
            Companies.add(Company);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return Companies;
    }


    private Company cursorToCompany(Cursor cursor) {
    	Company Company = new Company();
        Company.setId(cursor.getLong(0));
    	Company.setName(cursor.getString(1));
    	Company.setDescription(cursor.getString(2));
    	Company.setType(cursor.getString(3));
        return Company;
    }
} 
