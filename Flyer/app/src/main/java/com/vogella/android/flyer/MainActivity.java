package com.vogella.android.flyer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    //phone api returned error: business id api and review api works
    //https://api.yelp.com/v3/businesses/search/phone?phone=+16503866471
    //https://www.yelp.com/biz/the-voya-restaurant-mountain-view

    public final static  String YELP_BUSINESS = "";
    public final static  String YELP_SECRET_KEY = "0i0nf5Y4C40qcUMfHrmOKeU2fq4A99hFAcEa4tQNlihVtuON97PnXrqTPEoKOdNK";
    public final static  String YELP_CLIENT_ID = "pho1XNCTeRxQVzWR_5vacg";
    public final static  String YELP_AUTH_URL = "https://api.yelp.com/oauth2/token";
    public final static  String YELP_SEARCH_BY_PHONE = "https://api.yelp.com/v3/businesses/search/phone?phone=";
    public final static  String YELP_BUSINESS_API = "https://api.yelp.com/v3/businesses/";
    private final static String TAG = "MAIN_ACTIVITY";
    public final static String YELP_ACCESS_TOKEN_ERROR = "YELP_ACCESS_TOKEN_ERROR";


    @Bind(R.id.yelpUrl)
    protected EditText yelpUrlEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.go)
    public void onClick() {
        UserInput u = new UserInput(yelpUrlEditText.getText().toString().trim());
        if (u.getInputType() == UserInput.BAD_INPUT ||  u.getInput().isEmpty()) {
            ErrorDetail e = new ErrorDetail(u.getInputType(), this);
            e.showErrorToast(e.getErrorMessage());
        } else {
            YelpResultTask webserviceTask = new YelpResultTask();
            webserviceTask.execute(u);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private JSONObject readInputStream(InputStream in) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            return new JSONObject(stringBuilder.toString());
        } catch (Exception e) {
            Log.e(TAG, "readInputStream ", e);
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream ", e);
                }
            }

        }
    }

    private String parseAccessToken(JSONObject json) {
        try {
            return json.get("access_token").toString();
        } catch(Exception e) {
            Log.e(TAG, "parse access token", e);
        }
        return "";
    }

    private String parseYelpBusinessId(JSONObject json) {
        try {
            JSONArray businessArray = json.getJSONArray("businesses");
            JSONObject firstBusiness = (JSONObject) businessArray.get(0);
            return firstBusiness.has("id") ? firstBusiness.get("id").toString() : null;
        } catch(Exception e) {
            return null;

        }
    }

    private BusinessDetail parseYelpBusinessData(JSONObject yelpBusinessJson) {
        BusinessDetail y = new BusinessDetail();
        return y.parseBusinessDetail(yelpBusinessJson);
    }

    private ArrayList<Review> parseYelpReview(JSONObject reviewJson) {
        try {
            JSONArray reviewArray = reviewJson.getJSONArray("reviews");
            return reviewArray.length() >  0 ? new Review().parseReviews(reviewArray) : null;
        } catch(Exception e) {
            return null;

        }
    }

    private void setYelpIntent(Yelp business) {
        if (business != null) {
            Intent intent = new Intent(this, FlyerActivity.class);
            intent.putExtra(YELP_BUSINESS, business);
            startActivity(intent);
        }
    }



    private class YelpResultTask extends AsyncTask<UserInput, String, Yelp> {

        private Toast inProgressToast = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inProgressToast = Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.in_progress), Toast.LENGTH_SHORT);
            inProgressToast.show();
        }

        @Override
        protected Yelp doInBackground(UserInput... params) {
            ArrayList<ErrorDetail> errors = new ArrayList<>();
            UserInput input = params[0];
            String businessId;

            String accessToken =  getYelpAccessToken();
            if (accessToken == null) {
                errors.add(new ErrorDetail(YELP_ACCESS_TOKEN_ERROR, MainActivity.this));
                return new Yelp(null, null, errors);
            }

            businessId = input.getInputType() == UserInput.PHONE_INPUT
                    ? getYelpBusinessIdByPhone(accessToken, input.getInput()) : input.getInput();
            BusinessDetail yelpBusiness = getYelpBusinessData(accessToken, businessId);
            if (yelpBusiness == null) {
                errors.add(new ErrorDetail(input.getInputType(), MainActivity.this));
                return new Yelp(null, null, errors);
            }
            // for now we are not considering reviews so just sending reviews = null in yelp()
            return new Yelp(yelpBusiness, null, null);
           /* ArrayList<Review> yelpReviews = getYelpReviews(accessToken, businessId);
            return new Yelp(yelpBusiness, yelpReviews); */
        }


        @Override
        protected void onPostExecute(Yelp business) {
            inProgressToast.cancel();
            if (business.getYelpErrors() != null) {
                Log.e(TAG, business.getYelpErrors().get(0).getMessage() );
                ErrorDetail e = new ErrorDetail(MainActivity.this);
                e.showErrorToast(business.getYelpErrors().get(0).getMessage());
            } else {
                super.onPostExecute(business);
                setYelpIntent(business);
                finish();
            }
        }

        private String getYelpAccessToken() {
            HttpURLConnection urlConnection = null;
            String postParameters = "grant_type=client_credentials&client_secret="
                    + YELP_SECRET_KEY + "&client_id=" + YELP_CLIENT_ID;
            try {
                URL url = new URL(YELP_AUTH_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty(
                        "Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();
                JSONObject json = readInputStream(urlConnection.getInputStream());
                return parseAccessToken(json);
            } catch (IOException e) {
                Log.e(TAG, "getYelpAccessToken  ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        private String getYelpBusinessIdByPhone(String accessToken, String phone) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(YELP_SEARCH_BY_PHONE + phone);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                JSONObject json = readInputStream(urlConnection.getInputStream());
                return parseYelpBusinessId(json);
            } catch (IOException e) {
                Log.e(TAG, "getYelpBusinessIdByPhone ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        private BusinessDetail getYelpBusinessData(String accessToken, String businessId) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(YELP_BUSINESS_API + businessId);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                JSONObject json = readInputStream(urlConnection.getInputStream());
                return parseYelpBusinessData(json);
            } catch (IOException e) {
                Log.e(TAG, "getYelpBusinessData ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        private ArrayList<Review> getYelpReviews(String accessToken, String businessId) {
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(YELP_BUSINESS_API + businessId + "/reviews");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                JSONObject json = readInputStream(urlConnection.getInputStream());
                return parseYelpReview(json);
            } catch (IOException e) {
                Log.e(TAG, "getYelpReviews ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
}
