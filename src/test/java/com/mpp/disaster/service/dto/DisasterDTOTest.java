package com.mpp.disaster.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DisasterDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DisasterDTO.class);
        DisasterDTO disasterDTO1 = new DisasterDTO();
        disasterDTO1.setId(1L);
        DisasterDTO disasterDTO2 = new DisasterDTO();
        assertThat(disasterDTO1).isNotEqualTo(disasterDTO2);
        disasterDTO2.setId(disasterDTO1.getId());
        assertThat(disasterDTO1).isEqualTo(disasterDTO2);
        disasterDTO2.setId(2L);
        assertThat(disasterDTO1).isNotEqualTo(disasterDTO2);
        disasterDTO1.setId(null);
        assertThat(disasterDTO1).isNotEqualTo(disasterDTO2);
    }
}
