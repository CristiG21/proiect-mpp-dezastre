package com.mpp.disaster.service.mapper;

import static com.mpp.disaster.domain.DisasterAsserts.*;
import static com.mpp.disaster.domain.DisasterTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DisasterMapperTest {

    private DisasterMapper disasterMapper;

    @BeforeEach
    void setUp() {
        disasterMapper = new DisasterMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDisasterSample1();
        var actual = disasterMapper.toEntity(disasterMapper.toDto(expected));
        assertDisasterAllPropertiesEquals(expected, actual);
    }
}
