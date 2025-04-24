package com.mpp.disaster.domain;

import static com.mpp.disaster.domain.CenterTestSamples.*;
import static com.mpp.disaster.domain.CenterTypeWrapperTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CenterTypeWrapperTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CenterTypeWrapper.class);
        CenterTypeWrapper centerTypeWrapper1 = getCenterTypeWrapperSample1();
        CenterTypeWrapper centerTypeWrapper2 = new CenterTypeWrapper();
        assertThat(centerTypeWrapper1).isNotEqualTo(centerTypeWrapper2);

        centerTypeWrapper2.setId(centerTypeWrapper1.getId());
        assertThat(centerTypeWrapper1).isEqualTo(centerTypeWrapper2);

        centerTypeWrapper2 = getCenterTypeWrapperSample2();
        assertThat(centerTypeWrapper1).isNotEqualTo(centerTypeWrapper2);
    }

    @Test
    void centerTest() {
        CenterTypeWrapper centerTypeWrapper = getCenterTypeWrapperRandomSampleGenerator();
        Center centerBack = getCenterRandomSampleGenerator();

        centerTypeWrapper.setCenter(centerBack);
        assertThat(centerTypeWrapper.getCenter()).isEqualTo(centerBack);

        centerTypeWrapper.center(null);
        assertThat(centerTypeWrapper.getCenter()).isNull();
    }
}
