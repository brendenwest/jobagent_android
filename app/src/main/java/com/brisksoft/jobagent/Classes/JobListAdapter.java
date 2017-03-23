package com.brisksoft.jobagent.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.brisksoft.jobagent.R;

import java.util.List;
/**
 * Created by usexbrwe on 12/14/13.
 */

public class JobListAdapter extends ArrayAdapter {
    private final List<Job> jobList;
    private final Context context;

    public JobListAdapter(Context context, List<Job> jobList) {
        super(context, R.layout.list_item_2_line, jobList);
        this.context = context;
        this.jobList = jobList;
    }

    public class ViewHolder{
        public TextView item1;
        public TextView item2;
    }

    //        @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater vi =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_2_line, null);
            holder = new ViewHolder();
            holder.item1 = (TextView) v.findViewById(R.id.text1);
            holder.item2 = (TextView) v.findViewById(R.id.text2);
            v.setTag(holder);
        }
        else
            holder=(ViewHolder)v.getTag();

        final Job job = jobList.get(position);
        if (job != null) {
        	String company = (job.getCompany() != null) ? job.getCompany() + " | " : "";
            holder.item1.setText(job.getTitle());
            holder.item2.setText(company + job.getDate());
        }
        return v;
    }

}
