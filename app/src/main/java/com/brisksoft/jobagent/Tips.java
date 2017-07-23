package com.brisksoft.jobagent;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.brisksoft.jobagent.Classes.ActivityHelper;
import com.brisksoft.jobagent.Classes.ListAdapter;
import com.brisksoft.jobagent.Classes.Tip;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;

public class Tips extends ListActivity {
    /** Called when the activity is first created. */

    ListView listView;
    ListAdapter listAdapter;
	private List<Tip> tips = new ArrayList<Tip>();
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
//                if (tips.get(position)[2] != null && !tips.get(position)[2].isEmpty()) {
//                    // link to web page
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tips.get(position)[2]));
//                    startActivity(browserIntent);
//                }

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
             try {
                 for(int i = 0; i < response.length(); i++){
                     Tip tip = new Tip();
                     tip.setTitle(response.getJSONObject(i).getString("title"));
                     tip.setDescription(response.getJSONObject(i).getString("description"));
                     tip.setLink(response.getJSONObject(i).getString("link"));
                     tips.add(tip);
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


}



