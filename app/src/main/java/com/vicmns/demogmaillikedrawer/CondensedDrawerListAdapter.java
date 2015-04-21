package com.vicmns.demogmaillikedrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.vicmns.demogmaillikedrawer.model.DrawerItemModel;

import java.util.List;

/**
 * Created by vicmns on 2/13/2015.
 */
public class CondensedDrawerListAdapter extends BaseAdapter {
    private List<DrawerItemModel> mDrawerItemModels;
    LayoutInflater mInflater;

    public CondensedDrawerListAdapter(Context context, List<DrawerItemModel> drawerItemModels) {
        mDrawerItemModels = drawerItemModels;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDrawerItemModels.size();
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
        View rowView = mInflater.inflate(R.layout.condensed_drawer_list_item, parent, false);

        setViewsValues(rowView, position);

        return rowView;
    }

    private void setViewsValues(View rowView, int position) {
        ImageView imageView = (ImageView) rowView.findViewById(R.id.drawer_list_item_section_image);
        imageView.setImageResource(mDrawerItemModels.get(position).getItemImageResId());
    }
}
