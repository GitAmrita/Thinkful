package com.vogella.android.temperatureconverter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
/**
 * Created by amritachowdhury on 7/28/16.
 */
public class ConverterUtil {

    public static float convertFahrenheitToCelsius(float fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    public static float convertCelsiusToFahrenheit(float celsius) {
        return ((celsius * 9) / 5) + 32;
    }

    public static Bitmap generateFullBleedBitmap(Bitmap bitmap, int newResultSize) {
        Bitmap resultBitmap = Bitmap.createBitmap(newResultSize, newResultSize, Bitmap.Config.RGB_565);
        resultBitmap.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(resultBitmap);

        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        Rect dstRectForRender = new Rect(0, 0, newResultSize, newResultSize);

        int dstLeft = 0;
        int dstTop = 0;
        int dstRight = 0;
        int dstBottom = 0;

        if (imageHeight > imageWidth) {
            int newHeight = imageWidth;
            int margin = (imageHeight - newHeight) / 2;
            dstLeft = 0;
            dstTop = margin;
            dstRight = imageWidth;
            dstBottom = margin + newHeight;
        } else {
            int newWidth = imageHeight;
            int margin = (imageWidth - newWidth) / 2;
            dstLeft = margin;
            dstTop = 0;
            dstRight = margin + newWidth;
            dstBottom = imageHeight;

        }
        Rect src = new Rect(dstLeft, dstTop, dstRight, dstBottom);
        canvas.drawBitmap(bitmap, src, dstRectForRender, null);
        return resultBitmap;
    }



    public static ImageLoaderConfiguration getConfiguration(Context context, int memory) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .diskCacheExtraOptions(960, 960, null)
                .memoryCacheExtraOptions(400, 400)
                .threadPoolSize(4)
                .threadPriority(Thread.NORM_PRIORITY)
                .tasksProcessingOrder(QueueProcessingType.LIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(memory))
               // .diskCache(new UnlimitedDiscCache(getCachedDir(context))) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .writeDebugLogs()
               // .defaultDisplayImageOptions(getDisplayOptions())
                .build();
        return config;
    }
}
