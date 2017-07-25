/**
 * @brief called into home and detail screens
 */
package com.brisksoft.jobagent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

	/** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        switch (this.getLocalClassName()) {
            case "Home":
                inflater.inflate(R.menu.menu_main, menu);
                break;
            default:
                inflater.inflate(R.menu.menu_base, menu);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.navFavorites:
                startActivity(new Intent(this, Favorites.class));
                return true;
            case R.id.navCompanies:
                startActivity(new Intent(this, Companies.class));
                return true;
            case R.id.navContacts:
                startActivity(new Intent(this, Contacts.class));
                return true;
            case R.id.navTasks:
                startActivity(new Intent(this, Tasks.class));
                return true;
            case R.id.navTips:
                startActivity(new Intent(this, Tips.class));
                return true;
            default:
                break;
        }

        return true;

    }

}


