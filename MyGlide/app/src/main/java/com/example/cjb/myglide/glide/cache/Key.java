package com.example.cjb.myglide.glide.cache;

import java.security.MessageDigest;

public interface Key {
    void updateDiskCacheKey(MessageDigest messageDigest);
    byte[] getKeyBytes();
}
