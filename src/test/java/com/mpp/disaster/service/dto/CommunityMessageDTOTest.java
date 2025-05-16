package com.mpp.disaster.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommunityMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommunityMessageDTO.class);
        CommunityMessageDTO communityMessageDTO1 = new CommunityMessageDTO();
        communityMessageDTO1.setId(1L);
        CommunityMessageDTO communityMessageDTO2 = new CommunityMessageDTO();
        assertThat(communityMessageDTO1).isNotEqualTo(communityMessageDTO2);
        communityMessageDTO2.setId(communityMessageDTO1.getId());
        assertThat(communityMessageDTO1).isEqualTo(communityMessageDTO2);
        communityMessageDTO2.setId(2L);
        assertThat(communityMessageDTO1).isNotEqualTo(communityMessageDTO2);
        communityMessageDTO1.setId(null);
        assertThat(communityMessageDTO1).isNotEqualTo(communityMessageDTO2);
    }
}
