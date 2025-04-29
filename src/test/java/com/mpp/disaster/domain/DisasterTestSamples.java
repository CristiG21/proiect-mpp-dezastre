package com.mpp.disaster.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DisasterTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Disaster getDisasterSample1() {
        return new Disaster().id(1L).name("name1");
    }

    public static Disaster getDisasterSample2() {
        return new Disaster().id(2L).name("name2");
    }

    public static Disaster getDisasterRandomSampleGenerator() {
        return new Disaster().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
