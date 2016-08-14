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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public final static String REVIEW = "";
    public final static String YELP_BUSINESS = "";
    public final static  String YELP_ACCESS_TOKEN=
            "uf14Cl5SXkVdkjHxgfGzm6TumSgm_RqZoUZOeBrkJs0PAzl48WtZH8HswY6eNfauEVMsWmntUnnNS03bGDZ8U8NPaeJT8LbbhzP-7B1fIDM9iJTgZtLEOBG6iCKpV3Yx";

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

    private class YelpResultTask extends AsyncTask<String, String, Yelp>{

        @Override
        protected Yelp doInBackground(String... params) {
            ArrayList<Review> yelpReviews = getYelpReviews();
            BusinessDetail yelpBusiness = getYelpBusinessData();
            return new Yelp(yelpBusiness, yelpReviews);
        }

        @Override
        protected void onPostExecute(Yelp business) {
            super.onPostExecute(business);
            setYelpIntent(business);
            finish();
        }

        private BusinessDetail getYelpBusinessData(){
            HttpURLConnection urlConnection = null;

            try {
                //this is hardcoded now, we will get the value of the edit text box
                URL url = new URL("https://api.yelp.com/v3/businesses/north-india-restaurant-san-francisco");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + YELP_ACCESS_TOKEN);
                return parseYelpData(urlConnection.getInputStream());
            } catch (IOException e) {
                Log.e("MainActivity", "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        private ArrayList<Review> getYelpReviews(){
            HttpURLConnection urlConnection = null;

            try {
                //this is hardcoded now, we will get the value of the edit text box
                URL url = new URL("https://api.yelp.com/v3/businesses/north-india-restaurant-san-francisco/reviews");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + YELP_ACCESS_TOKEN);
                return parseYelpReview(urlConnection.getInputStream());
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

    protected BusinessDetail parseYelpData(InputStream in) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            JSONObject yelp = new JSONObject(stringBuilder.toString());
            BusinessDetail y = new BusinessDetail();
            return y.parseBusinessDetail(yelp);
        } catch (Exception e) {
            Log.e("MainActivity", "Error", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return null;

    }

    protected ArrayList<Review> parseYelpReview(InputStream in) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            JSONObject reviewJson = new JSONObject(stringBuilder.toString());
            if (reviewJson.has("reviews")) {
                JSONArray reviewArray = reviewJson.getJSONArray("reviews");
                return reviewArray.length() >  0 ? new Review().parseReviews(reviewArray) : null;
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return null;
    }

    protected void setReviewIntent(ArrayList<Review> review){
        if (review != null && review.size() > 0) {
            Intent intent = new Intent(this, FlyerActivity.class);
            intent.putExtra(REVIEW, review);
            startActivity(intent);
        }
    }

    protected void setYelpBusiness(BusinessDetail business){
        if (business != null) {
            Intent intent = new Intent(this, FlyerActivity.class);
            intent.putExtra(YELP_BUSINESS, business);
            startActivity(intent);
        }
    }

    protected void setYelpIntent(Yelp business){
        if (business != null) {
            Intent intent = new Intent(this, FlyerActivity.class);
            intent.putExtra(YELP_BUSINESS, business);
            startActivity(intent);
        }
    }
}
