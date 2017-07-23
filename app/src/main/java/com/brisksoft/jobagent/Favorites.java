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

public class Favorites extends BaseActivity {
    private JobsDataSource datasource;
    private ListAdapter listAdapter;
    private final ActivityHelper helper = new ActivityHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        String TAG = getString(R.string.favorites_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // load item list and set on-click events
        datasource = new JobsDataSource(this);
        datasource.open();

        final List<Job> jobList = datasource.getAllJobs();
    	
        listAdapter = new ListAdapter(this, jobList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listAdapter);

        // set on-click event for list items
    	listView.setOnItemClickListener(new OnItemClickListener()
        {
        public void onItemClick( AdapterView<?> arg0, View view, int position, long id)
            {
        		// pass selected job item to detail view
                Intent intentDetail = new Intent(getApplicationContext(), JobDetail.class);            	
                String[] aJob = new String[] {String.valueOf(jobList.get(position).getId()),jobList.get(position).getTitle(), 
                		jobList.get(position).getCompany(),jobList.get(position).getDescription(),jobList.get(position).getLink(),
                		jobList.get(position).getLocation(),jobList.get(position).getType(),jobList.get(position).getDate(),
                		jobList.get(position).getStatus(),jobList.get(position).getContact(),jobList.get(position).getPay()};

                Log.d("job", "job[6] = "+aJob[6]);
                Log.d("job", "job[7] = "+aJob[7]);
                Log.d("job", "job[8] = "+aJob[8]);
                Log.d("job", "job[9] = "+aJob[9]);
                Log.d("job", "job[10] = "+aJob[10]);
                
                intentDetail.putExtra("JOB", aJob);
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
        addItem.setVisible(true).setTitle("Add Job");
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
    	List<Job> newItems = datasource.getAllJobs();
    	listAdapter.clear();
    	listAdapter.addAll(newItems);
    	listAdapter.notifyDataSetChanged();
    	datasource.close();    	
    }
    
    void loadItemDetail(String[] aItem) {

		// pass selected job item to detail view
    	if (aItem == null) {
        	Log.d("Favorites", "create empty job ");
        	aItem = new String[] {"","","","","","","","","","",""};
    	}
        Intent intentDetail = new Intent(getApplicationContext(), JobDetail.class);
        intentDetail.putExtra("JOB", aItem);
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
