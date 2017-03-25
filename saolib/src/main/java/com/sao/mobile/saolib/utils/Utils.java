package com.sao.mobile.saolib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Seb on 03/08/2016.
 */
public class Utils {
    private Utils() {}

    /**
     * Method converts iso date string to better readable form.
     *
     * @param isoDate input iso date. Example: "2016-04-13 13:21:04".
     * @return processed date string.
     */
    public static String parseDate(String isoDate) {
        try {
            String[] parts = isoDate.split("-");

            String year = parts[0];
            String month = parts[1];

            String dayTemp = parts[2];
            String[] parts2 = dayTemp.split(" ");
            String day = parts2[0].trim();
            if (day.length() > 2) throw new RuntimeException("String with day number unexpected length.");

            return day + "." + month + "." + year;
        } catch (Exception e) {
            return isoDate;
        }
    }

    public static int dpToPx(Context context, int dp) {
        return Math.round(dp * getPixelScaleFactor(context));
    }

    public static int pxToDp(Context context, int px) {
        return Math.round(px / getPixelScaleFactor(context));
    }

    private static float getPixelScaleFactor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT;
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ? drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ? drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void downloadImageTask(ImageView imageView, String url) {
        new DownloadImageTask(imageView).execute(url);
    }

    public static Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    public static CharSequence getRelativeTime(long date) {
        long now = System.currentTimeMillis();
        return DateUtils.getRelativeTimeSpanString(
                date,
                now,
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE);
    }
}
