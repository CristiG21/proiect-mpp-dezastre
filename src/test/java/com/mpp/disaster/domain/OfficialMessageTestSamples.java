package com.mpp.disaster.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OfficialMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static OfficialMessage getOfficialMessageSample1() {
        return new OfficialMessage().id(1L).title("title1").body("body1");
    }

    public static OfficialMessage getOfficialMessageSample2() {
        return new OfficialMessage().id(2L).title("title2").body("body2");
    }

    public static OfficialMessage getOfficialMessageRandomSampleGenerator() {
        return new OfficialMessage().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).body(UUID.randomUUID().toString());
    }
}
