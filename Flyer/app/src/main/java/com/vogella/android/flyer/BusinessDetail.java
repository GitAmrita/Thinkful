package com.vogella.android.flyer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by amritachowdhury on 8/10/16.
 */
public class BusinessDetail implements Parcelable {

    public String getBusinessId() {
        return businessId;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public ArrayList<String> getPhotos() {
        if (photos!= null) {
            return photos;
        } else {
            return null;
        }
    }

    public double getAverageRating() {
        return averageRating;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getPriceCategory() {
        return priceCategory;
    }

    public String getYelpUrl() {
        return yelpUrl;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public BusinessLocation getBusinessLocation() {
        return businessLocation;
    }

    public ArrayList<OperationHour> getOperationHours() {
        return operationHours;
    }

    public static Creator<BusinessDetail> getCREATOR() {
        return CREATOR;
    }

    private final static String TAG = "BUSINESS_DETAIL";

    private String businessId;
    private String yelpUrl;
    private String businessPhone;
    private double averageRating;
    private String businessName;
    private String priceCategory;
    private int reviewCount;
    private ArrayList<String> photos;
    private BusinessLocation businessLocation;
    private ArrayList<OperationHour> operationHours;
    private ArrayList<BusinessCategory> categories;

    public BusinessDetail() {}

    public BusinessDetail(String businessId, String yelpUrl, String businessPhone,
                          double averageRating, String businessName, String priceCategory,
                          ArrayList<String> photos, BusinessLocation businessLocation,
                          ArrayList<OperationHour> operationHours, int reviewCount,
                          ArrayList<BusinessCategory> categories) {
        this.businessId = businessId;
        this.yelpUrl = yelpUrl;
        this.businessPhone = businessPhone;
        this.averageRating = averageRating;
        this.businessName = businessName;
        this.priceCategory = priceCategory;
        this.photos = photos;
        this.businessLocation = businessLocation;
        this.operationHours = operationHours;
        this.reviewCount = reviewCount;
        this.categories = categories;
    }

    public BusinessDetail(Parcel source) {
        businessId = source.readString();
        yelpUrl = source.readString();
        businessPhone = source.readString();
        averageRating = source.readDouble();
        businessName = source.readString();
        priceCategory = source.readString();
        photos = source.createStringArrayList();
        businessLocation = source.readParcelable(BusinessLocation.class.getClassLoader());
        operationHours = new ArrayList<>();
        source.readTypedList(operationHours, OperationHour.CREATOR);
        reviewCount = source.readInt();
        categories = new ArrayList<>();
        source.readTypedList(categories, BusinessCategory.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(businessId);
        dest.writeString(yelpUrl);
        dest.writeString(businessPhone);
        dest.writeDouble(averageRating);
        dest.writeString(businessName);
        dest.writeString(priceCategory);
        dest.writeStringList(photos);
        dest.writeParcelable(this.businessLocation, flags);
        dest.writeTypedList(operationHours);
        dest.writeInt(reviewCount);
        dest.writeTypedList(categories);
    }

    public static final Creator<BusinessDetail> CREATOR = new Creator<BusinessDetail>() {
        @Override
        public BusinessDetail[] newArray(int size) {
            return new BusinessDetail[size];
        }

        @Override
        public BusinessDetail createFromParcel(Parcel source) {
            return new BusinessDetail(source);
        }
    };

    public BusinessDetail parseBusinessDetail(JSONObject yelpBusiness){
        businessId = parseBusinessId(yelpBusiness);
        yelpUrl = parseYelpUrl(yelpBusiness);
        businessPhone = parseBusinessPhone(yelpBusiness);
        averageRating = parseAverageRating(yelpBusiness);
        businessName = parseBusinessName(yelpBusiness);
        priceCategory = parsePriceCategory(yelpBusiness);
        photos = parsePhotos(yelpBusiness);
        businessLocation = parseBusinessLocation(yelpBusiness);
        operationHours = parseOperationHours(yelpBusiness);
        reviewCount = parseReviewCount(yelpBusiness);
        categories = parseCategories(yelpBusiness);
        return new BusinessDetail(businessId, yelpUrl,businessPhone, averageRating, businessName,
                priceCategory, photos, businessLocation, operationHours, reviewCount, categories);
    }

    private String parseBusinessId(JSONObject yelpBusiness){
        try {
            return (yelpBusiness.has("id")) ? yelpBusiness.get("id").toString() : "";
        } catch(Exception e){
            Log.e(TAG, "Error in id field", e);
            return "";
        }
    }

    private String parseYelpUrl(JSONObject yelpBusiness){
        try {
            return (yelpBusiness.has("url")) ? yelpBusiness.get("url").toString() : "";
        } catch(Exception e){
            Log.e(TAG, "Error in url field", e);
            return "";
        }
    }

    private String parseBusinessPhone(JSONObject yelpBusiness){
        try {
            return (yelpBusiness.has("phone")) ? yelpBusiness.get("phone").toString() : "";
        } catch(Exception e){
            Log.e(TAG, "Error in phone field", e);
            return "";
        }
    }

    private double parseAverageRating(JSONObject yelpBusiness){
        try {
            return (yelpBusiness.has("rating")) ? (double)yelpBusiness.get("rating") : 0;
        } catch(Exception e){
            Log.e(TAG, "Error in rating field", e);
            return 0;
        }
    }

    private String parseBusinessName(JSONObject yelpBusiness){
        try {
            return (yelpBusiness.has("name")) ? yelpBusiness.get("name").toString() : "";
        } catch(Exception e){
            Log.e(TAG, "Error in name field", e);
            return "";
        }
    }

    private String parsePriceCategory(JSONObject yelpBusiness){
        try {
            return (yelpBusiness.has("price")) ? yelpBusiness.get("price").toString() : "";
        } catch(Exception e){
            Log.e(TAG, "Error in price field", e);
            return "";
        }
    }

    private ArrayList<String> parsePhotos(JSONObject yelpBusiness){
        // the api returns same image with different protocol like http https.
        // imageFile is used to weed out the duplicates images even though they have different protocols
        Set<String> imageFile = new HashSet<>();
        ArrayList<String> photos = new ArrayList<>();
        try {
            if (yelpBusiness.has("photos")) {
                JSONArray jsonArray = yelpBusiness.getJSONArray("photos");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String imageURL = jsonArray.get(i).toString();
                    URL url = new URL(imageURL);
                    String file = url.getFile();
                    if (! imageFile.contains(file)) {
                        photos.add(imageURL);
                        imageFile.add(file);
                    }
                }
            }
            if (yelpBusiness.has("image_url")) {
                URL url = new URL(yelpBusiness.get("image_url").toString());
                if (! imageFile.contains(url.getFile())) {
                    photos.add(url.toString());
                    imageFile.add(url.getFile());
                }
            }
        } catch(Exception e){
            Log.e(TAG, "Error in photos field", e);
        }
        return new ArrayList(photos);
    }

    private BusinessLocation parseBusinessLocation(JSONObject yelpBusiness){
        try {
            JSONObject location = yelpBusiness.has("location") ?
                    (JSONObject) yelpBusiness.get("location") : null;
            JSONObject coordinates = yelpBusiness.has("coordinates") ?
                    (JSONObject) yelpBusiness.get("coordinates") : null;
            return (location == null && coordinates == null) ? null :
                    new BusinessLocation().parseLocation(location, coordinates);
        } catch(Exception e){
            Log.e(TAG, "Error in location/ coordinate field", e);
            return null;
        }
    }

    private ArrayList<OperationHour> parseOperationHours(JSONObject yelpBusiness){
        try {
            if (yelpBusiness.has("hours")) {
                OperationHour  oh = new OperationHour();
                return oh.parseOperationHours((JSONArray) yelpBusiness.get("hours"));
            } else {
                return null;
            }
        } catch(Exception e){
            Log.e(TAG, "Error in hours field", e);
            return null;
        }
    }

    private int parseReviewCount(JSONObject yelpBusiness){
        try {
            return (yelpBusiness.has("review_count")) ? (int) yelpBusiness.get("review_count") : 0;
        } catch(Exception e){
            Log.e("ParseYelpBusiness", "Error in id field", e);
            return 0;
        }
    }

    private ArrayList<BusinessCategory> parseCategories(JSONObject yelpBusiness){
        try {
            if (yelpBusiness.has("categories")) {
                BusinessCategory  bc = new BusinessCategory();
                return bc.parseBusinessCategory((JSONArray) yelpBusiness.get("categories"));
            } else {
                return null;
            }
        } catch(Exception e){
            Log.e(TAG, "Error in hours field", e);
            return null;
        }
    }
}
