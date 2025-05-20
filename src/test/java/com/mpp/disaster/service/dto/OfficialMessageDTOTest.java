package com.mpp.disaster.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OfficialMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OfficialMessageDTO.class);
        OfficialMessageDTO officialMessageDTO1 = new OfficialMessageDTO();
        officialMessageDTO1.setId(1L);
        OfficialMessageDTO officialMessageDTO2 = new OfficialMessageDTO();
        assertThat(officialMessageDTO1).isNotEqualTo(officialMessageDTO2);
        officialMessageDTO2.setId(officialMessageDTO1.getId());
        assertThat(officialMessageDTO1).isEqualTo(officialMessageDTO2);
        officialMessageDTO2.setId(2L);
        assertThat(officialMessageDTO1).isNotEqualTo(officialMessageDTO2);
        officialMessageDTO1.setId(null);
        assertThat(officialMessageDTO1).isNotEqualTo(officialMessageDTO2);
    }
}
