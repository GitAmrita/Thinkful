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

    public String getInput() {
        return input;
    }
    public String getInputType() {
        return inputType;
    }

    private String input;
    private String inputType;

    public UserInput(String inputString) {
        if (isValidPhone(inputString)) {
            this.input = formatPhoneNumber(inputString);
            this.inputType = PHONE_INPUT;
        } else if (isValidUrl(inputString)) {
            this.input = getBusinessIdFromUrl(inputString);
            this.inputType = URL_INPUT;
        } else {
            this.input = null;
            this.inputType = BAD_INPUT;
        }
    }

    private boolean isValidPhone(String input) {
        // allowing +countrycode in phone as valid and regex doesn't include that.
        if (input.length() < 10) {
            return false;
        }
        input = input.charAt(0) == '+'
                ? input.substring(2, input.length())
                : input;
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Matcher matcher = pattern.matcher(input.replace(" ",""));
        return matcher.matches();
    }

    private boolean isValidUrl(String input) {
        return (input.startsWith("https://www.yelp.com/biz/") ||
                input.startsWith("http://www.yelp.com/biz/") ||
        input.startsWith("www.yelp.com/biz/"));
    }

    private String formatPhoneNumber(String phone) {
        phone = phone.charAt(0) == '+' ? phone.substring(2, phone.length()) : phone;
        String formattedPhone = "+1";
        for (char s : phone.toCharArray()) {
            if (Character.isDigit(s)) {
                formattedPhone += s;
            }
        }
        return formattedPhone;
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
}

