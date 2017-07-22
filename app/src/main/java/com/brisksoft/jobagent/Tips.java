package com.brisksoft.jobagent;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.brisksoft.jobagent.Classes.ActivityHelper;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;

public class Tips extends ListActivity {
    /** Called when the activity is first created. */

    ListView listView;
    ListAdapter listAdapter;
	private List<String[]> tips = new ArrayList<String[]>();
	private static final String tipsUrl = "http://brisksoft.us/jobagent/tips2.json";
	private final ActivityHelper helper = new ActivityHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        
        final String TAG = getString(R.string.tips_title);

        // configure action bar
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TAG);
        }

        listView = (ListView) findViewById(android.R.id.list);
        listAdapter = new ListAdapter(this, tips);
        listView.setAdapter(listAdapter);

        ConnectivityManager connMgr = (ConnectivityManager) 
                getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // fetch data
                loadList(tipsUrl);
            } else {
                // display error
            	Log.d("search results", "network error");
            }


            // set on-click task for list items
        listView.setOnItemClickListener(new OnItemClickListener()
            {
            public void onItemClick( AdapterView<?> arg0, View view, int position, long id)
                {
            		// pass selected task item to detail view
                Log.d(TAG, "tip " + position);
                if (tips.get(position)[2] != null && !tips.get(position)[2].isEmpty()) {
                    // link to web page
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tips.get(position)[2]));
                    startActivity(browserIntent);
                }

                }
            } );
        
        	// log screen view
        	((JobAgent) this.getApplication()).trackPV(TAG);
     }

     private void loadList(String url) {
         RequestQueue queue = Volley.newRequestQueue(this);
         JsonArrayRequest jsonReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
             @Override
             public void onResponse(JSONArray response) {
Log.d("JSON","loaded " + response.length());
                 try {
                     for(int i = 0; i < response.length(); i++){
                         String[] item = new String[3];
                         item[0] = response.getJSONObject(i).getString("title");
                         item[1] = response.getJSONObject(i).getString("description");
                         item[2] = response.getJSONObject(i).getString("link");
                         tips.add(item);
                     }
                     // trigger refresh of recycler view
                     listAdapter.notifyDataSetChanged();
                 } catch (JSONException e) {
                 }
             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 Log.d("JSON", "Error: " + error.getMessage());
             }

         });
         // Add the request to the RequestQueue.
         queue.add(jsonReq);
     }

    @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	  return helper.onCreateOptionsMenu(menu);
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		  return helper.onOptionsItemSelected(item);
	}

    private class ListAdapter extends ArrayAdapter<String[]> {
        private final List<String[]> tips;
        private final Context context;

        private ListAdapter(Context context, List<String[]> tips) {
            super(context,0, tips);
            this.context = context;
            this.tips = tips;
        }

        private class ViewHolder{
            private TextView item1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater vi =
                        (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.item_title);
            final String[] tip = tips.get(position);
            if (tip != null) {
                title.setText(tip[0]);
            }
            return convertView;
        }
    }

}



