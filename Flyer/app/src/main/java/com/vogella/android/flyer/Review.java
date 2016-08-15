package com.vogella.android.flyer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by amritachowdhury on 8/9/16.
 */
public class Review implements Parcelable {

    public String getReviewUrl(){
        return this.reviewUrl;
    }

    public String getReviewerProfileImageUrl(){
        return this.reviewerProfileImageUrl;
    }

    public String getReviewerName(){
        return this.reviewerName;
    }

    public String getReviewComment(){
        return this.reviewComment;
    }

    public int getRating(){
        return this.rating;
    }

    public static Creator<Review> getCREATOR() {
        return CREATOR;
    }

    private final static String TAG = "REVIEW";

    private String reviewUrl;
    private String reviewerProfileImageUrl;
    private String reviewerName;
    private String reviewComment;
    private int rating;

    public Review(){}

    public Review (String reviewUrl, String reviewerProfileImageUrl, String reviewerName,
                   String reviewComment, int rating ){
        this.reviewUrl = reviewUrl;
        this.reviewerProfileImageUrl = reviewerProfileImageUrl;
        this.reviewerName = reviewerName;
        this.reviewComment = reviewComment;
        this.rating = rating;
    }

    public Review(Parcel source) {
        reviewUrl = source.readString();
        reviewerProfileImageUrl = source.readString();
        reviewerName = source.readString();
        reviewComment = source.readString();
        rating = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reviewUrl);
        dest.writeString(reviewerProfileImageUrl);
        dest.writeString(reviewerName);
        dest.writeString(reviewComment);
        dest.writeInt(rating);
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }

        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }
    };

    public Review parseReview(JSONObject review){
        reviewUrl = parseReviewUrl(review);
        reviewerProfileImageUrl = parseReviewerProfileImageUrl(review);
        reviewerName = parseReviewerName(review);
        reviewComment = parseComment(review);
        rating = parseRating(review);
        return new Review(reviewUrl, reviewerProfileImageUrl, reviewerName, reviewComment,  rating);
    }

    public ArrayList<Review> parseReviews(JSONArray yelpReviews){
        ArrayList<Review> reviews = new ArrayList<>();
        try {
            int reviewCount = yelpReviews.length();
            for(int j= 0;  j< reviewCount;  j++ ) {
                reviews.add(parseReview((JSONObject)yelpReviews.get(j)));
            }
        } catch(Exception e){
            Log.e(TAG, "Error in reviews field", e);
        }
        return reviews;
    }

    private int parseRating(JSONObject review){
        try {
            return review.has("rating") ? (int) review.get("rating") : 0;
        } catch(Exception e){
            Log.e(TAG, "Error in rating field", e);
            return 0;
        }
    }

    private String parseReviewerProfileImageUrl(JSONObject review){
        try {
            if (review.has("user")) {
                JSONObject user = (JSONObject) review.get("user");
                return user.has("image_url") ?  user.get("image_url").toString() : "";
            } else {
                return "";
            }
        } catch(Exception e){
            Log.e(TAG, "Error in image_url field", e);
            return "";
        }
    }

    private String parseReviewerName(JSONObject review){
        try {
            if (review.has("user")) {
                JSONObject user = (JSONObject) review.get("user");
                return user.has("name") ?  user.get("name").toString() : "";
            } else {
                return "";
            }
        } catch(Exception e){
            Log.e(TAG, "Error in name field", e);
            return "";
        }
    }

    private String parseComment(JSONObject review){
        try {
            return review.has("text") ?  review.get("text").toString() : "";
        } catch(Exception e){
            Log.e(TAG, "Error in comment field", e);
            return "";
        }
    }

    private String parseReviewUrl(JSONObject review){
        try {
            return review.has("url") ?  review.get("url").toString() : "";
        } catch(Exception e){
            Log.e(TAG, "Error in review url field", e);
            return "";
        }
    }
}
