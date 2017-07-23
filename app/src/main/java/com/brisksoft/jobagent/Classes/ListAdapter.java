package com.brisksoft.jobagent.Classes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.brisksoft.jobagent.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by usexbrwe on 12/14/13.
 */

public class ListAdapter<T> extends ArrayAdapter {
    private final List<T> list;
    private final Context context;

    public ListAdapter(Context context, List<T> list) {
        super(context, R.layout.list_item, list);
        this.context = context;
        this.list = list;
    }

    public class ViewHolder{
        public TextView title;
        public TextView subtitle;
    }

    //        @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater vi =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) v.findViewById(R.id.item_title);
            holder.subtitle = (TextView) v.findViewById(R.id.item_subtitle);
            v.setTag(holder);
        }
        else
            holder=(ViewHolder)v.getTag();

        T item = list.get(position);
        switch (item.getClass().getSimpleName()) {
            case "Tip":
                Tip tip = com.brisksoft.jobagent.Classes.Tip.class.cast(item);
                holder.title.setText(tip.getTitle());
                break;
            case "Company":
                Company co = com.brisksoft.jobagent.Classes.Company.class.cast(item);
                holder.title.setText(co.getName());
                break;
            case "Contact":
                Contact person = com.brisksoft.jobagent.Classes.Contact.class.cast(item);
                holder.title.setText(person.getContact());
                break;
            case "Job":
                Job job = com.brisksoft.jobagent.Classes.Job.class.cast(item);
                String company = (job.getCompany() != null) ? job.getCompany() + " | " : "";
                holder.title.setText(job.getTitle());
                holder.subtitle.setText(company + job.getDate());
                holder.subtitle.setVisibility(View.VISIBLE);
                break;
        }

        return v;
    }

}
