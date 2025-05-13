package com.mpp.disaster.domain;

import static com.mpp.disaster.domain.CenterTestSamples.*;
import static com.mpp.disaster.domain.CenterTypeWrapperTestSamples.*;
import static com.mpp.disaster.domain.PhotoURLTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CenterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Center.class);
        Center center1 = getCenterSample1();
        Center center2 = new Center();
        assertThat(center1).isNotEqualTo(center2);

        center2.setId(center1.getId());
        assertThat(center1).isEqualTo(center2);

        center2 = getCenterSample2();
        assertThat(center1).isNotEqualTo(center2);
    }

    @Test
    void typesTest() {
        Center center = getCenterRandomSampleGenerator();
        CenterTypeWrapper centerTypeWrapperBack = getCenterTypeWrapperRandomSampleGenerator();

        center.addTypes(centerTypeWrapperBack);
        assertThat(center.getTypes()).containsOnly(centerTypeWrapperBack);
        assertThat(centerTypeWrapperBack.getCenter()).isEqualTo(center);

        center.removeTypes(centerTypeWrapperBack);
        assertThat(center.getTypes()).doesNotContain(centerTypeWrapperBack);
        assertThat(centerTypeWrapperBack.getCenter()).isNull();

        center.types(new HashSet<>(Set.of(centerTypeWrapperBack)));
        assertThat(center.getTypes()).containsOnly(centerTypeWrapperBack);
        assertThat(centerTypeWrapperBack.getCenter()).isEqualTo(center);

        center.setTypes(new HashSet<>());
        assertThat(center.getTypes()).doesNotContain(centerTypeWrapperBack);
        assertThat(centerTypeWrapperBack.getCenter()).isNull();
    }

    @Test
    void photoUrlTest() {
        Center center = getCenterRandomSampleGenerator();
        PhotoURL photoURLBack = getPhotoURLRandomSampleGenerator();

        center.addPhotoUrl(photoURLBack);
        assertThat(center.getPhotoUrls()).containsOnly(photoURLBack);
        assertThat(photoURLBack.getCenter()).isEqualTo(center);

        center.removePhotoUrl(photoURLBack);
        assertThat(center.getPhotoUrls()).doesNotContain(photoURLBack);
        assertThat(photoURLBack.getCenter()).isNull();

        center.photoUrls(new HashSet<>(Set.of(photoURLBack)));
        assertThat(center.getPhotoUrls()).containsOnly(photoURLBack);
        assertThat(photoURLBack.getCenter()).isEqualTo(center);

        center.setPhotoUrls(new HashSet<>());
        assertThat(center.getPhotoUrls()).doesNotContain(photoURLBack);
        assertThat(photoURLBack.getCenter()).isNull();
    }
}
