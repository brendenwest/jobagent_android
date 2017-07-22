package com.brisksoft.jobagent;

import com.brisksoft.jobagent.Classes.*;

import java.util.List;

import android.app.ListActivity;
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

public class Contacts extends ListActivity {
    private ContactsDataSource datasource;
    private ContactListAdapter listAdapter;
    private final ActivityHelper helper = new ActivityHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        String TAG = getString(R.string.contacts_title);

        // configure action bar
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TAG);
        }

        datasource = new ContactsDataSource(this);
        datasource.open();

        final List<Contact> contactList = datasource.getAllContacts();
    	
        // use the CustomAdapter to map elements to a ListView
        listAdapter = new ContactListAdapter(this, contactList);
        setListAdapter(listAdapter);

        // set on-click event for list items
    	ListView list = getListView();
    	list.setOnItemClickListener(new OnItemClickListener()
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem addItem = menu.getItem(0);
        addItem.setVisible(true).setTitle("Add Person");
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

    public class ContactListAdapter extends ArrayAdapter {
        private final List<Contact> contactList;
        private final Context context;

        public ContactListAdapter(Context context, List<Contact> contactList) {
            super(context, R.layout.list_item_2_line, contactList);
            this.context = context;
            this.contactList = contactList;
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

            final Contact contact = contactList.get(position);
            if (contact != null) {
            	Log.d("contact", "contact = " + contact.getContact());
            	String title = contact.getTitle();
            	String company = contact.getCompany();
            	
                String spacer = (title != null && company != null && !title.isEmpty() && !company.isEmpty()) ? ", " : "";
                String line2 = (title != null && !title.isEmpty()) ? title : "";
                if (company != null) { // company field comes from different table, so check for null value
                	line2 += spacer + company;
                }
                		
                holder.item1.setText(contact.getContact());
                holder.item2.setText(line2);
            }
            return v;
        }

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
