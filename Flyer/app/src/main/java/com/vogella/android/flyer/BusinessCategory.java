package com.vogella.android.flyer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by amritachowdhury on 8/11/16.
 */
public class BusinessCategory implements Parcelable {
    public static Creator<BusinessCategory> getCREATOR() {
        return CREATOR;
    }

    public String getAlias() {
        return alias;
    }

    public String getTitle() {
        return title;
    }

    private final static String TAG = "BUSINESS_CATEGORY";
    private String alias;
    private String title;

    public BusinessCategory() {}

    public BusinessCategory(String alias, String title) {
        this.alias = alias;
        this.title = title;
    }

    public BusinessCategory(Parcel source) {
        alias = source.readString();
        title = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(alias);
        dest.writeString(title);
    }

    public static final Creator<BusinessCategory> CREATOR = new Creator<BusinessCategory>() {
        @Override
        public BusinessCategory[] newArray(int size) {
            return new BusinessCategory[size];
        }

        @Override
        public BusinessCategory createFromParcel(Parcel source) {
            return new BusinessCategory(source);
        }
    };

    public ArrayList<BusinessCategory> parseBusinessCategory(JSONArray businessCategories){
        ArrayList<BusinessCategory> categories = new ArrayList<>();
        try {
            int categoryCount = businessCategories.length();
            for(int j= 0;  j< categoryCount;  j++ ) {
                JSONObject category = (JSONObject)businessCategories.get(j);
                String alias = category.has("alias") ? category.get("alias").toString() : "";
                String title = category.has("title") ? category.get("title").toString() : "";
                categories.add(new BusinessCategory(alias, title));
            }
        } catch(Exception e){
            Log.e(TAG, "Error in categories field", e);
        }
        return categories;
    }
}
