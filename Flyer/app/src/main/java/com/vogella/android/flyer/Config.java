package com.vogella.android.flyer;

/**
 * Created by amritachowdhury on 8/22/16.
 */
public class Config {

    public static final class api {
        public final static  String YELP_SECRET_KEY = "0i0nf5Y4C40qcUMfHrmOKeU2fq4A99hFAcEa4tQNlihVtuON97PnXrqTPEoKOdNK";
        public final static  String YELP_CLIENT_ID = "pho1XNCTeRxQVzWR_5vacg";

        public final static  String YELP_AUTH_URL = "https://api.yelp.com/oauth2/token";
        public final static  String YELP_SEARCH_BY_PHONE = "https://api.yelp.com/v3/businesses/search/phone?phone=";
        public final static  String YELP_BUSINESS_API = "https://api.yelp.com/v3/businesses/";

        public final static  String YELP_ACCESS_TOKEN_ERROR = "YELP_ACCESS_TOKEN_ERROR";
        public final static  String YELP_ACCESS_TOKEN = " ";
        public final static  String YELP_BUSINESS = "";
    }

    public static final class flurry {
        public final static String FLURRY_SESSION = "W856ZMMJP8MTTBJVXJMJ";
    }

    public static final class flyer {
        //1.7 is the aspect ratio we need for the business card
        public final static  double FLYER_ASPECT_RATIO = 1.7;
    }
}
