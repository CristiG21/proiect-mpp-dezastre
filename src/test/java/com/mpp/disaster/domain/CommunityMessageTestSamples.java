package com.mpp.disaster.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CommunityMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CommunityMessage getCommunityMessageSample1() {
        return new CommunityMessage().id(1L).content("content1");
    }

    public static CommunityMessage getCommunityMessageSample2() {
        return new CommunityMessage().id(2L).content("content2");
    }

    public static CommunityMessage getCommunityMessageRandomSampleGenerator() {
        return new CommunityMessage().id(longCount.incrementAndGet()).content(UUID.randomUUID().toString());
    }
}
