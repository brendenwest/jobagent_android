package com.brisksoft.jobagent;

import com.brisksoft.jobagent.Classes.*;

import java.util.List;

import android.app.ListActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Tasks extends ListActivity {
    private TasksDataSource datasource;
    private TaskListAdapter listAdapter;
    private final ActivityHelper helper = new ActivityHelper(this);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        String TAG = getString(R.string.tasks_title);

        // configure action bar
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TAG);
        }

        datasource = new TasksDataSource(this);
        datasource.open();

        final List<Task> taskList = datasource.getAllTasks();
    	
        // use the CustomAdapter to map elements to a ListView
        listAdapter = new TaskListAdapter(this, taskList);
        setListAdapter(listAdapter);

        // set on-click task for list items
    	ListView list = getListView();
    	list.setOnItemClickListener(new OnItemClickListener()
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

    public class TaskListAdapter extends ArrayAdapter {
        private final List<Task> taskList;
        private final Context context;

        public TaskListAdapter(Context context, List<Task> taskList) {
            super(context, R.layout.list_item_2_line, taskList);
            this.context = context;
            this.taskList = taskList;
        }

        public class ViewHolder{
            public TextView item1;
            public TextView item2;
        }

        //        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                LayoutInflater vi =
                        (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_2_line, null);
                holder = new ViewHolder();
                holder.item1 = (TextView) v.findViewById(R.id.item_title);
                holder.item2 = (TextView) v.findViewById(R.id.item_subtitle);
                v.setTag(holder);
            }
            else
                holder=(ViewHolder)v.getTag();

            final Task task = taskList.get(position);
            if (task != null) {
                holder.item1.setText(task.getTitle());
                String line2 = (!task.getPriority().isEmpty()) ? "Priority:</i> <b>" + task.getPriority() + "</b>": "";
                if (!task.getDate().isEmpty()) { line2 += "   | Date: <b>" + task.getDate() + "</b>"; }
                if (!task.getStatus().isEmpty()) { line2 += "   | Status: <b>" + task.getStatus() + "</b>"; }
                holder.item2.setText(Html.fromHtml(line2));
            }
            return v;
        }

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
