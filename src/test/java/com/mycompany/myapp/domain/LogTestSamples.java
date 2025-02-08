package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Log getLogSample1() {
        return new Log().id(1L).eventType("eventType1").ipAddress("ipAddress1").description("description1");
    }

    public static Log getLogSample2() {
        return new Log().id(2L).eventType("eventType2").ipAddress("ipAddress2").description("description2");
    }

    public static Log getLogRandomSampleGenerator() {
        return new Log()
            .id(longCount.incrementAndGet())
            .eventType(UUID.randomUUID().toString())
            .ipAddress(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
