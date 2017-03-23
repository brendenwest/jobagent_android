package com.brisksoft.jobagent;

import android.app.ListActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Parcelable;

public class Cities extends ListActivity {
    private Address[] locationList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_2_line);

        final String TAG = getString(R.string.cities_title);

        // configure action bar
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TAG);
        }

        // get list of address list from home screen
        Parcelable[] parcels = getIntent().getExtras().getParcelableArray("cityList");
        locationList = new Address[parcels.length];
        for (int i = 0; i < parcels.length; i++) {
            locationList[i] = (Address) parcels[i];
        }

        // use the CustomAdapter to map elements to a ListView
        CityListAdapter listAdapter = new CityListAdapter(this, locationList);
        setListAdapter(listAdapter);

        // set on-click city for list items
        ListView list = getListView();
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                // return postal code for selected city
                Log.d(TAG, "city " + position + " = " + locationList[position].getLocality());
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newLocation", locationList[position]);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        // log screen view
        ((JobAgent) this.getApplication()).trackPV("Cities");

    }


    public class CityListAdapter extends ArrayAdapter {
        private final Address[] locationList;
        private final Context context;

        public CityListAdapter(Context context, Address[] locationList) {
            super(context, R.layout.list_item_2_line, locationList);
            this.context = context;
            this.locationList = locationList;
        }

        public class ViewHolder {
            public TextView item1;
            public TextView item2;
        }

        //        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                LayoutInflater vi =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.item1 = (TextView) v.findViewById(R.id.text1);
                v.setTag(holder);
            } else
                holder = (ViewHolder) v.getTag();

            final Address location = locationList[position];
            if (location != null) {
                holder.item1.setText(location.getLocality() + ", " + location.getAdminArea());
            }
            return v;
        }

    }

}