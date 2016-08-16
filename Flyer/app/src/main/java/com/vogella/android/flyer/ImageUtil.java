package com.vogella.android.flyer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by amritachowdhury on 8/12/16.
 */
public class ImageUtil {

    public static ArrayList<Bitmap> DownloadBitmapFromUrl(ArrayList<String> urls) {
        ArrayList<Bitmap> bitmaps = new ArrayList();
        for(String url : urls) {
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            bitmaps.add(bitmap);
        }
        return bitmaps;
    }

    public static Bitmap GenerateFullBleedBitmap(Bitmap bitmap, int width, int height) {
        Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        resultBitmap.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(resultBitmap);

        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        Rect dstRectForRender = new Rect(0, 0, width, height);

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

    public static boolean SaveBitmapToFile(File dir, String fileName, Bitmap bm,
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
}
