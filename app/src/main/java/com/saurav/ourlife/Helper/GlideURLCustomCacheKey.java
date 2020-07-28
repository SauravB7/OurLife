package com.saurav.ourlife.Helper;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;

import java.net.URISyntaxException;
import java.net.URL;

public class GlideURLCustomCacheKey extends GlideUrl {
    public GlideURLCustomCacheKey(String url) {
        super(url);
    }

    public GlideURLCustomCacheKey(String url, Headers headers) {
        super(url, headers);
    }

    public GlideURLCustomCacheKey(URL url) {
        super(url);
    }

    public GlideURLCustomCacheKey(URL url, Headers headers) {
        super(url, headers);
    }

    @Override
    public String getCacheKey() {
        String url = toStringUrl();
        try {
            return AWSS3Helper.getKeyFromPresignedURL(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }
}