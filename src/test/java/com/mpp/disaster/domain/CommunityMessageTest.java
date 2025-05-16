package com.mpp.disaster.domain;

import static com.mpp.disaster.domain.CommunityMessageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommunityMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommunityMessage.class);
        CommunityMessage communityMessage1 = getCommunityMessageSample1();
        CommunityMessage communityMessage2 = new CommunityMessage();
        assertThat(communityMessage1).isNotEqualTo(communityMessage2);

        communityMessage2.setId(communityMessage1.getId());
        assertThat(communityMessage1).isEqualTo(communityMessage2);

        communityMessage2 = getCommunityMessageSample2();
        assertThat(communityMessage1).isNotEqualTo(communityMessage2);
    }
}
