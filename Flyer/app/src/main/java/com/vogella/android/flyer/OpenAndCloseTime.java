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
public class OpenAndCloseTime implements Parcelable {

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public String getOpensAt() {
        return opensAt;
    }

    public String getClosesAt() {
        return closesAt;
    }

    public boolean isOvernight() {
        return isOvernight;
    }

    public static Creator<OpenAndCloseTime> getCREATOR() {
        return CREATOR;
    }

    private final static String TAG = "OPEN_AND_CLOSE_TIME";

    private int dayOfWeek;
    private String opensAt;
    private String closesAt;
    private boolean isOvernight;

    public OpenAndCloseTime() {}

    public OpenAndCloseTime(int dayOfWeek, String opensAt, String closesAt, boolean isOvernight) {
        this.dayOfWeek = dayOfWeek;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
        this.isOvernight = isOvernight;
    }

    public OpenAndCloseTime(Parcel source) {
        dayOfWeek = source.readInt();
        opensAt = source.readString();
        closesAt = source.readString();
        isOvernight = (source.readInt() == 0) ? false : true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dayOfWeek);
        dest.writeString(opensAt);
        dest.writeString(closesAt);
        dest.writeInt(isOvernight ? 1 : 0);
    }

    public static final Creator<OpenAndCloseTime> CREATOR = new Creator<OpenAndCloseTime>() {
        @Override
        public OpenAndCloseTime[] newArray(int size) {
            return new OpenAndCloseTime[size];
        }

        @Override
        public OpenAndCloseTime createFromParcel(Parcel source) {
            return new OpenAndCloseTime(source);
        }
    };

    public ArrayList<OpenAndCloseTime> parseOpenAndCloseTime(JSONArray businessHour){
        ArrayList<OpenAndCloseTime> hours = new ArrayList<>();
        try {
                int days = businessHour.length();
                for(int j= 0;  j< days;  j++ ) {
                    JSONObject day = (JSONObject)businessHour.get(j);
                    int dayOfWeek = day.has("day") ? (int) day.get("day") : j;
                    String opensAt = day.has("start") ? day.get("start").toString() : "";
                    String closesAt = day.has("end") ? day.get("end").toString() : "";
                    boolean isOvernight = day.has("is_overnight") ?
                            (boolean) day.get("is_overnight") : false;
                    OpenAndCloseTime time = new OpenAndCloseTime(dayOfWeek, opensAt, closesAt,
                            isOvernight);
                    hours.add(time);
                }
            } catch(Exception e){
            Log.e(TAG, "Error in open field", e);
        }
        return hours;
    }
}
