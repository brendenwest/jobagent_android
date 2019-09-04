package com.brisksoft.jobagent;

import com.brisksoft.jobagent.Classes.*;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class ContactDetail extends BaseActivity {
    /** Called when the activity is first created. */

    private String[] contact;
    private ContactsDataSource datasource;

    private EditText txtContact;
    private EditText txtCompany;
    private EditText txtTitle;
    private EditText txtPhone;
    private EditText txtEmail;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        String TAG = getString(R.string.details_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // contact ID is at 0 index, but not shown in UI
        contact = getIntent().getStringArrayExtra("CONTACT");
        Log.d(TAG, "contact = " + contact[1]);

        txtContact = (EditText) findViewById(R.id.name);
        txtContact.setText(contact[1]);

        txtCompany = (EditText) findViewById(R.id.company);
        txtCompany.setText(contact[2]);

        txtTitle = (EditText) findViewById(R.id.title);
        txtTitle.setText(contact[3]);

        txtPhone = (EditText) findViewById(R.id.phone);
        txtPhone.setText(contact[4]);

        txtEmail = (EditText) findViewById(R.id.email);
        txtEmail.setText(contact[5]);

        // Open db for save/delete operations
        datasource = new ContactsDataSource(this);
        datasource.open();
        
        // Log pageview w/ Google Analytics
        ((JobAgent) this.getApplication()).trackPVFull("Contact details", "contact", contact[1],"");
     }

    // package item detail for saving
    void saveEdits() {
    	String name = txtContact.getText().toString();
    	if (!name.isEmpty()) {
	        Contact newContact = new Contact(name, txtCompany.getText().toString());
	        newContact.setTitle(txtTitle.getText().toString());
	        newContact.setContact(txtContact.getText().toString());
	        newContact.setPhone(txtPhone.getText().toString());
	        newContact.setEmail(txtEmail.getText().toString());

	        if (contact[0] != null && !contact[0].isEmpty()) { // existing Contacts already have an ID
	            newContact.setId(Long.valueOf(contact[0]));
	            // save the new Contact to the database
	            datasource.updateContact(newContact);
	        } else {
	            // save the new Contact to the database
	            Contact savedContact = datasource.createContact(newContact);
	            contact[0] = String.valueOf(savedContact.getId());
	        }
	        // notify user that job was saved
	        Toast.makeText(getApplicationContext(), "Saving...", Toast.LENGTH_SHORT).show();
	        // Log pageview w/ Google Analytics
	        ((JobAgent) this.getApplication()).trackPVFull("Contact details", "save contact", name,"");

    	}
    }
    
    void reachContact() {
    	Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
    	shareIntent.setType("text/plain");
    	shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

    	// Add data to the intent, the receiving app will decide what to do with it.
    	shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Reaching out - ");
    	shareIntent.putExtra(Intent.EXTRA_EMAIL,txtEmail.getText().toString());
    	shareIntent.putExtra(Intent.EXTRA_PHONE_NUMBER,txtPhone.getText().toString());
    	startActivity(Intent.createChooser(shareIntent,"Choose contact method"));
        // Log pageview w/ Google Analytics
        ((JobAgent) this.getApplication()).trackPVFull("Contact details", "reach out", txtTitle.getText().toString(),"");

    }


    // Called via the ContactBtnClicked attribute
    public void itemBtnClicked(View view) {
        switch (view.getId()) {
            case R.id.linkedin:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contact[3]));
                startActivity(browserIntent);
                break;
        	case R.id.connect:
        		reachContact();
                break;
            case R.id.delete:
                if (contact[0] != null && !contact[0].isEmpty()) {
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
         // delete an existing company 
         datasource.deleteContact(Long.valueOf(contact[0]));
         txtContact.setText(""); // set title to blank to avoid saving on exit
         ContactDetail.this.finish();
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
 }



