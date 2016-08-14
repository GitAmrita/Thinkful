package com.vogella.android.flyer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FlyerActivity extends AppCompatActivity {

    private static final Map<Integer, String> week;
    static
    {
        week = new HashMap();
        week.put(0, "Mon");
        week.put(1, "Tue");
        week.put(2, "Wed");
        week.put(3, "Thu");
        week.put(4, "Fri");
        week.put(5, "Sat");
        week.put(6, "Sun");
    }

    @Bind(R.id.headerBusiness)
    protected TextView businessName;
    @Bind(R.id.headerAddress1)
    protected TextView businessAddress1;
    @Bind(R.id.headerAddress2)
    protected TextView businessAddress2;
    @Bind(R.id.headerPhone)
    protected TextView businessPhone;

    @Bind(R.id.flyer)
    protected  LinearLayout layout;

    @Bind(R.id.yelpImage)
    protected ImageView yelpImage;

    @Bind(R.id.ratingBar)
    protected RatingBar ratingBar;
    @Bind(R.id.yelpReview)
    protected TextView yelpReview;

    @Bind(R.id.timingWeekdays)
    protected TextView weekDays;
    @Bind(R.id.timingSaturday)
    protected TextView saturday;
    @Bind(R.id.timingSunday)
    protected TextView sunday;

    private BusinessDetail mBusiness;
    private ArrayList<Review> mReviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flyer);
        ButterKnife.bind(this);
        calculateLayoutDimensions();
        getDataFromIntent();
        setFlyerHeader(mBusiness);
        setYelpImages(mBusiness);
        setYelpRatings(mBusiness);
        setHoursOfOperation(mBusiness);
    }

    @OnClick(R.id.shareBtnOnClick)
    public void onClick() {
        Toast.makeText(getBaseContext(), "Landen: Your code goes here",
                Toast.LENGTH_LONG).show();
    }

    private void calculateLayoutDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width  = layout.getMeasuredWidth();
                int height = layout.getMeasuredHeight();
                int requiredLayoutWidth = width;
                //1.7 is the aspect ratio we need for the flyer
                int requiredLayoutHeight = (int)(1.7 * width);
                if (requiredLayoutHeight > height) {
                    requiredLayoutHeight = height;
                    requiredLayoutWidth = (int)(height / 1.7);
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        requiredLayoutWidth,requiredLayoutHeight);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                layout.setLayoutParams(params);

                LinearLayout.LayoutParams yelpImageParams = new LinearLayout.LayoutParams(
                        requiredLayoutWidth,requiredLayoutWidth/2);
                yelpImage.setLayoutParams(yelpImageParams);

            }
        });

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        Yelp yelpBusiness = intent.getExtras().getParcelable(MainActivity.YELP_BUSINESS);
        mBusiness = yelpBusiness.getYelpBusiness();
        mReviews = yelpBusiness.getYelpReviews();
    }

    private void setFlyerHeader(BusinessDetail business) {
        businessName.setText(business.getBusinessName());

        BusinessLocation location = business.getBusinessLocation();


        String streetAddress = location.getAddress1() + location.getAddress2() +
                location.getAddress3();

        String city = location.getCity();

        String address1 = String.format("%s %s", streetAddress, city);
        String address2 = String.format("%s %s", location.getState(), location.getZipCode());

        businessAddress1.setText(address1);
        businessAddress2.setText(address2);

        String phone = business.getBusinessPhone();
        String formattedPhoneNumber = String.format("(%s) %s-%s", phone.substring(2, 5),
                phone.substring(5, 8), phone.substring(8, 12));
        businessPhone.setText(formattedPhoneNumber);
    }

    private void setYelpImages(BusinessDetail business) {
        ArrayList<String> imageUrls = new ArrayList<>();
        //Just add 1 image to the arraylist as per the template demand
        imageUrls.add(business.getPhotos().get(0));
        new DownloadImageTask().execute(imageUrls);
    }

    private void setYelpRatings(BusinessDetail business) {
        float yelpRating = (float)business.getAverageRating();
        ratingBar.setRating(yelpRating);

        String reviewsTotal = String.valueOf(business.getReviewCount()) + " reviews";
        yelpReview.setText(reviewsTotal);
    }

    private void setHoursOfOperation(BusinessDetail business) {

        ArrayList<OperationHour> hours = business.getOperationHours();
        ArrayList<OpenAndCloseTime> timings = hours.get(0).getOpenAndCloseTimes();
        String monToFri = week.get( timings.get(0).getDayOfWeek())
                + " - " + week.get( timings.get(4).getDayOfWeek());
        String opensAt = formatTime(timings.get(0).getOpensAt());
        String closesAt = formatTime(timings.get(0).getClosesAt());
        String weekdayTiming = monToFri + ": " + opensAt  + " - " + closesAt;
        weekDays.setText(weekdayTiming);

        String sat = week.get( timings.get(5).getDayOfWeek());
        opensAt = formatTime(timings.get(5).getOpensAt());
        closesAt = formatTime(timings.get(5).getClosesAt());
        String satTiming = sat + ": " + opensAt  + " - " + closesAt;
        saturday.setText(satTiming);

        String sun = week.get( timings.get(6).getDayOfWeek());
        opensAt = formatTime(timings.get(6).getOpensAt());
        closesAt = formatTime(timings.get(6).getClosesAt());
        String sunTiming = sat + ": " + opensAt  + " - " + closesAt;
        sunday.setText(satTiming);

    }

    private String formatTime(String time) {
        float f = Float.parseFloat(time.substring(0, 2));
        String clock = f <= 11 ? " am" : " pm";
        String hours = String.valueOf(f <= 11 ? time.substring(0, 2) : f % 12);
        String min = time.substring(2, 4);
        return hours.substring(0, 2) + ":" + min + clock;
    }

    private class DownloadImageTask extends AsyncTask<ArrayList<String>, Void, ArrayList<Bitmap>> {

        @Override
        protected ArrayList<Bitmap> doInBackground(ArrayList<String>... params) {
            ArrayList<String>  urls= params[0];
            return ImageUtil.DownloadBitmapFromUrl(urls);
        }

        protected void onPostExecute(ArrayList<Bitmap> result) {
            int imageViewWidth = yelpImage.getWidth() ;
            Bitmap fullBleed = ImageUtil.generateFullBleedBitmap(result.get(0),
                    imageViewWidth, imageViewWidth/2);
           yelpImage.setImageBitmap(fullBleed);
        }
    }
}
