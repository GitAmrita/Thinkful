package com.vogella.android.flyer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.flurry.android.FlurryAgent;

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

    public static final String SHARED_PREFERENCES = "preferences" ;
    private final static String TAG = "MAIN_ACTIVITY";

    @Bind(R.id.sampleImg)
    protected ImageView sampleImage;
    @Bind(R.id.userInput)
    protected EditText yelpEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        calculateLayoutDimensions();
        FlurryAgent.setLogEnabled(false);
        FlurryAgent.init(this, Config.flurry.FLURRY_SESSION);
    }

    @OnClick(R.id.go)
    public void onGoClick() {
        UserInput u = new UserInput(yelpEditText.getText().toString().trim());
        if (u.getInputType() == UserInput.BAD_INPUT ||  u.getInput().isEmpty()) {
            ErrorDetail e = new ErrorDetail(u.getInputType(), this);
            e.showErrorToast(e.getErrorMessage());
        } else {
            YelpResultTask webserviceTask = new YelpResultTask();
            webserviceTask.execute(u);
        }
    }

    @OnClick(R.id.urlInput)
    public void onUrlClick() {
        yelpEditText.setHint(getResources().getString(R.string.yelp_url_hint));
    }

    @OnClick(R.id.phoneInput)
    public void onPhoneClick() {
        yelpEditText.setHint(getResources().getString(R.string.yelp_phone_hint));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void calculateLayoutDimensions() {
        ViewTreeObserver vto = sampleImage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sampleImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int widthPx = size.x;
                int heightPx = size.y;
                // 0.6, 0.5 have been arbitrarily chosen since such a layout looks good
                int requiredLayoutWidth = (int)(0.6 * widthPx);
                int requiredLayoutHeight = (int) (0.5 * heightPx);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        requiredLayoutWidth,requiredLayoutHeight);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.topMargin = 10;
                sampleImage.setLayoutParams(params);
            }
        });
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
            intent.putExtra(Config.api.YELP_BUSINESS, business);
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

            String accessToken =  generateYelpAccessToken();
            if (accessToken == null) {
                errors.add(new ErrorDetail(Config.api.YELP_ACCESS_TOKEN_ERROR, MainActivity.this));
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
            String token = readYelpAccessTokenFromSharedPreferences();
            if (token == null) {
                token = generateYelpAccessToken();
                writeYelpAccessTokenToSharedPreferences(token);
            }
            return token;
        }

        private String generateYelpAccessToken() {
            HttpURLConnection urlConnection = null;
            String postParameters = "grant_type=client_credentials&client_secret="
                    + Config.api.YELP_SECRET_KEY + "&client_id=" + Config.api.YELP_CLIENT_ID;
            try {
                URL url = new URL(Config.api.YELP_AUTH_URL);
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
                Log.e(TAG, "generateYelpAccessToken  ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        private void writeYelpAccessTokenToSharedPreferences(String token) {
            SharedPreferences.Editor editor = getSharedPreferences(
                    SHARED_PREFERENCES, MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            editor.putString(Config.api.YELP_ACCESS_TOKEN, token);
            editor.apply();
        }

        private String readYelpAccessTokenFromSharedPreferences() {
            SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
            String token = prefs.getString(Config.api.YELP_ACCESS_TOKEN, null);
            return token;
        }

        private String getYelpBusinessIdByPhone(String accessToken, String phone) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(Config.api.YELP_SEARCH_BY_PHONE + phone);
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
                URL url = new URL(Config.api.YELP_BUSINESS_API + businessId);
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
                URL url = new URL(Config.api.YELP_BUSINESS_API + businessId + "/reviews");
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
