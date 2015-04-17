package com.vicmns.demogmaillikedrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vicmns on 2/13/2015.
 */
public class ExpandedDrawerListAdapter extends BaseAdapter {
    private List<DrawerListItemModel> mDrawerListItemModels;
    LayoutInflater mInflater;

    public ExpandedDrawerListAdapter(Context context, List<DrawerListItemModel> drawerListItemModels) {
        mDrawerListItemModels = drawerListItemModels;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDrawerListItemModels.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.expanded_drawer_list_item, parent, false);

        setViewsValues(rowView, position);

        return rowView;
    }

    private void setViewsValues(View rowView, int position) {
        TextView textView = (TextView) rowView.findViewById(R.id.drawer_list_item_section_text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.drawer_list_item_section_image);

        textView.setText(mDrawerListItemModels.get(position).getItemText());
        imageView.setImageResource(mDrawerListItemModels.get(position).getItemImageResId());
    }
}
