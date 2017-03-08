package com.sao.mobile.saolib.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.sao.mobile.saolib.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Seb on 06/03/2017.
 */
public abstract class AbstractSaoNotificationManager {
    protected AbstractSaoNotificationManager() {
    }

    protected abstract void showNotification(Context context, Serializable mObject, Bitmap bitmap);

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private Context mContext;
        private String mThumbnail;
        private Serializable mObject;

        public DownloadImageTask(Context context, Serializable object, String thumbnail) {
            this.mContext = context;
            this.mObject = object;
            this.mThumbnail = thumbnail;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            try {
                int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
                bitmap = Picasso.with(mContext).load(mThumbnail).resize(avatarSize, avatarSize).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            showNotification(mContext, mObject, bitmap);
        }
    }
}
