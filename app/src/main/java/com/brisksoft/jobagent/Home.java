package com.brisksoft.jobagent;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.brisksoft.jobagent.Classes.LocationHelper;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class Home extends BaseActivity {
    /** Called when the activity is first created. */
    public final static String EXTRA_MESSAGE = "com.brisksoft.jobagent.MESSAGE";

	/** The view to show the ad. */
	private AdView adView;

	  /* Your ad unit id. Replace with your actual ad unit id. */
	private static final String AD_UNIT_ID = "a14f6e8f0c6d11b";

    private SharedPreferences sharedPref;

    private JobAgent jobAgent;
	private LocationHelper locationHelper;

    private static final int PICK_LOCATION = 1;  // The request code
    private static final int CHANGE_LOCATION = 2;  // The request code
    private static final String TAG = "Home";
    private EditText txtLocation;
    private List<String> recentSearches;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jobAgent = ((JobAgent) this.getApplication());
        sharedPref = getPreferences(Context.MODE_PRIVATE);

        setContentView(R.layout.main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

        locationHelper = new LocationHelper(this);
        txtLocation = (EditText) findViewById(R.id.txtLocation);
    	txtLocation.setOnFocusChangeListener(new OnFocusChangeListener() {          
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                	String newLocation = txtLocation.getText().toString();
                Log.d(TAG, "txtLocation lost focus");
                	if (locationHelper.isNewLocation(newLocation)) {
                        locationHelper.getAddressFromString(newLocation);
                	}
                }
            }
        });

    	createAd();
    	
        /*
         * check if user has stored location
         * if none, check for location provider
         * if providers, detect location
         * otherwise pop location settings prompt
        */
    	if (locationHelper.hasLocation()) {
            txtLocation.setText(locationHelper.getLocationText());
    	} else {
           	Log.d(TAG, "no stored location. Use detection...");

	        if (locationHelper.hasProviders()) {

                locationHelper.detectUserLocation();

            } else {

                AlertDialog.Builder dialog = new AlertDialog.Builder(getBaseContext());
	            dialog.setMessage(getString(R.string.location_not_enabled));
	            dialog.setPositiveButton(getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {

	                @Override
	                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
	                    // open location settings screen
	   	        	  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                        startActivity(intent);
	                }
	            });
	            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

	                @Override
	                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
	                    // TODO Auto-generated method stub

	                }
	            });
	            dialog.show();

	        }
    	}
    	
        // populate list of recent searches
     	listView = (ListView) findViewById(R.id.searchesList);
    	getRecentSearches();
    	
    	// log screen view
		jobAgent.trackPV(TAG);

     }

    void createAd() {
        // Create an ad.
        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);

        // Add the AdView to the view hierarchy. The view will have no size
        // until the ad is loaded.
        LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
        layout.addView(adView);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.
        AdRequest adRequest = new AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDevice("DCB112EB3B09B486F81A09A786DF60D")
            .build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }
    
    void getRecentSearches() {
	
	    // load recent searches from preferences
    	// populate global list of recent searches
    	// populate search form w/ most recent search
	    int size = sharedPref.getInt("searches_size", 0);  
	    
	    if (size > 0) {
		    if (null == recentSearches) {
		    	recentSearches = new ArrayList<String>();
		    } else {
	    		recentSearches.clear(); // clear recentSearches arraylist		    	
		    }
	    	for (int i=0; i<size;i++) 
		    { 
		    	recentSearches.add(sharedPref.getString("search_" + i, null)); // populate recentSearches arraylist
		    }

            findViewById(R.id.recentSearchesHeader).setVisibility(View.VISIBLE);        	        	
	     	 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	                 android.R.layout.simple_list_item_1, android.R.id.text1, recentSearches);
	       
	               // Assign adapter to ListView
	         listView.setAdapter(adapter);
	         
	         // populate search field with most recent term 
             String[] newSearch = recentSearches.get(0).split("~");
             EditText txtSearch = (EditText) findViewById(R.id.txtQuery);
             txtSearch.setText(newSearch[0]);

		     listView.setOnItemClickListener(new OnItemClickListener()
		        {
		        public void onItemClick( AdapterView<?> arg0, View view, int position, long id)
		            {
		        		// pass selected task item to detail view
		                String[] newSearch = recentSearches.get(position).split("~");
		            	EditText txtSearch = (EditText) findViewById(R.id.txtQuery);
		            	txtSearch.setText(newSearch[0]);
		            	txtLocation.setText(newSearch[1]);
                        executeSearch(newSearch[1], newSearch[0]);
                    }
		        } );
	    } else {
            findViewById(R.id.recentSearchesHeader).setVisibility(View.INVISIBLE);	        	
	    }
	}

	public Boolean validSearchEntries(String searchTerm, String locationText) {
		return (!searchTerm.isEmpty() && !locationText.isEmpty());
	}

    void setRecentSearch(String search, String postalCode) {
	    if (null == recentSearches) {
	    	recentSearches = new ArrayList<String>();
	    }
	    String searchStr = search + "~"+ postalCode;
	    if (!recentSearches.contains(searchStr)) {
		    recentSearches.add(0,searchStr);
	
	        // save the list of recent searches to shared preferences
		    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		    SharedPreferences.Editor editor = sharedPref.edit();
		    
		    // remove all existing items to prevent orphaned values
		    int size = recentSearches.size();
		    for (int i = 0; i < size; i++) {
		        editor.remove("search_"+i);
		    }
		    editor.apply();
		    
		    // limit to 10 most recent searches
		    if (size > 10) { size = 10; } 
		    editor.putInt("searches_size", size);
		    for (int i = 0; i < size; i++) {
		        editor.putString("search_"+i, recentSearches.get(i));
		    }       
		    editor.apply();
	    }
    }

    /** Called when the user selects the Search button */
    public void searchBtnClicked(View view) {
        // Check input text fields
    	
    	EditText txtSearch = (EditText) findViewById(R.id.txtQuery);
    	String searchTerm = txtSearch.getText().toString();
    	String locationText = txtLocation.getText().toString();

    	if (validSearchEntries(searchTerm, locationText)) {
    		if (locationHelper.isNewLocation(locationText)) {
    		// user entered new location, so get get location details before searching
                locationHelper.getAddressFromString(locationText);
    		}
    		executeSearch(locationText, searchTerm);
    	} else {
			jobAgent.simpleAlert(Home.this, "Enter a search term & location");
		}
    	
	}

	private void executeSearch(String locationText, String searchTerm) {
		setRecentSearch(searchTerm, locationText);
		// launch search activity w/ text field values
		Intent intent = new Intent(this, SearchResults.class);
		String message = searchTerm + "^" + locationText + "^" + locationHelper.getCountry();
		intent.putExtra(EXTRA_MESSAGE, message); 
		startActivity(intent);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_LOCATION) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a location
                locationHelper.setZipFromData(data.getParcelableExtra("newLocation"));
                txtLocation.setText(locationHelper.getLocationText());
            }
        }
        else if (requestCode == CHANGE_LOCATION) {
            txtLocation.setText(locationHelper.getLocationText());
        }

    }

    @Override
    public void onResume() {
      super.onResume();
      if (adView != null) {
        adView.resume();
      }
      getRecentSearches();

    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
          }
        super.onPause();
      }

    /** Called before the activity is destroyed. */
    @Override
    public void onDestroy() {
      // Destroy the AdView.
      if (adView != null) {
        adView.destroy();
      }
      super.onDestroy();
    }
}


