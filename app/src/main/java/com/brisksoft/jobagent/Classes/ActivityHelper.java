/** @class ActivityHelper
 * @brief - shared methods for 'activity' screens
 */

package com.brisksoft.jobagent.Classes;

import com.brisksoft.jobagent.Companies;
import com.brisksoft.jobagent.Contacts;
import com.brisksoft.jobagent.Favorites;
import com.brisksoft.jobagent.R;
import com.brisksoft.jobagent.Tasks;
import com.brisksoft.jobagent.Tips;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ActivityHelper extends Activity {

	public ActivityHelper()
	{	}

	private Activity activity;

	public ActivityHelper(Activity activity)
	{
		this.activity = activity;
	}

    public boolean onCreateOptionsMenu(Menu menu) {
        // configure action bar
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
     
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Base", "called ActivityHelper with item = " + item.getItemId());

        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("ActivityHelper", "called home w/ item = " + item.getItemId());
                activity.finish();
                return true;
            case R.id.navFavorites:
                activity.startActivity(new Intent(activity, Favorites.class));
                return true;
            case R.id.navCompanies:
                activity.startActivity(new Intent(activity, Companies.class));
                return true;
            case R.id.navContacts:
                activity.startActivity(new Intent(activity, Contacts.class));
                return true;
            case R.id.navTasks:
                activity.startActivity(new Intent(activity, Tasks.class));
                return true;
            case R.id.navTips:
                activity.startActivity(new Intent(activity, Tips.class));
				return true;
            default:
                Log.d("ActivityHelper", "called default with item = " + item.getItemId());
                return super.onOptionsItemSelected(item);
        }
        
    }

}


