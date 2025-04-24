package com.mpp.disaster.service.mapper;

import static com.mpp.disaster.domain.CenterAsserts.*;
import static com.mpp.disaster.domain.CenterTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CenterMapperTest {

    private CenterMapper centerMapper;

    @BeforeEach
    void setUp() {
        centerMapper = new CenterMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCenterSample1();
        var actual = centerMapper.toEntity(centerMapper.toDto(expected));
        assertCenterAllPropertiesEquals(expected, actual);
    }
}
