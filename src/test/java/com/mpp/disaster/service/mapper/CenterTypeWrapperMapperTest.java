package com.mpp.disaster.service.mapper;

import static com.mpp.disaster.domain.CenterTypeWrapperAsserts.*;
import static com.mpp.disaster.domain.CenterTypeWrapperTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CenterTypeWrapperMapperTest {

    private CenterTypeWrapperMapper centerTypeWrapperMapper;

    @BeforeEach
    void setUp() {
        centerTypeWrapperMapper = new CenterTypeWrapperMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCenterTypeWrapperSample1();
        var actual = centerTypeWrapperMapper.toEntity(centerTypeWrapperMapper.toDto(expected));
        assertCenterTypeWrapperAllPropertiesEquals(expected, actual);
    }
}
