package com.vogella.android.flyer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.EditText;


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

    public final static  String YELP_BUSINESS = "";
    public final static  String YELP_SECRET_KEY = "0i0nf5Y4C40qcUMfHrmOKeU2fq4A99hFAcEa4tQNlihVtuON97PnXrqTPEoKOdNK";
    public final static  String YELP_CLIENT_ID = "pho1XNCTeRxQVzWR_5vacg";
    public final static  String YELP_AUTH_URL = "https://api.yelp.com/oauth2/token";

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
        String yelpUrl = yelpUrlEditText.getText().toString();
        YelpResultTask webserviceTask = new YelpResultTask();
        webserviceTask.execute(yelpUrl);
    }

    private class YelpResultTask extends AsyncTask<String, String, Yelp> {

        @Override
        protected Yelp doInBackground(String... params) {
            String accessToken =  getYelpAccessToken();
            BusinessDetail yelpBusiness = getYelpBusinessData(accessToken);
            ArrayList<Review> yelpReviews = getYelpReviews(accessToken);
            return new Yelp(yelpBusiness, yelpReviews);
        }

        @Override
        protected void onPostExecute(Yelp business) {
            super.onPostExecute(business);
            setYelpIntent(business);
            finish();
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
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();
                JSONObject json = readInputStream(urlConnection.getInputStream());
                return parseAccessToken(json);
            } catch (IOException e) {
                Log.e("bapi", "Error ", e);
                return "";
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        private BusinessDetail getYelpBusinessData(String accessToken) {
            HttpURLConnection urlConnection = null;

            try {
                //this is hardcoded now, we will get the value of the edit text box
                URL url = new URL("https://api.yelp.com/v3/businesses/north-india-restaurant-san-francisco");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                JSONObject json = readInputStream(urlConnection.getInputStream());
                return parseYelpData(json);
            } catch (IOException e) {
                Log.e("MainActivity", "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        private ArrayList<Review> getYelpReviews(String accessToken) {
            HttpURLConnection urlConnection = null;

            try {
                //this is hardcoded now, we will get the value of the edit text box
                URL url = new URL("https://api.yelp.com/v3/businesses/north-india-restaurant-san-francisco/reviews");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                JSONObject json = readInputStream(urlConnection.getInputStream());
                return parseYelpReview(json);
            } catch (IOException e) {
                Log.e("MainActivity", "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }

    protected JSONObject readInputStream(InputStream in) {
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
            Log.e("MainActivity", "Error", e);
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }

        }
    }

    protected BusinessDetail parseYelpData(JSONObject yelp) {
        BusinessDetail y = new BusinessDetail();
        return y.parseBusinessDetail(yelp);
    }

    protected ArrayList<Review> parseYelpReview(JSONObject reviewJson) {
        try {
            JSONArray reviewArray = reviewJson.getJSONArray("reviews");
            return reviewArray.length() >  0 ? new Review().parseReviews(reviewArray) : null;
        } catch(Exception e) {
            return null;

        }
    }

    protected String parseAccessToken(JSONObject json) {
        try {
            return json.get("access_token").toString();
        } catch(Exception e) {
            Log.e("PlaceholderFragment", "Error closing stream", e);
        }
        return "";
    }

    protected void setYelpIntent(Yelp business){
        if (business != null) {
            Intent intent = new Intent(this, FlyerActivity.class);
            intent.putExtra(YELP_BUSINESS, business);
            startActivity(intent);
        }
    }
}
