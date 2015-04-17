package com.vicmns.demogmaillikedrawer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import java.io.IOException;

public class DeviceProfileInformation {

    private static DeviceProfileInformation instance;

    public static DeviceProfileInformation getInstance(){
        if(instance == null)
            instance = new DeviceProfileInformation();

        return instance;
    }

    public Bitmap getDeviceProfileImage(Context context) {
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Profile.CONTENT_URI,
                new String[]{ContactsContract.Profile.PHOTO_URI},
                null,
                null,
                null);

        if(cursor.getCount() > 0){
            return getProfileImage(context, cursor);
        }else
            return getDefaultImage(context);
    }

    public String getDeviceUserName(Context context) {
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Profile.CONTENT_URI,
                new String[]{ContactsContract.Profile.DISPLAY_NAME_PRIMARY},
                null,
                null,
                null);

        cursor.moveToFirst();

        if(cursor.getCount() > 0)
            return cursor.getString(0);
        else
            return "";
    }

    public String getCurrentUserID(Context context) {
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Profile.CONTENT_URI,
                new String[]{ContactsContract.Profile._ID},
                null,
                null,
                null);

        cursor.moveToFirst();

        if(cursor.getCount() > 0)
            return cursor.getString(0);
        else
            return "";
    }

    private Bitmap getProfileImage(Context context, Cursor cursor) {
        cursor.moveToFirst();
        if(cursor.getString(0) == null)
            return getDefaultImage(context);
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(cursor.getString(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Bitmap getDefaultImage(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_default);
    }
}