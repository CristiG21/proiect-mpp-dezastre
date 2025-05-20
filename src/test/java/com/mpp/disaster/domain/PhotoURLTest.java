package com.mpp.disaster.domain;

import static com.mpp.disaster.domain.CenterTestSamples.*;
import static com.mpp.disaster.domain.PhotoURLTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PhotoURLTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PhotoURL.class);
        PhotoURL photoURL1 = getPhotoURLSample1();
        PhotoURL photoURL2 = new PhotoURL();
        assertThat(photoURL1).isNotEqualTo(photoURL2);

        photoURL2.setId(photoURL1.getId());
        assertThat(photoURL1).isEqualTo(photoURL2);

        photoURL2 = getPhotoURLSample2();
        assertThat(photoURL1).isNotEqualTo(photoURL2);
    }

    @Test
    void centerTest() {
        PhotoURL photoURL = getPhotoURLRandomSampleGenerator();
        Center centerBack = getCenterRandomSampleGenerator();

        photoURL.setCenter(centerBack);
        assertThat(photoURL.getCenter()).isEqualTo(centerBack);

        photoURL.center(null);
        assertThat(photoURL.getCenter()).isNull();
    }
}
