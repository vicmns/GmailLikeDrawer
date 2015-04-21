package com.vicmns.demogmaillikedrawer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vicmns.demogmaillikedrawer.lib.CrossFadeSlidingPanelLayout;
import com.vicmns.demogmaillikedrawer.model.DrawerItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vicmns on 2/13/2015.
 */
public class ExpandedDrawerListAdapter extends BaseAdapter {
    private static final String TAG = "ListAdapter";
    private List<DrawerItemModel> mDrawerItemModels;
    private LayoutInflater mInflater;

    public ExpandedDrawerListAdapter(Context context, List<DrawerItemModel> drawerItemModels) {
        mDrawerItemModels = drawerItemModels;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDrawerItemModels.size();
    }

    @Override
    public DrawerItemModel getItem(int position) {
        return mDrawerItemModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        DrawerItemModel cItem = getItem(position);
        return (cItem.getItemType() == DrawerItemModel.ItemType.SIMPLE_SEPARATOR ||
                cItem.getItemType() == DrawerItemModel.ItemType.SECTION_SEPARATOR) ? 1 : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int type = getItemViewType(position);

        if(convertView == null) {
            if(type == 1) {
                //Separator
                convertView = mInflater.inflate(R.layout.expanded_drawer_list_separator, parent, false);
            } else {
                convertView = mInflater.inflate(R.layout.expanded_drawer_list_item, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                initViews(convertView, viewHolder);
                convertView.setTag(viewHolder);
            }
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if(viewHolder != null) {
            setViewsValues(viewHolder, position);
            checkForAlphaAnimation(viewHolder, position);
        }

        return convertView;
    }

    private void initViews(View rowView, ViewHolder viewHolder) {
        viewHolder.mDrawerListItemParentLayout = (LinearLayout) rowView.findViewById(R.id.drawer_list_item_parent_layout);
        viewHolder.mDrawerListItemSectionText = (TextView) rowView.findViewById(R.id.drawer_list_item_section_text);
        viewHolder.mDrawerListItemSectionImage = (ImageView) rowView.findViewById(R.id.drawer_list_item_section_image);
    }

    private void setViewsValues(ViewHolder viewHolder, int position) {
        if(mDrawerItemModels.get(position).getItemType() == DrawerItemModel.ItemType.SECTION_SEPARATOR ||
                mDrawerItemModels.get(position).getItemType() == DrawerItemModel.ItemType.SIMPLE_SEPARATOR) {
            return;
        }

        viewHolder.mDrawerListItemSectionText.setText(mDrawerItemModels.get(position).getItemText());
        viewHolder.mDrawerListItemSectionImage.setImageResource(mDrawerItemModels.get(position).getItemImageResId());
    }

    private void checkForAlphaAnimation(ViewHolder viewHolder, int position) {
        if(mDrawerItemModels.get(position).getItemType() == DrawerItemModel.ItemType.SECONDARY_ITEM) {
            //alphaAnimationViews.add(viewHolder.mDrawerListItemParentLayout);
        }
    }

    public void setCrossFadeSlidingListener(CrossFadeSlidingPanelLayout crossFadeSlidingPanelLayout) {

    }

    private static class ViewHolder {
        public LinearLayout mDrawerListItemParentLayout;
        public TextView mDrawerListItemSectionText;
        public ImageView mDrawerListItemSectionImage;
    }
}
