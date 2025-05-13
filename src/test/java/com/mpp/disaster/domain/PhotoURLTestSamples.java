package com.mpp.disaster.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PhotoURLTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PhotoURL getPhotoURLSample1() {
        return new PhotoURL().id(1L).url("url1");
    }

    public static PhotoURL getPhotoURLSample2() {
        return new PhotoURL().id(2L).url("url2");
    }

    public static PhotoURL getPhotoURLRandomSampleGenerator() {
        return new PhotoURL().id(longCount.incrementAndGet()).url(UUID.randomUUID().toString());
    }
}
