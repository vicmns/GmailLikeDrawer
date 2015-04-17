package com.vicmns.demogmaillikedrawer;

/**
 * Created by vicmns on 2/13/2015.
 */
public class DrawerListItemModel {
    private int itemImageResId;
    private String itemText;

    public DrawerListItemModel(){};

    public DrawerListItemModel(int itemImageResId, String itemText) {
        this.itemImageResId = itemImageResId;
        this.itemText = itemText;
    }

    public int getItemImageResId() {
        return itemImageResId;
    }

    public void setItemImageResId(int itemImageResId) {
        this.itemImageResId = itemImageResId;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }
}
