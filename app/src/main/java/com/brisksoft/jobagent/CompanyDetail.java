package com.brisksoft.jobagent;

import com.brisksoft.jobagent.Classes.CompaniesDataSource;
import com.brisksoft.jobagent.Classes.Company;
import com.brisksoft.jobagent.Classes.DialogCallback;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class CompanyDetail extends BaseActivity {
    /** Called when the activity is first created. */

    private String[] company;
    private CompaniesDataSource datasource;

    private EditText txtCompany;
    private EditText txtDescription;
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company);

        String TAG = getString(R.string.details_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // company ID is at 0 index, but not shown in UI
        
        company = getIntent().getStringArrayExtra("COMPANY");
        Log.d(TAG, "company = " + company[1]);

        txtCompany = (EditText) findViewById(R.id.companyName);
        txtCompany.setText(company[1]);

        txtDescription = (EditText) findViewById(R.id.companyDescription);
        txtDescription.setText(company[2]);
        
        // set Co type radio button
        RadioGroup groupType = (RadioGroup)findViewById(R.id.type);
        int count = groupType.getChildCount();
        for (int i=0;i<count;i++) {
            View o = groupType.getChildAt(i);
            if (o instanceof RadioButton) {
                RadioButton currentRadio = (RadioButton) o;
                if(currentRadio.getText().equals(company[3])) {
                	currentRadio.setChecked(true);
                }
            }
        }       

        // show 'save' button for new item
        // show 'delete' button in case of an existing item
        if (company[0] != null && !company[0].isEmpty()) { // existing items already have an ID
            findViewById(R.id.save).setVisibility(View.GONE);        	
        } else {
            findViewById(R.id.delete).setVisibility(View.GONE);        	        	
        }

        // Open db for save/delete operations
        datasource = new CompaniesDataSource(this);
        datasource.open();
        
        // Log pageview w/ Google Analytics
        ((JobAgent) this.getApplication()).trackPVFull("Company details", "company", company[1],"");
     }

    // package item detail for saving
    void saveEdits() {
    	String itemTitle = txtCompany.getText().toString();
        if (!itemTitle.isEmpty()) {
	        RadioGroup groupType =(RadioGroup)findViewById(R.id.type);
	        RadioButton selectedBtn = (RadioButton) findViewById(groupType.getCheckedRadioButtonId());
            Company newCompany = new Company(itemTitle, txtDescription.getText().toString(), selectedBtn.getText().toString());

	        if (company[0] != null && !company[0].isEmpty()) { // existing Contacts already have an ID
	            newCompany.setId(Long.valueOf(company[0]));
	            // save  existing Company to the database
	            datasource.updateCompany(newCompany);
	        } else {
	            // save  new Company to the database
	            Company savedCompany = datasource.createCompany(newCompany);
	            company[0] = String.valueOf(savedCompany.getId()); // update current company with newly created ID to avoid duplicate saves
	        }
	        // notify user that job was saved
	        Toast.makeText(getApplicationContext(), "Saving...", Toast.LENGTH_SHORT).show();
	        // Log pageview w/ Google Analytics
	        ((JobAgent) this.getApplication()).trackPVFull("Company details", "save company",itemTitle,"");
    	}
    }

    // Called via the CompanyBtnClicked attribute
    public void itemBtnClicked(View view) {

        switch (view.getId()) {
        	case R.id.save:
        		saveEdits(); 
            	break;
            case R.id.glassdoor:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.glassdoor.com/Reviews/" +company[1]+ "-reviews-SRCH_KE0,7.htm"));
                startActivity(browserIntent);
                break;
            case R.id.linkedin:
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/"+company[1]));
                startActivity(browserIntent2);
                break;
            case R.id.delete:
                if (company[0] != null && !company[0].isEmpty()) {
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
	
    void deleteItem() {
        // delete an existing company 
    	// called from DialogCallback
        datasource.deleteCompany(Long.valueOf(company[0]));
        txtCompany.setText(""); // set title to blank to avoid saving on exit
        this.finish();

    }
 }



