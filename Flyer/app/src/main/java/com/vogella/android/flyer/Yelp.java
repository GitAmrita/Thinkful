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

    private BusinessDetail yelpBusiness;
    private ArrayList<Review> yelpReviews;

    public Yelp(){}

    public Yelp(BusinessDetail yelpBusiness, ArrayList<Review> yelpReviews) {
        this.yelpBusiness = yelpBusiness;
        this.yelpReviews = yelpReviews;
    }

    public Yelp(Parcel source) {
        yelpBusiness = source.readParcelable(BusinessDetail.class.getClassLoader());
        yelpReviews = new ArrayList<>();
        source.readTypedList(yelpReviews, Review.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(yelpBusiness, flags);
        dest.writeTypedList(yelpReviews);
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
