package com.mpp.disaster.domain;

import static com.mpp.disaster.domain.DisasterTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DisasterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Disaster.class);
        Disaster disaster1 = getDisasterSample1();
        Disaster disaster2 = new Disaster();
        assertThat(disaster1).isNotEqualTo(disaster2);

        disaster2.setId(disaster1.getId());
        assertThat(disaster1).isEqualTo(disaster2);

        disaster2 = getDisasterSample2();
        assertThat(disaster1).isNotEqualTo(disaster2);
    }
}
