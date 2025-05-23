package com.mpp.disaster.domain;

import static com.mpp.disaster.domain.CommunityMessageTestSamples.*;
import static com.mpp.disaster.domain.CommunityMessageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
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

    @Test
    void parentTest() {
        CommunityMessage communityMessage = getCommunityMessageRandomSampleGenerator();
        CommunityMessage communityMessageBack = getCommunityMessageRandomSampleGenerator();

        communityMessage.setParent(communityMessageBack);
        assertThat(communityMessage.getParent()).isEqualTo(communityMessageBack);

        communityMessage.parent(null);
        assertThat(communityMessage.getParent()).isNull();
    }

    @Test
    void repliesTest() {
        CommunityMessage communityMessage = getCommunityMessageRandomSampleGenerator();
        CommunityMessage communityMessageBack = getCommunityMessageRandomSampleGenerator();

        communityMessage.addReplies(communityMessageBack);
        assertThat(communityMessage.getReplies()).containsOnly(communityMessageBack);
        assertThat(communityMessageBack.getParent()).isEqualTo(communityMessage);

        communityMessage.removeReplies(communityMessageBack);
        assertThat(communityMessage.getReplies()).doesNotContain(communityMessageBack);
        assertThat(communityMessageBack.getParent()).isNull();

        communityMessage.replies(new HashSet<>(Set.of(communityMessageBack)));
        assertThat(communityMessage.getReplies()).containsOnly(communityMessageBack);
        assertThat(communityMessageBack.getParent()).isEqualTo(communityMessage);

        communityMessage.setReplies(new HashSet<>());
        assertThat(communityMessage.getReplies()).doesNotContain(communityMessageBack);
        assertThat(communityMessageBack.getParent()).isNull();
    }
}
