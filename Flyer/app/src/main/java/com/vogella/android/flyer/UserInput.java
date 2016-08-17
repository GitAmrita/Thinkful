package com.vogella.android.flyer;

import android.util.Log;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by amritachowdhury on 8/16/16.
 */
public class UserInput {
    public final static  String PHONE_REGEX = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$";
    public final static String PHONE_INPUT = "phone";
    public final static String URL_INPUT = "businessId";
    public final static String BAD_INPUT = "badInput";
    private final static String TAG = "USER_INPUT";
    private final static String US_COUNTRY_CODE_FORMATTED = "+1";

    public String getInput() {
        return input;
    }
    public String getInputType() {
        return inputType;
    }

    private String input;
    private String inputType;

    public UserInput(String inputString) {
        if (isValidUrl(inputString)) {
            this.input = getBusinessIdFromUrl(inputString);
            this.inputType = URL_INPUT;
        } else {
            inputString = removeCountryCode(inputString);
            if (isValidPhone(inputString)) {
                this.input = formatPhoneNumber(inputString);
                this.inputType = PHONE_INPUT;
            } else {
                this.input = null;
                this.inputType = BAD_INPUT;
            }
        }
    }

    private boolean isValidUrl(String input) {
        return (input.startsWith("https://www.yelp.com/biz/") ||
                input.startsWith("http://www.yelp.com/biz/") ||
                input.startsWith("www.yelp.com/biz/"));
    }

    private String getBusinessIdFromUrl(String yelpUrl) {
        try {
            yelpUrl = yelpUrl.startsWith("www.yelp.com") ? "https://" + yelpUrl : yelpUrl;
            URL url = new URL(yelpUrl);
            String[] parts = url.getFile().split("/");
            return parts.length >2 ? parts[2] : "";
        } catch(Exception e) {
            Log.e(TAG, "getBusinessIdFromUrl ", e );
        }
        return "";
    }

    private String removeCountryCode(String phone) {
        String removedCountryCode = "";
        for (char s : phone.toCharArray()) {
            if (Character.isDigit(s)) {
                removedCountryCode += s;
            }
        }
        int phoneNumberLength = removedCountryCode.length();
        return phoneNumberLength > 10 ? removedCountryCode.substring(1, phoneNumberLength )
                : removedCountryCode;
    }

    private boolean isValidPhone(String input) {
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    private String formatPhoneNumber(String phone) {
        return US_COUNTRY_CODE_FORMATTED + phone;
    }

}

