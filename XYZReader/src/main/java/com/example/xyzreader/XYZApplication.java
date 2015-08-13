package com.example.xyzreader;

import android.app.Application;

import com.example.xyzreader.remote.Constants;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

public class XYZApplication extends Application {

    private OkHttpClient mHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        setupPicasso(getOkHttpClient());
    }

    public OkHttpClient getOkHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = createHttpClient();
        }
        return mHttpClient;
    }

    private OkHttpClient createHttpClient() {
        OkHttpClient client = new OkHttpClient();

        //Set Cache size and Timeout limits
        Cache cache = new Cache(getCacheDir(), Constants.HTTP_CACHE_SIZE_BYTES);
        client.setCache(cache);
        client.setConnectTimeout(Constants.HTTP_CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(Constants.HTTP_READOUT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(Constants.HTTP_WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        return client;
    }

    private void setupPicasso(OkHttpClient client) {
        Picasso instance = new Picasso.Builder(this)
                .loggingEnabled(false)
                .indicatorsEnabled(false)
                .downloader(new OkHttpDownloader(getOkHttpClient()))
                .build();

        Picasso.setSingletonInstance(instance);
    }
}
