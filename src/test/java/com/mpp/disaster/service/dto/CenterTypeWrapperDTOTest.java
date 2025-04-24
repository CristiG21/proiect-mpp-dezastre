package com.mpp.disaster.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CenterTypeWrapperDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CenterTypeWrapperDTO.class);
        CenterTypeWrapperDTO centerTypeWrapperDTO1 = new CenterTypeWrapperDTO();
        centerTypeWrapperDTO1.setId(1L);
        CenterTypeWrapperDTO centerTypeWrapperDTO2 = new CenterTypeWrapperDTO();
        assertThat(centerTypeWrapperDTO1).isNotEqualTo(centerTypeWrapperDTO2);
        centerTypeWrapperDTO2.setId(centerTypeWrapperDTO1.getId());
        assertThat(centerTypeWrapperDTO1).isEqualTo(centerTypeWrapperDTO2);
        centerTypeWrapperDTO2.setId(2L);
        assertThat(centerTypeWrapperDTO1).isNotEqualTo(centerTypeWrapperDTO2);
        centerTypeWrapperDTO1.setId(null);
        assertThat(centerTypeWrapperDTO1).isNotEqualTo(centerTypeWrapperDTO2);
    }
}
