package com.vogella.android.flyer;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

/**
 * Created by amritachowdhury on 8/17/16.
 */
public class ErrorDetail implements Parcelable{
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Parcelable.Creator<ErrorDetail> getCREATOR() {
        return CREATOR;
    }

    private String message;
    private String type;
    Context context;

    public ErrorDetail(Context context) {
        this.context = context;
    }

    public ErrorDetail(String errorType, Context context) {
        this.context = context;
        this.type = errorType;
        this.message = getErrorMessage();
    }

    public ErrorDetail(Parcel source) {
        message = source.readString();
        type = source.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(type);
    }

    public static final Parcelable.Creator<ErrorDetail> CREATOR = new Parcelable.Creator<ErrorDetail>() {
        @Override
        public ErrorDetail[] newArray(int size) {
            return new ErrorDetail[size];
        }

        @Override
        public ErrorDetail createFromParcel(Parcel source) {
            return new ErrorDetail(source);
        }
    };

    public String getErrorMessage() {
        String message = "";
        switch(type){
            case UserInput.BAD_INPUT :
                message = context.getString(R.string.bad_input);
                break;
            case UserInput.PHONE_INPUT :
                message = context.getString(R.string.invalid_phone_number);
                break;
            case UserInput.URL_INPUT :
                message = context.getString(R.string.invalid_yelp_url);
                break;
            case MainActivity.YELP_ACCESS_TOKEN_ERROR :
                message = context.getString(R.string.yelp_access_token_error);
                break;
            default :
                message = context.getString(R.string.default_error);
                break;
        }
        return message;
    }

    public void showErrorToast(String message) {
        Toast.makeText(context, message,  Toast.LENGTH_LONG).show();
    }
}
