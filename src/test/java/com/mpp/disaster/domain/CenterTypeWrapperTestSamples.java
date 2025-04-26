package com.mpp.disaster.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CenterTypeWrapperTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CenterTypeWrapper getCenterTypeWrapperSample1() {
        return new CenterTypeWrapper().id(1L);
    }

    public static CenterTypeWrapper getCenterTypeWrapperSample2() {
        return new CenterTypeWrapper().id(2L);
    }

    public static CenterTypeWrapper getCenterTypeWrapperRandomSampleGenerator() {
        return new CenterTypeWrapper().id(longCount.incrementAndGet());
    }
}
