package com.brisksoft.jobagent;

import com.brisksoft.jobagent.Classes.*;

import java.util.List;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.os.Bundle;

public class Companies extends BaseActivity {
    private CompaniesDataSource datasource;
    private ListAdapter listAdapter;
    private final ActivityHelper helper = new ActivityHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        String TAG = getString(R.string.companies_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        datasource = new CompaniesDataSource(this);
        datasource.open();

        final List<Company> companyList = datasource.getAllCompanies();
    	
        // use the CustomAdapter to map elements to a ListView
        ListView listView = (ListView) findViewById(R.id.listView);
        listAdapter = new ListAdapter(this, companyList);
        listView.setAdapter(listAdapter);

        // set on-click event for list items
        listView.setOnItemClickListener(new OnItemClickListener()
        {
        public void onItemClick( AdapterView<?> arg0, View view, int position, long id)
            {
        		// pass selected job item to detail view
                String[] aCompany = new String[] {String.valueOf(companyList.get(position).getId()),companyList.get(position).getName(), companyList.get(position).getDescription(),
                		companyList.get(position).getType()};
        		// pass selected company item to detail view
        		Intent intentDetail = new Intent(getApplicationContext(), CompanyDetail.class);
                intentDetail.putExtra("COMPANY", aCompany);
                startActivity(intentDetail);
            }
        } );
    	
    	// log screen view
    	((JobAgent) this.getApplication()).trackPV(TAG);

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem addItem = menu.getItem(0);
        addItem.setVisible(true).setTitle("Add Company");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navAdd:
                loadItemDetail(null);
                return true;
            default:
                return helper.onOptionsItemSelected(item);
        }
    }

    void updateList() {
        datasource.open();
    	List<Company> newItems = datasource.getAllCompanies();
    	listAdapter.clear();
    	listAdapter.addAll(newItems);
    	listAdapter.notifyDataSetChanged();
    	datasource.close();    	
    }

    void loadItemDetail(String[] aItem) {

		// pass selected job item to detail view
    	if (aItem == null) {
        	Log.d("Companies", "create empty job ");
    		aItem = new String[] {"","","",""};
    	}
        Intent intentDetail = new Intent(getApplicationContext(), CompanyDetail.class);
        intentDetail.putExtra("COMPANY", aItem);
        startActivity(intentDetail);

    }


    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }
}


