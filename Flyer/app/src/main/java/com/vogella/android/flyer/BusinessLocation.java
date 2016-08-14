package com.vogella.android.flyer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import com.yelp.clientlib.entities.Business;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by amritachowdhury on 8/10/16.
 */

  public class BusinessLocation implements Parcelable {

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getAddress3() {
        return address3;
    }


    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public static Creator<BusinessLocation> getCREATOR() {
        return CREATOR;
    }

    private String country;
    private String state;
    private String zipCode;
    private String city;
    private String address1;
    private String address2;
    private String address3;
    private double longitude;
    private double latitude;

    public BusinessLocation() {}

    public BusinessLocation(String country, String state, String zipCode, String city,
                            String address1, String address2, String address3, double longitude,
                            double latitude) {
        this.country = country;
        this.state = state;
        this.zipCode = zipCode;
        this.city = city;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public BusinessLocation(Parcel source) {
        country = source.readString();
        state = source.readString();
        zipCode = source.readString();
        city = source.readString();
        address1 = source.readString();
        address2 = source.readString();
        address3 = source.readString();
        longitude = source.readDouble();
        latitude = source.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(country);
        dest.writeString(state);
        dest.writeString(zipCode);
        dest.writeString(city);
        dest.writeString(address1);
        dest.writeString(address2);
        dest.writeString(address3);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }

    public static final Creator<BusinessLocation> CREATOR = new Creator<BusinessLocation>() {
        @Override
        public BusinessLocation[] newArray(int size) {
            return new BusinessLocation[size];
        }

        @Override
        public BusinessLocation createFromParcel(Parcel source) {
            return new BusinessLocation(source);
        }
    };

    public BusinessLocation parseLocation(JSONObject location, JSONObject coordinates){
        try {
            String country = location.has("country") ? location.get("country").toString() : "";
            String state = location.has("state") ? location.get("state").toString() : "";
            String zipCode = location.has("zip_code") ? location.get("zip_code").toString() : "";
            String city = location.has("city") ? location.get("city").toString() : "";
            String address1 = location.has("address1") ? location.get("address1").toString() : "";
            String address2 = location.has("address2") ? location.get("address2").toString() : "";
            String address3 = location.has("address3") ? location.get("address3").toString() : "";
            Double longitude = coordinates.has("longitude") ?
                    (double)coordinates.get("longitude") : null;
            Double latitude = coordinates.has("latitude") ?
                    (double)coordinates.get("latitude") : null;
            return new BusinessLocation(country, state, zipCode, city, address1, address2, address3,
                    longitude, latitude);
        } catch(Exception e){
            Log.e("ParseLocation", "Error in parsing location/ coordinates", e);
            return null;
        }
    }
}
