package com.mpp.disaster.service.mapper;

import static com.mpp.disaster.domain.ReviewAsserts.*;
import static com.mpp.disaster.domain.ReviewTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReviewMapperTest {

    private ReviewMapper reviewMapper;

    @BeforeEach
    void setUp() {
        reviewMapper = new ReviewMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReviewSample1();
        var actual = reviewMapper.toEntity(reviewMapper.toDto(expected));
        assertReviewAllPropertiesEquals(expected, actual);
    }
}
