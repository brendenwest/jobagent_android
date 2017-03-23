package com.brisksoft.jobagent;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// class for global variables
public class JobAgent extends Application {

   private final Boolean GA_DryRun = false;
   private final String GA_AppVersion = "4.1";
   private final String kGaPropertyId = "UA-30717261-1";

   @Override
   public void onCreate(){

   }

    public void trackPV(String screen) {
    	GoogleAnalytics.getInstance(this).setDryRun(GA_DryRun);
    	// Set the log level to verbose.
    	GoogleAnalytics.getInstance(this).getLogger().setLogLevel(LogLevel.VERBOSE);
    	// Initialize a tracker using a Google Analytics property ID.
    	Tracker t = GoogleAnalytics.getInstance(this).newTracker(kGaPropertyId);
    	
        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName(screen);
        t.setAppVersion(GA_AppVersion);

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    	
    }
    
    public void trackPVFull(String screen, String category, String action, String label) {
    	GoogleAnalytics.getInstance(this).setDryRun(GA_DryRun);
    	// Set the log level to verbose.
    	GoogleAnalytics.getInstance(this).getLogger().setLogLevel(LogLevel.VERBOSE);
    	// Initialize a tracker using a Google Analytics property ID.
    	Tracker t = GoogleAnalytics.getInstance(this).newTracker(kGaPropertyId);
    	
        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName(screen);
        t.setAppVersion(GA_AppVersion);

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
        
        // This event will also be sent with &cd=<screen>
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
            .setCategory(category)
            .setAction(action)
            .setLabel(label)
            .build());
    }
   
    public String getShortDate(String pubdate) {
 	   if (pubdate != null && !pubdate.isEmpty()) {
 		   // 2014-06-27T10:19:54.000Z
 	       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.getDefault());
 	       
 	       Calendar c = Calendar.getInstance();
 	       try {
 				Date date = sdf.parse(pubdate);			  
 		       c.setTime(date);
 		       return c.get(Calendar.MONTH)+1 + "-" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.YEAR);
 			} catch (ParseException e) {
 				e.printStackTrace();
 				return null;
 			}
 	    		 
 	   } else {
 		   return null;
 	   }
    }


    public Boolean validZip(String zip) {
    	int myNum = 0;

    	try {
    	    myNum = Integer.decode(zip);
    	} catch(NumberFormatException nfe) {
            Log.d("zip validation","Could not parse " + nfe);
    	}

        return zip.length() == 5 && myNum > 0;
    }

	public void simpleAlert(Context context, String message) {
		final AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setMessage(message);
		dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}


}

