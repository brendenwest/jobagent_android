package com.brisksoft.jobagent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.brisksoft.jobagent.Classes.ActivityHelper;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
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

public class Tips extends ListActivity {
    /** Called when the activity is first created. */

	private List<String[]> tips = new ArrayList<String[]>();
	private static final String tipsUrl = "http://brisksoft.us/jobagent/tips.json";
	private final ActivityHelper helper = new ActivityHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_2_line);
        
        final String TAG = getString(R.string.tips_title);

        // configure action bar
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TAG);
        }

        ConnectivityManager connMgr = (ConnectivityManager) 
                getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // fetch data
                new LoadListTask().execute(tipsUrl);
            } else {
                // display error
            	Log.d("search results", "network error");
            }


            // set on-click task for list items
        	ListView list = getListView();
        	list.setOnItemClickListener(new OnItemClickListener()
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

    @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	  return helper.onCreateOptionsMenu(menu);
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		  return helper.onOptionsItemSelected(item);
	}

    
 // Implementation of AsyncTask used to download XML feed 
    private class LoadListTask extends AsyncTask<String, Void, List<String[]>> {
    	
        @Override
        protected List<String[]> doInBackground(String... jsonData) {
            
            try{
                /** Getting the parsed data as a List construct */
            	InputStream source = downloadUrl(tipsUrl);

        	   	tips = readJsonStream(source);                
 	     	   
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
  
            return tips;
        }
        
        @Override
        protected void onPostExecute(List<String[]> tips) {
        	setTipsView(tips);

        }
    }

    private void setTipsView(List<String[]> tips) {
        TipsListAdapter listAdapter = new TipsListAdapter(this, tips);
        setListAdapter(listAdapter);    	
    }
    
    // classes to parse JSON response
    List<String[]> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        reader.setLenient(true);
        try {
          return readTipsArray(reader);
        }
         finally {
          reader.close();
        }
      }

      List<String[]> readTipsArray(JsonReader reader) throws IOException {

 	   	reader.beginObject();
 	   	reader.skipValue(); // skip the root name
        while (reader.hasNext()) {
            reader.beginArray();
            while (reader.hasNext()) {
 	            	tips.add(readTip(reader));
            }
           reader.endArray();
        }
        reader.endObject();
        return tips;
      }

      String[] readTip(JsonReader reader) throws IOException {
    	 String[] tip = new String[3];
        
        reader.beginObject();
        while (reader.hasNext()) {
          String name = reader.nextName();
          if (name.equals("title")) {
        	  tip[0] = reader.nextString();
          } else if (name.equals("description")) {
        	  tip[1] = reader.nextString();
          } else if (name.equals("link")) {
        	  tip[2] = reader.nextString();
          } else {
            reader.skipValue();
          }
        }
        reader.endObject();
        return tip;
      }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    public class TipsListAdapter extends ArrayAdapter<String[]> {
        private final List<String[]> tips;
        private final Context context;

        private TipsListAdapter(Context context, List<String[]> tips) {
            super(context, R.layout.list_item_2_line, tips);
            this.context = context;
            this.tips = tips;
        }

        private class ViewHolder{
            private TextView item1;
            private TextView item2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                LayoutInflater vi =
                        (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_2_line, parent);
                holder = new ViewHolder();
                holder.item1 = (TextView) v.findViewById(R.id.text1);
                holder.item2 = (TextView) v.findViewById(R.id.text2);
                v.setTag(holder);
            }
            else
                holder=(ViewHolder)v.getTag();

            final String[] tip = tips.get(position);
            if (tip != null) {
                holder.item1.setText(tip[0]);
                holder.item2.setText(tip[1]);
            }
            return v;
        }

    }

}



