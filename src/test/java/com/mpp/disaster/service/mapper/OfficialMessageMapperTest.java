package com.mpp.disaster.service.mapper;

import static com.mpp.disaster.domain.OfficialMessageAsserts.*;
import static com.mpp.disaster.domain.OfficialMessageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OfficialMessageMapperTest {

    private OfficialMessageMapper officialMessageMapper;

    @BeforeEach
    void setUp() {
        officialMessageMapper = new OfficialMessageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOfficialMessageSample1();
        var actual = officialMessageMapper.toEntity(officialMessageMapper.toDto(expected));
        assertOfficialMessageAllPropertiesEquals(expected, actual);
    }
}
