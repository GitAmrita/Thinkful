package com.vogella.android.flyer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.io.FileOutputStream;
import java.io.IOException;
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
        Typeface oswald = Typeface.createFromAsset(getAssets(),"fonts/Oswald-Regular.ttf");
        businessName.setTypeface(oswald);
        businessAddress1.setTypeface(oswald);
        businessAddress2.setTypeface(oswald);
        businessPhone.setTypeface(oswald);
        yelpReview.setTypeface(oswald);
    }

    @OnClick(R.id.shareBtnOnClick)
    public void onClick() {
        shareItWrapper();
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
            saveBitmapToFile(dir,"flyer.jpg",b,Bitmap.CompressFormat.JPEG,100);
        }
        else {
            Log.e("app","Couldn't create target directory.");
        }
    }

    private void shareIt() {
        //sharing implementation here
        saveJpeg();
        String shareBody = "Here is the share content body";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/jpeg");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "I am Doge!");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Bork bork bork.");
        String imagePath = Environment.getExternalStorageDirectory()+ File.separator + "Pictures" + File.separator + "flyer.jpg";
        File imageFileToShare = new File(imagePath);
        Uri uri = Uri.fromFile(imageFileToShare);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share your string using"));
    }

    private void shareItWrapper() {
        try {
            int REQUEST_CODE_ASK_PERMISSIONS = 123;
            int hasWriteExternalStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
            shareIt();
        } catch(NoSuchMethodError e) {
            shareIt();
        }
    }

    public boolean saveBitmapToFile(File dir, String fileName, Bitmap bm,
                                    Bitmap.CompressFormat format, int quality) {

        File imageFile = new File(dir,fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bm.compress(format,quality,fos);
            fos.close();
            return true;
        }
        catch (IOException e) {
            Log.e("app",e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
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
        String sunTiming = sun + ": " + opensAt  + " - " + closesAt;
        sunday.setText(sunTiming);

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
//            int imageViewWidth = yelpImage.getWidth() ;
//            Bitmap fullBleed = ImageUtil.generateFullBleedBitmap(result.get(0),
//                    imageViewWidth, imageViewWidth/2);
           yelpImage.setImageBitmap(result.get(0));
        }
    }
}
