package com.mpp.disaster.service.mapper;

import static com.mpp.disaster.domain.PhotoURLAsserts.*;
import static com.mpp.disaster.domain.PhotoURLTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PhotoURLMapperTest {

    private PhotoURLMapper photoURLMapper;

    @BeforeEach
    void setUp() {
        photoURLMapper = new PhotoURLMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPhotoURLSample1();
        var actual = photoURLMapper.toEntity(photoURLMapper.toDto(expected));
        assertPhotoURLAllPropertiesEquals(expected, actual);
    }
}
