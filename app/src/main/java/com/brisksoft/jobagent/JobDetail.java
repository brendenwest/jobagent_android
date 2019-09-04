package com.brisksoft.jobagent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.brisksoft.jobagent.Classes.*;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class JobDetail extends BaseActivity implements OnItemSelectedListener {
    /** Called when the activity is first created. */

    private String[] job;
    private JobsDataSource datasource;
    private String TAG;

    private EditText txtTitle;
    private EditText txtCompany;
    private EditText txtDescription;
    private EditText txtLink;
    private EditText txtLocation;
    private EditText txtDate;
    private EditText txtContact;
    private EditText txtPay;
    
    private Spinner spinnerType;
    private Spinner spinnerStatus;

    private final String[] jobtype = { "", "full-time", "part-time", "contract-W2", "C2H", "1099", "C2C" };
    private final String[] jobstatus = { "", "applied", "phone interview", "in-person interview", "declined", "rejected", "accepted" };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jobdetail);

        TAG = getString(R.string.details_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // job ID is at 0 index, but not shown in UI
        
        job = getIntent().getStringArrayExtra("JOB");

        txtTitle = (EditText) findViewById(R.id.title);
        txtTitle.setText(job[1]);

        txtCompany = (EditText) findViewById(R.id.company);
        txtCompany.setText(job[2]);

        txtDescription = (EditText) findViewById(R.id.description);
        txtDescription.setText(job[3]);

        txtLink = (EditText) findViewById(R.id.link);
        txtLink.setText(job[4]);

        txtLocation = (EditText) findViewById(R.id.location);
        txtLocation.setText(job[5]);
        
        txtDate = (EditText) findViewById(R.id.itemDate);
        txtDate.setInputType(InputType.TYPE_NULL);

        txtContact = (EditText) findViewById(R.id.contact);
        txtContact.setText(job[9]);

        txtPay = (EditText) findViewById(R.id.pay);
        txtPay.setText(job[10]);
        
        if (job[7] != null && !job[7].isEmpty()) {
        	txtDate.setText(job[7]);
        } else {
 	       String shortDate = DateUtils.getShortDate(new Date());
 	       txtDate.setText(shortDate);
        }
        
        // set up 'job type' spinner 
        spinnerType = (Spinner) findViewById(R.id.spinnerType);
        ArrayAdapter<String> adapter_type = new ArrayAdapter<String>(this,
          android.R.layout.simple_spinner_item, jobtype);
        adapter_type
          .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter_type);
        spinnerType.setSelection(adapter_type.getPosition(job[6]));
        spinnerType.setOnItemSelectedListener(this);

        // set up 'status' spinner 
        spinnerStatus = (Spinner) findViewById(R.id.spinnerStatus);
        ArrayAdapter<String> adapter_status = new ArrayAdapter<String>(this,
          android.R.layout.simple_spinner_item, jobstatus);
        adapter_status
          .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerStatus.setAdapter(adapter_status);
        spinnerStatus.setSelection(adapter_status.getPosition(job[8]));
        spinnerStatus.setOnItemSelectedListener(this);
        
        
        // show 'save' button where job details originate from search
        // show 'delete' button in case of a saved job
        if (job[0] != null && !job[0].isEmpty()) { // existing jobs already have an ID
            findViewById(R.id.save).setVisibility(View.GONE);        	
        } else {
            findViewById(R.id.delete).setVisibility(View.GONE);        	        	
        }
        
        // Open db for save/delete operations
        datasource = new JobsDataSource(this);
        datasource.open();
        
        // Log pageview w/ Google Analytics
        ((JobAgent) this.getApplication()).trackPVFull("Job details", "load job", job[1],"");
     }

    public void onItemSelected(AdapterView<?> parent, View view, int position,
    		   long id) {    	
            switch (parent.getId()) {
            case R.id.spinnerType:
		    	spinnerType.setSelection(position);
                break;
            case R.id.spinnerStatus:
		    	spinnerStatus.setSelection(position);
                break;
            default:
                break;
            }
        }

    // package item detail for saving
    void saveEdits(boolean btnClicked) {
    	String jobTitle = txtTitle.getText().toString();
    	boolean hasId = (job[0] != null && !job[0].isEmpty());

        // save jobs only where user has clicked 'save' button or job was previously saved, 
        // and where job title is not empty
    	if (jobTitle != null && !jobTitle.isEmpty() && (btnClicked || hasId)) {
	        Job newJob = new Job();
	        newJob.setTitle(txtTitle.getText().toString());
	        newJob.setCompany(txtCompany.getText().toString());
	        newJob.setDescription(txtDescription.getText().toString());
	        newJob.setLink(txtLink.getText().toString());
	        newJob.setLocation(txtLocation.getText().toString());
	        newJob.setDate(txtDate.getText().toString());
	        newJob.setType(spinnerType.getSelectedItem().toString());
	        newJob.setStatus(spinnerStatus.getSelectedItem().toString());
	        newJob.setContact(txtContact.getText().toString());
	        newJob.setPay(txtPay.getText().toString());
	        
	        
	        if (hasId) { // existing jobs already have an ID
	            Log.d(TAG, "saving as existing");
	            newJob.setId(Long.valueOf(job[0]));
	            // save the new job to the database
	            datasource.updateJob(newJob);
	        } else {
	            // save the new job to the database
	            Log.d(TAG, "saving as new");
	            Job savedJob = datasource.createJob(newJob);
	            job[0] = Long.toString(savedJob.getId()); // retain id of new job
	        }
	        // notify user that job was saved
	        Toast.makeText(getApplicationContext(), "Job saved to Favorites", Toast.LENGTH_SHORT).show();
	        // Log pageview w/ Google Analytics
	        ((JobAgent) this.getApplication()).trackPVFull("Job details", "save job", txtTitle.getText().toString(),"");

    	}
    }
    
    // Called via the itemBtnClicked attribute
    public void itemBtnClicked(View view) {
        
        switch (view.getId()) {
        	case R.id.save:
                saveEdits(true);
                break;
        	case R.id.share:
        		shareJob();
                break;
            case R.id.apply:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(job[4]));
                startActivity(browserIntent);
                break;
            case R.id.delete:
                if (job[0] != null && !job[0].isEmpty()) {
                    // delete an existing job 
                    Log.d(TAG, "deleting job ID = " + job[0]);
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
    
    void deleteItem() {
  	      datasource.deleteJob(Long.valueOf(job[0]));
	      txtTitle.setText(""); // set title to blank to avoid saving on exit
	      JobDetail.this.finish();
     } 

    void shareJob() {
    	Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
    	shareIntent.setType("text/plain");
    	shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

    	// Add data to the intent, the receiving app will decide what to do with it.
    	shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Job lead - "+ txtTitle.getText().toString());
    	shareIntent.putExtra(Intent.EXTRA_TEXT, txtTitle.getText().toString() + "\n " + txtLink.getText().toString() + "\n sent via Job Agent mobile app (http://brisksoft.us)");
    	startActivity(Intent.createChooser(shareIntent,"Share this job"));
        // Log pageview w/ Google Analytics
        ((JobAgent) this.getApplication()).trackPVFull("Job details", "share job", txtTitle.getText().toString(),"");

    }

    // Date picker
    public static class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {
		// Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			TextView tmpDate = (TextView) getActivity().findViewById(R.id.itemDate);
            String existingDate = tmpDate.getText().toString();
            // if user has already entered a date, use that, otherwise use the current date
            if (!existingDate.isEmpty()) {
	            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
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
			TextView tmpDate = (TextView) getActivity().findViewById(R.id.itemDate);
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
        saveEdits(false);
        datasource.close();
        super.onPause();
    }

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
 }



