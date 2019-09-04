package com.brisksoft.jobagent;

import com.brisksoft.jobagent.Classes.*;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.os.Bundle;

public class Contacts extends BaseActivity {
    private ContactsDataSource datasource;
    private ListAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        String TAG = getString(R.string.contacts_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        datasource = new ContactsDataSource(this);
        datasource.open();

        final List<Contact> contactList = datasource.getAllContacts();

        listAdapter = new ListAdapter<>(this, contactList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listAdapter);

        // set on-click event for list items
    	listView.setOnItemClickListener(new OnItemClickListener()
        {
        public void onItemClick( AdapterView<?> arg0, View view, int position, long id)
            {
                String[] aContact = new String[] {String.valueOf(contactList.get(position).getId()),contactList.get(position).getContact(), 
                		contactList.get(position).getCompany(),contactList.get(position).getTitle(),contactList.get(position).getPhone(),contactList.get(position).getEmail()};
        		// pass selected contact item to detail view
        		Intent intentDetail = new Intent(getApplicationContext(), ContactDetail.class);
                intentDetail.putExtra("CONTACT", aContact);
                startActivity(intentDetail);

            }
        } );

    	// log screen view
    	((JobAgent) this.getApplication()).trackPV(TAG);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navAdd:
                loadItemDetail(null);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void updateList() {
        datasource.open();
    	List<Contact> newItems = datasource.getAllContacts();
    	listAdapter.clear();
    	listAdapter.addAll(newItems);
    	listAdapter.notifyDataSetChanged();
    	datasource.close();    	
    }

    void loadItemDetail(String[] aItem) {

 		// pass selected job item to detail view
     	if (aItem == null) {
     		aItem = new String[] {"","","","","","",""};
     	}
         Intent intentDetail = new Intent(getApplicationContext(), ContactDetail.class);
         intentDetail.putExtra("CONTACT", aItem);
         startActivity(intentDetail);

     }

    @Override
    protected void onResume() {
        updateList();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }
}
