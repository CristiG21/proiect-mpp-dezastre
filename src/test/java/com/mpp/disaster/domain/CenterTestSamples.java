package com.mpp.disaster.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CenterTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Center getCenterSample1() {
        return new Center().id(1L).name("name1");
    }

    public static Center getCenterSample2() {
        return new Center().id(2L).name("name2");
    }

    public static Center getCenterRandomSampleGenerator() {
        return new Center().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
