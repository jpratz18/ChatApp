package com.mac.chatapp.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mac.chatapp.BaseActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by admin on 13/06/2016.
 */
public class LoadImage extends AsyncTask<String, Void, Bitmap> {

    public ImageView image;
    public BaseActivity activity;

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(params[0]);
            URLConnection urlConnection = url.openConnection();
            if (urlConnection instanceof HttpURLConnection) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                httpURLConnection.setAllowUserInteraction(false);
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                if (httpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {
                    InputStream in = httpURLConnection.getInputStream();
                    if (in != null) {
                        bitmap = BitmapFactory.decodeStream(in);
                    }
                    in.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (image != null && bitmap != null) {
            image.setImageBitmap(bitmap);
            image.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(activity.getBaseContext(), "It was a problem downloading the image", Toast.LENGTH_SHORT).show();
        }
        activity.hideProgressDialog();
    }
}
