package com.example.xyzreader.remote;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Georgi on 5.6.2015 г..
 */
public class Constants {

    static {
        URL url = null;
        try {
            url = new URL("https://dl.dropboxusercontent.com/u/231329/xyzreader_data/data.json" );
        } catch (MalformedURLException ignored) {
            throw new IllegalArgumentException("Invalid base URL", ignored);
        }

        BASE_URL = url;
    }

    public static final URL BASE_URL;

    public static final int HTTP_CACHE_SIZE_BYTES = 25 * 1024 * 1024;
    public static final int HTTP_CONNECT_TIMEOUT_MS = 10*1000;
    public static final int HTTP_READOUT_TIMEOUT_MS = 20*1000;
    public static final int HTTP_WRITE_TIMEOUT_MS = 20*1000;
}
