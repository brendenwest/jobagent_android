package com.brisksoft.jobagent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.brisksoft.jobagent.Classes.*;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TaskDetail extends BaseActivity {
    /** Called when the activity is first created. */

    private String[] task;
    private TasksDataSource datasource;
    private String TAG;

    private EditText txtTitle;
    private TextView txtDate;
    private EditText txtDescription;
    private EditText txtCompany;
    private EditText txtContact;    
    private EditText txtJob;    

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);

        TAG = getString(R.string.details_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // task ID is at 0 index, but not shown in UI
        
        task = getIntent().getStringArrayExtra("TASK");
        Log.d(TAG, "task = " + task[0]);

        txtTitle = (EditText) findViewById(R.id.title);
        txtTitle.setText(task[1]);

        txtDate = (TextView) findViewById(R.id.taskDate);
        txtDate.setInputType(InputType.TYPE_NULL); // over-ride input type to avoid showing numeric keyboard
        if (!task[2].isEmpty()) {
        	txtDate.setText(task[2]);
        } else {
 	       Calendar c = Calendar.getInstance();
 	       Date date = new Date();			  
 	       c.setTime(date);
 	       String shortDate = c.get(Calendar.MONTH)+1 + "-" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.YEAR);
 	       txtDate.setText(shortDate);
        }

	    setRadioBtn((RadioGroup)findViewById(R.id.status), task[3]);
	    setRadioBtn((RadioGroup)findViewById(R.id.priority), task[4]);

        txtDescription = (EditText) findViewById(R.id.description);
        txtDescription.setText(task[5]);

        txtCompany = (EditText) findViewById(R.id.company);
        txtCompany.setText(task[6]);

        txtContact = (EditText) findViewById(R.id.contact);
        txtContact.setText(task[7]);

        txtJob = (EditText) findViewById(R.id.job);
        txtJob.setText(task[8]);

        // Open db for save/delete operations
        datasource = new TasksDataSource(this);
        datasource.open();
        
        // Log pageview w/ Google Analytics
        ((JobAgent) this.getApplication()).trackPVFull("Task details", "load task", task[1],"");
     }

	// set radio button
    // set job type radio button
    private void setRadioBtn(RadioGroup groupType, String item) {
	    int count = groupType.getChildCount();
	    for (int i=0;i<count;i++) {
	        View o = groupType.getChildAt(i);
	        if (o instanceof RadioButton) {
	            RadioButton currentRadio = (RadioButton) o;
	            if(currentRadio.getText().equals(item)) {
	            	currentRadio.setChecked(true);
	            }
	        }
	    }       
    }

    
    // package item detail for saving
    private void saveEdits() {
    	String taskTitle = txtTitle.getText().toString();
    	if (!taskTitle.isEmpty()) {
            Log.d(TAG, "saving task = " + task[0] + "; " + taskTitle);
	        Task newTask = new Task();
	        newTask.setTitle(taskTitle);
	        newTask.setDate(txtDate.getText().toString());
	        newTask.setDescription(txtDescription.getText().toString());
	        newTask.setCompany(txtCompany.getText().toString());
	        newTask.setContact(txtContact.getText().toString());	        
	        newTask.setJob(txtJob.getText().toString());

	        RadioGroup groupType =(RadioGroup)findViewById(R.id.status);
	        RadioButton selectedBtn = (RadioButton) findViewById(groupType.getCheckedRadioButtonId());
	        newTask.setStatus(selectedBtn.getText().toString());

	         groupType =(RadioGroup)findViewById(R.id.priority);
	         selectedBtn = (RadioButton) findViewById(groupType.getCheckedRadioButtonId());
	        newTask.setPriority(selectedBtn.getText().toString());
	        
	        if (task[0] != null && !task[0].isEmpty()) { // existing tasks already have an ID
	            Log.d(TAG, "saving as existing");
	            newTask.setId(Long.valueOf(task[0]));
	            // save the new task to the database
	            datasource.updateTask(newTask);
	        } else {
	            // save the new task to the database and obtain new ID
	            task[0] = Long.toString(datasource.createTask(newTask));
	        }
	        // notify user that job was saved
	        Toast.makeText(getApplicationContext(), "Saving...", Toast.LENGTH_SHORT).show();
	        // Log pageview w/ Google Analytics
	        ((JobAgent) this.getApplication()).trackPVFull("Task details", "save task", taskTitle,"");

    	}
    }

    // Called via the TaskBtnClicked attribute
    public void itemBtnClicked(View view) {
        switch (view.getId()) {
            case R.id.delete:
	            Log.d(TAG, "delete called");
                if (task[0] != null && !task[0].isEmpty()) {
                	DialogCallback dialog = new DialogCallback(this)
                	{
                	  public void run()
                	  {
                		  deleteItem();
                	  }
                	};
               }
                break;

        }
    }

    private void deleteItem() {
         datasource.deleteTask(Long.valueOf(task[0]));
         txtTitle.setText(""); // set title to blank to avoid saving on exit
         TaskDetail.this.finish();
   } 

    // Date picker
    public static class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {
		// Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			TextView tmpDate = (TextView) getActivity().findViewById(R.id.taskDate);
            String existingDate = tmpDate.getText().toString();
            // if user has already entered a date, use that, otherwise use the current date
            if (!existingDate.isEmpty()) {
	            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy",  Locale.US);
	            try {
					Date date = sdf.parse(existingDate);
					c.setTime(date);
					  
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
 
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
		
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
			Log.d("task", "picked date");
		    String selectedDate = (month+1) + "/" + day + "/" + year; // picker uses zero-based months
			TextView tmpDate = (TextView) getActivity().findViewById(R.id.taskDate);
		    tmpDate.setText(selectedDate);
		}
    }
    
    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
    
    // END Date picker

    
    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        saveEdits(); 
        datasource.close();
        super.onPause();
    }
 }



