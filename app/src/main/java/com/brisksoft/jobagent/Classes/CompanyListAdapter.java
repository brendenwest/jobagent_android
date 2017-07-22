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

public class CompanyListAdapter extends ArrayAdapter {
    private final List<Company> companyList;
    private final Context context;

    public CompanyListAdapter(Context context, List<Company> companyList) {
        super(context, R.layout.list_item, companyList);
        this.context = context;
        this.companyList = companyList;
    }

    public class ViewHolder{
        public TextView item1;
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
            holder.item1 = (TextView) v.findViewById(R.id.item_title);
            v.setTag(holder);
        }
        else
            holder=(ViewHolder)v.getTag();

        final Company company = companyList.get(position);
        if (company != null) {
            holder.item1.setText(company.getCompany());
        }
        return v;
    }

}
