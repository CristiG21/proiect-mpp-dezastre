package com.mpp.disaster.domain;

import static com.mpp.disaster.domain.OfficialMessageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OfficialMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OfficialMessage.class);
        OfficialMessage officialMessage1 = getOfficialMessageSample1();
        OfficialMessage officialMessage2 = new OfficialMessage();
        assertThat(officialMessage1).isNotEqualTo(officialMessage2);

        officialMessage2.setId(officialMessage1.getId());
        assertThat(officialMessage1).isEqualTo(officialMessage2);

        officialMessage2 = getOfficialMessageSample2();
        assertThat(officialMessage1).isNotEqualTo(officialMessage2);
    }
}
