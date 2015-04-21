package com.vicmns.demogmaillikedrawer.model;

/**
 * Created by vicmns on 2/13/2015.
 */
public class DrawerItemModel {
    public enum ItemType {MAIN_ITEM, SECONDARY_ITEM, SECTION_SEPARATOR, SIMPLE_SEPARATOR}
    private int mItemImageResId;
    private String mItemText;
    private ItemType mItemType;

    public DrawerItemModel(){};

    public DrawerItemModel(int itemImageResId, String itemText, ItemType itemType) {
        mItemImageResId = itemImageResId;
        mItemText = itemText;
        mItemType = itemType;
    }

    public int getItemImageResId() {
        return mItemImageResId;
    }

    public void setItemImageResId(int itemImageResId) {
        mItemImageResId = itemImageResId;
    }

    public String getItemText() {
        return mItemText;
    }

    public void setItemText(String itemText) {
        mItemText = itemText;
    }

    public ItemType getItemType() {
        return mItemType;
    }

    public void setItemType(ItemType mItemType) {
        this.mItemType = mItemType;
    }
}
