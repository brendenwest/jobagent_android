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

public class Tasks extends BaseActivity {
    private TasksDataSource datasource;
    private ListAdapter listAdapter;
    private final ActivityHelper helper = new ActivityHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        String TAG = getString(R.string.tasks_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        datasource = new TasksDataSource(this);
        datasource.open();

        final List<Task> taskList = datasource.getAllTasks();

        listAdapter = new ListAdapter<>(this, taskList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listAdapter);

        // set on-click task for list items
    	listView.setOnItemClickListener(new OnItemClickListener()
        {
        public void onItemClick( AdapterView<?> arg0, View view, int position, long id)
            {
        		// pass selected task item to detail view
                String[] aTask = new String[] {String.valueOf(taskList.get(position).getId()),taskList.get(position).getTitle(), taskList.get(position).getDate(),
                		taskList.get(position).getStatus(), taskList.get(position).getPriority(),taskList.get(position).getDescription(),taskList.get(position).getCompany(),taskList.get(position).getContact(),taskList.get(position).getJob()};
        		// pass selected task item to detail view
        		Intent intentDetail = new Intent(getApplicationContext(), TaskDetail.class);
                intentDetail.putExtra("TASK", aTask);
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
        addItem.setVisible(true).setTitle("Add Task");
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
        Log.d("tasks", "updating list ");

        datasource.open();
    	List<Task> newItems = datasource.getAllTasks();
    	listAdapter.clear();
    	listAdapter.addAll(newItems);
    	listAdapter.notifyDataSetChanged();
    	datasource.close();    	
    }
    
    void loadItemDetail(String[] aItem) {

		// pass selected job item to detail view
    	if (aItem == null) {
    		aItem = new String[] {"","","","","","","","",""};
    	}
        Intent intentDetail = new Intent(getApplicationContext(), TaskDetail.class);
        intentDetail.putExtra("TASK", aItem);
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
