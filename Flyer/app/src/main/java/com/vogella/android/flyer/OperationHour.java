package com.vogella.android.flyer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by amritachowdhury on 8/10/16.
 */

public class OperationHour implements Parcelable {

    public boolean isOpenNow() {
        return isOpenNow;
    }

    public ArrayList<OpenAndCloseTime> getOpenAndCloseTimes() {
        return openAndCloseTimes;
    }

    public String getHoursType() {
        return hoursType;
    }

    public static Creator<OperationHour> getCREATOR() {
        return CREATOR;
    }

    private boolean isOpenNow;
    private String hoursType;


    private ArrayList<OpenAndCloseTime> openAndCloseTimes;

    public OperationHour() {}

    public OperationHour(boolean isOpenNow, String hoursType,
                         ArrayList<OpenAndCloseTime> openAndCloseTimes) {
        this.isOpenNow = isOpenNow;
        this.hoursType = hoursType;
        this.openAndCloseTimes = openAndCloseTimes;
    }

    public OperationHour(Parcel source) {
        isOpenNow = (source.readInt() == 0) ? false : true;
        hoursType = source.readString();
        openAndCloseTimes = new ArrayList<>();
        source.readTypedList(openAndCloseTimes, OpenAndCloseTime.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isOpenNow ? 1 : 0);
        dest.writeString(hoursType);
        dest.writeTypedList(openAndCloseTimes);
    }

    public static final Creator<OperationHour> CREATOR = new Creator<OperationHour>() {
        @Override
        public OperationHour[] newArray(int size) {
            return new OperationHour[size];
        }

        @Override
        public OperationHour createFromParcel(Parcel source) {
            return new OperationHour(source);
        }
    };

    public ArrayList<OperationHour> parseOperationHours(JSONArray operationHours){
        ArrayList<OperationHour> hours = new ArrayList<>();
        try {
            for (int i = 0; i < operationHours.length(); i++){
                JSONObject v = (JSONObject)operationHours.get(i);
                boolean isOpen = v.has("is_open_now") ? (boolean)v.get("is_open_now") : false;
                String hoursType = v.has("hours_type") ? v.get("hours_type").toString() : "";
                if (v.has("open") ) {
                    OpenAndCloseTime o = new OpenAndCloseTime();
                    ArrayList<OpenAndCloseTime> timing = o.parseOpenAndCloseTime(
                            (JSONArray) v.get("open"));
                    OperationHour h = new OperationHour(isOpen, hoursType, timing);
                    hours.add(h);
                } else {
                    OperationHour h = new OperationHour(isOpen, hoursType, null);
                    hours.add(h);
                }
            }
        } catch(Exception e){
            Log.e("ParseYelpBusiness", "Error in hours field", e);
        }
        return hours;
    }
}
