package com.brisksoft.jobagent.Classes;

/**
 * Created by brenden on 3/17/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.brisksoft.jobagent.Cities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper implements LocationListener {
    private static LocationManager mLocationManager;
    private static final int PICK_LOCATION = 1;  // The request code from city picklist
    private Activity activity;

    // preference settings
    private SharedPreferences sharedPreferences;
    private static final String City = "city";
    private static final String State = "state";
    private static final String Country = "country";
    private static final String PostalCode = "postalCode";
    private static final String LocationText = "locationText";


    public LocationHelper(Activity activity)
    {
        this.activity = activity;
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        mLocationManager =
                (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

    }

    public boolean hasProviders()
    {
        List<String> providers = mLocationManager.getProviders(true);
        return (providers != null);
    }

    public boolean hasLocation()
    {
        String locationText = getLocationText();
        return locationText != null && !locationText.isEmpty();
    }

    public String getLocationText() {
        return sharedPreferences.getString(LocationText, "");
    }

    public String getCountry() {
        return sharedPreferences.getString(Country, "");
    }

    public Boolean isNewLocation(String newLocation) {
        return !newLocation.equals(getLocationText());
    }

    public void getAddressFromString(String locationEntry) {

        if(Geocoder.isPresent()) {
            Geocoder geocoder= new Geocoder(this.activity, Locale.getDefault());

            try {

                List<Address> addresses = geocoder.getFromLocationName(locationEntry, 30); //

                if(addresses != null) {
                    Log.d("getLocationAddress", "# of addresses found = " + addresses.size());
                    if (addresses.size() == 1) {
                        setZipFromAddress(addresses.get(0));
                    }
                    else {
                        for(int i=0; i<addresses.size(); i++) {
                            Log.d("geocode","locations found " + addresses.get(i).getLocality() + ", " + addresses.get(i).getAdminArea());
                        }
                        // convert address list to parcelable array and launch picker activity
                        final Address[] addressArray = addresses.toArray(new Address[addresses.size()]);

                        Intent intent = new Intent().setClass(activity.getBaseContext(), Cities.class);
                        intent.putExtra("cityList", addressArray);
                        activity.startActivityForResult(intent, PICK_LOCATION);
                    }
                }

                else
                    Log.d("geocode","no location found");

            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("geocode","Could not get address..!");
            }
        } else {
            Log.d("geocode","No Geocoder");

        }
    }

    public void setZipFromData(Parcelable location) {
        Address newLocation = (Address) location;
        setZipFromAddress(newLocation);
    }

    private void setZipFromAddress(Address address) {

        Log.d("geocode","one location found " + address.getLocality() + ", " + address.getAdminArea() + ", " + address.getPostalCode() + ", " + address.getLatitude() + ", " + address.getLongitude());
        if(Geocoder.isPresent()) {
            Geocoder geocoder= new Geocoder(this.activity, Locale.ENGLISH);

            try {

                List<Address> foundAddress = geocoder.getFromLocation(address.getLatitude(),address.getLongitude(), 1);
//		    Log.d("geocode","rev geocode = " + foundAddress.get(0).getLocality() + ", " + foundAddress.get(0).getAdminArea() + ", " + foundAddress.get(0).getPostalCode() + ", " + foundAddress.get(0).getLatitude() + ", " + foundAddress.get(0).getLongitude());

                if(foundAddress != null) {
                    Log.d("geocode","set new zip");
                    setLocation(foundAddress.get(0));
                }
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("geocode","Could not get address..!");
            }
        } else {
            Log.d("geocode","No Geocoder");

        }

    }

    private void setLocation(Address newLocation) {
        String city = newLocation.getLocality();
        String country = newLocation.getCountryCode();
        String postalCode = newLocation.getPostalCode();
        String locationText = (country.equals("US")) ? postalCode : city + ", " + country;
        Log.d("setDefaultLocation","country = '" + country + "'");
        Log.d("setDefaultLocation","locationText = " + locationText);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PostalCode, postalCode);
        editor.putString(City, city);
        editor.putString(State, newLocation.getAdminArea());
        editor.putString(Country, country);
        editor.putString(LocationText, locationText);
        editor.apply();

    }

    /** Called if no last-known location */
    public void detectUserLocation() {
        // Retrieve a list of location providers that have fine accuracy, no monetary cost, etc
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setCostAllowed(false);

        String providerName = mLocationManager.getBestProvider(criteria, true);

        if (providerName != null) {
            Location oldLocation = mLocationManager.getLastKnownLocation(providerName);
            Log.d("LocationHelper", "last location  = "+ oldLocation);

            // Initialize the location fields
            if (oldLocation != null) {
                Log.d("LocationHelper", "calling onLocationChanged w/ oldLocation ");
                onLocationChanged(oldLocation);
            } else {
                Log.d("LocationHelper", "calling requestLocationUpdates ");
                mLocationManager.requestLocationUpdates(providerName,
                        400,          // 10-second interval.
                        500,             // 10 meters.
                        this);

            }
        }

    }

    public void onLocationChanged(Location location) {
        // We're sending the update to a handler which then updates the UI with the new
        // location.
        Log.d("LocationHelper", "location onLocationChanged");


        Geocoder geocoder= new Geocoder(activity, Locale.getDefault());

        try {

            //Place your latitude and longitude
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);

            if(addresses != null) {
                Address fetchedAddress = addresses.get(0);
                Log.d("LocationHelper", "detected address = "+ fetchedAddress.getPostalCode());
                setLocation(fetchedAddress);
                activity.setResult(2);
            }

            // add error handling

        }
        catch (IOException e) {
            // TODO Auto-generated catch block

        }
        mLocationManager.removeUpdates(this);

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

}

