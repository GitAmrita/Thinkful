package com.vogella.android.flyer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FlyerActivity extends AppCompatActivity {

    //1.7 is the aspect ratio we need for the business card
    private final static  double FLYER_ASPECT_RATIO = 1.7;
    private final static String TAG = "FLYER_ACTIVITY";
    private static final Map<Integer, String> week;
    static {
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
        Typeface oswald = Typeface.createFromAsset(getAssets(),"fonts/Oswald-Regular.ttf");
        businessName.setTypeface(oswald);
        businessAddress1.setTypeface(oswald);
        businessAddress2.setTypeface(oswald);
        businessPhone.setTypeface(oswald);
        yelpReview.setTypeface(oswald);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.shareBtnOnClick)
    public void onClick() {
        shareItWrapper();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        Yelp yelpBusiness = intent.getExtras().getParcelable(MainActivity.YELP_BUSINESS);
        mBusiness = yelpBusiness.getYelpBusiness();
        mReviews = yelpBusiness.getYelpReviews();
    }

    private void calculateLayoutDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width  = layout.getMeasuredWidth();
                int height = layout.getMeasuredHeight();
                int requiredLayoutWidth = width;
                int requiredLayoutHeight = (int)(FLYER_ASPECT_RATIO * width);
                if (requiredLayoutHeight > height) {
                    requiredLayoutHeight = height;
                    requiredLayoutWidth = (int)(height / FLYER_ASPECT_RATIO);
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

    private void setFlyerHeader(BusinessDetail business) {
        businessName.setText(business.getBusinessName());

        BusinessLocation location = business.getBusinessLocation();
        String streetAddress = location.getAddress1() + " " + location.getAddress2() + " " +
                location.getAddress3();
        String address1 = String.format("%s", streetAddress);
        String address2 = String.format("%s, %s %s",location.getCity(),
                location.getState(), location.getZipCode());
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

        String reviewsTotal = String.valueOf(business.getReviewCount()) + " " +
                getResources().getString(R.string.review);
        yelpReview.setText(reviewsTotal);
    }

    private void setHoursOfOperation(BusinessDetail business) {
        int weekDayCount = 0;
        Map<Integer, String> weekendTiming = new HashMap<>();
        Set weekdayTimings = new HashSet();
        ArrayList<OperationHour> hours = business.getOperationHours();
        ArrayList<OpenAndCloseTime> timings = hours.get(0).getOpenAndCloseTimes();
        for(OpenAndCloseTime timing : timings) {
            String opensAt = formatTime(timing.getOpensAt());
            String closesAt = formatTime(timing.getClosesAt());
            //if: weekday timings else: weekend timings
            if(timing.getDayOfWeek() >= 0 && timing.getDayOfWeek() < 5) {
                weekDayCount ++;
                weekdayTimings.add(opensAt  + " - " + closesAt);
            } else {
                weekendTiming.put(timing.getDayOfWeek(), opensAt  + " - " + closesAt);
            }
        }
        //make sure all 5 days of the week mon - fri have same timing
        if(weekDayCount == 5 && weekdayTimings.size() == 1) {
            weekDays.setText(getResources().getString(R.string.weekday) + " " +
                    weekdayTimings.toArray()[0].toString());
            String[] weekendTimings = getWeekendTimings(weekendTiming);
            saturday.setText(getResources().getString(R.string.sat_open) + " " + weekendTimings[0]);
            sunday.setText(getResources().getString(R.string.sun_open) + " " + weekendTimings[1]);
        } else {
            weekDays.setText(getResources().getString(R.string.call_for_time)) ;
        }
    }

    private String formatTime(String time) {
        return formatHours(time) + ":" + time.substring(2, 4) +
                getAMorPM(Integer.parseInt(time.substring(0, 2)));
    }

    private String getAMorPM(int time) {
        return   " " + (time <= 11 ? getResources().getString(R.string.am) :
                getResources().getString(R.string.pm));
    }

    private String formatHours(String time) {
        String hour = time.substring(0, 2);
        //replace 24 hr with 12 hr
        hour = String.valueOf(Integer.parseInt(hour) <= 11 ? hour : Integer.parseInt(hour) % 12);
        // hr == 00 : 12 am hr == 0 : 12 pm
        hour = hour.equals("00") || hour.equals("0") ? "12" : hour;
        hour = hour.charAt(0) == '0' ? String.valueOf(hour.charAt(1)): hour;
        return hour;
    }

    private String[] getWeekendTimings(Map<Integer, String> weekendTiming) {
        String[] s = new String[] { getResources().getString(R.string.sat_closed),
                getResources().getString(R.string.sun_closed)
        };
        int sat = 5;
        int sun = 6;
        if(weekendTiming.get(sat) != null) {
            s[0] = weekendTiming.get(sat);
        }
        if(weekendTiming.get(sun) != null) {
            s[1] = weekendTiming.get(sun);
        }
        return s;
    }

    private void shareItWrapper() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int REQUEST_CODE_ASK_PERMISSIONS = 123;
            int hasWriteExternalStoragePermission = checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
        shareIt();
    }

    private void shareIt() {
        //sharing implementation here
        saveJpeg();
        String shareBody = getResources().getString(R.string.share_body);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/jpeg");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                getResources().getString(R.string.share_subject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                getResources().getString(R.string.share_text));
        String imagePath = Environment.getExternalStorageDirectory()+ File.separator + "Pictures" +
                File.separator + "flyer.jpg";
        File imageFileToShare = new File(imagePath);
        Uri uri = Uri.fromFile(imageFileToShare);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent,
                getResources().getString(R.string.share_intent)));
    }

    private void saveJpeg(){
        layout.setDrawingCacheEnabled(true);
        layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
        layout.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(layout.getDrawingCache());
        layout.setDrawingCacheEnabled(false); // clear drawing cache

        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures");

        boolean doSave = true;
        if (!dir.exists()) {
            doSave = dir.mkdirs();
        }

        if (doSave) {
            ImageUtil.SaveBitmapToFile(dir,"flyer.jpg",b,Bitmap.CompressFormat.JPEG,100);
        }
        else {
            Log.e(TAG,"Couldn't create target directory.");
        }
    }

    private class DownloadImageTask extends AsyncTask<ArrayList<String>, Void, ArrayList<Bitmap>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            yelpImage.setBackgroundColor(getResources().getColor(R.color.Bg));
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(ArrayList<String>... params) {
            ArrayList<String>  urls= params[0];
            return ImageUtil.DownloadBitmapFromUrl(urls);
        }

        protected void onPostExecute(ArrayList<Bitmap> result) {
//            int imageViewWidth = yelpImage.getWidth() ;
//            Bitmap fullBleed = ImageUtil.generateFullBleedBitmap(result.get(0),
//                    imageViewWidth, imageViewWidth/2);
           yelpImage.setImageBitmap(result.get(0));
        }
    }
}
