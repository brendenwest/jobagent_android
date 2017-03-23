package com.brisksoft.jobagent;

/**
 * Created by brenden on 9/3/16.
 */

import com.brisksoft.jobagent.Classes.Job;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class SearchResults extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    TabLayout tabLayout;
    ListView listView ;

    private List<Job> listings;
    private ProgressDialog progress;

    private static String SEARCH_API = "http://brisksoft.herokuapp.com/getjobs?location=<location>&kw=<kw>&v=1&country=<country>&max=50";

    private RequestQueue requestQueue;
    private Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);
        setupTabs();

        String TAG = getString(R.string.results_title);

        // configure action bar
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setTitle(TAG);

        listings = new ArrayList<Job>();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        // Get the message from home screen
        Intent intent = getIntent();
        String message = intent.getStringExtra(Home.EXTRA_MESSAGE);
        if (null != message) {
            Log.d(TAG, "message = " + message);
            TextView txtSearchMsg = (TextView) findViewById(R.id.txtSearchMsg);
            String[] searchTerms = message.split("\\^");
            txtSearchMsg.setText("Results for " + searchTerms[0] + " in " + searchTerms[1]);

            fetchJobs(searchTerms);

            // Log pageview w/ Google Analytics
            ((JobAgent) this.getApplication()).trackPVFull(TAG, "search term", searchTerms[0],"");
        }

    }

    // tab methods
    private void setupTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Indeed"));
        tabLayout.addTab(tabLayout.newTab().setText("Career Builder"));
        tabLayout.addTab(tabLayout.newTab().setText("Oodle"));
        tabLayout.addTab(tabLayout.newTab().setText("LinkUp"));
        tabLayout.addOnTabSelectedListener(this);
    }

    private void showProgress(Boolean show) {
        if (show) {
            progress = new ProgressDialog(this);
            progress.setMessage("Searching jobs... ");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
        } else {
            SearchResults.this.progress.dismiss();
        }

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        setListViewContent(tab.getText().toString().toLowerCase().replaceAll("\\s",""));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private class ResultsAdapter extends ArrayAdapter<Job> {
        private final List<Job> jobList;
        private final Context context;

        public ResultsAdapter(Context context, List<Job> jobList) {
            super(context, R.layout.list_item_2_line, jobList);
            this.context = context;
            this.jobList = jobList;
        }

        public class ViewHolder{
            public TextView item1;
            public TextView item2;
        }

        //       @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            SearchResults.ResultsAdapter.ViewHolder holder;
            if (v == null) {
                LayoutInflater vi =
                        (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_2_line, null);
                holder = new SearchResults.ResultsAdapter.ViewHolder();
                holder.item1 = (TextView) v.findViewById(R.id.text1);
                holder.item2 = (TextView) v.findViewById(R.id.text2);
                v.setTag(holder);
            }
            else
                holder=(SearchResults.ResultsAdapter.ViewHolder)v.getTag();

            final Job job = jobList.get(position);
            if (job != null) {
                holder.item1.setText(job.title);
                String jobDate = ((JobAgent) getApplication()).getShortDate(job.date);
                holder.item2.setText(jobDate + " ~ " + job.company + " ~ " + job.location);
            }
            return v;
        }

    }

    private void setListViewContent(String mSite) {

        final List<Job> jobs = new ArrayList<>();

        for (int i = 0; i < listings.size(); i++) {
            String tmpStr = listings.get(i).link;
            if (tmpStr.contains(mSite)) {
                jobs.add(listings.get(i));
            }
        }

        ResultsAdapter adapter = new ResultsAdapter(this,jobs);
        listView = (ListView) findViewById(R.id.siteListing);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onListItemClick(jobs.get(position));
            }
        });

    }

    private void onListItemClick(Job job) {
        // get job at requested position in tmpList
        // pass empty value for job ID, status, contact & pay
        Log.d("CLICK", job.title);

        String jobDate = ((JobAgent) getApplication()).getShortDate(job.getDate());
        String[] aJob = new String[] {"",job.title, job.company,
                job.description,job.link,job.location,job.type, jobDate,"", "", ""};
        // pass selected job item to detail view
        Intent intentDetail = new Intent(getApplicationContext(), JobDetail.class);
        intentDetail.putExtra("JOB", aJob);
        startActivity(intentDetail);

    }

    private void fetchJobs(String[] searchTerms) {
        String query = "", location = "";
        try {
            query = URLEncoder.encode(searchTerms[0],"UTF-8");
            location = URLEncoder.encode(searchTerms[1],"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        showProgress(true);
        String searchUrl = SEARCH_API.replace("<kw>",query).replace("<location>", location).replace("<country>", searchTerms[2]);

        StringRequest request = new StringRequest(Request.Method.GET, searchUrl, onJobsLoaded, onJobsError);
        Log.d("JobAgent", "Searching: " + searchUrl);

        requestQueue.add(request);
    }

    private final Response.Listener<String> onJobsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                // returned from background thread
                // convert string to JSONObject & extract array. JSONObjectRequest failing for some reason
                JSONObject responseObj = new JSONObject(response);
                listings = Arrays.asList(gson.fromJson(responseObj.getJSONArray("jobs").toString(), Job[].class));

                showProgress(false);
                // populate listview
                setListViewContent("indeed");

            } catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
            }
        }
    };

    private final Response.ErrorListener onJobsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
    }

}