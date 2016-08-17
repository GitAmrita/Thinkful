package com.vogella.android.flyer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by amritachowdhury on 8/11/16.
 */
public class Yelp implements Parcelable{

    public BusinessDetail getYelpBusiness() {
        return yelpBusiness;
    }

    public ArrayList<Review> getYelpReviews() {
        return yelpReviews;
    }

    public static Creator<Yelp> getCREATOR() {
        return CREATOR;
    }

    public ArrayList<ErrorDetail> getYelpErrors() {
        return yelpErrors;
    }

    public void setYelpErrors(ArrayList<ErrorDetail> yelpErrors) {
        this.yelpErrors = yelpErrors;
    }

    private BusinessDetail yelpBusiness;
    private ArrayList<Review> yelpReviews;
    private ArrayList<ErrorDetail> yelpErrors;

    public Yelp(){}

    public Yelp(BusinessDetail yelpBusiness, ArrayList<Review> yelpReviews,
                ArrayList<ErrorDetail> yelpErrors) {
        this.yelpBusiness = yelpBusiness;
        this.yelpReviews = yelpReviews;
        this.yelpErrors = yelpErrors;
    }

    public Yelp(Parcel source) {
        yelpBusiness = source.readParcelable(BusinessDetail.class.getClassLoader());
        yelpReviews = new ArrayList<>();
        source.readTypedList(yelpReviews, Review.CREATOR);
        yelpErrors = new ArrayList<>();
        source.readTypedList(yelpErrors, ErrorDetail.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(yelpBusiness, flags);
        dest.writeTypedList(yelpReviews);
        dest.writeTypedList(yelpErrors);
    }


    public static final Creator<Yelp> CREATOR = new Creator<Yelp>() {
        @Override
        public Yelp[] newArray(int size) {
            return new Yelp[size];
        }

        @Override
        public Yelp createFromParcel(Parcel source) {
            return new Yelp(source);
        }
    };
}
