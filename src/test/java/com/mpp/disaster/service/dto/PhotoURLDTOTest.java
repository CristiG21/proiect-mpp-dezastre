package com.mpp.disaster.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PhotoURLDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PhotoURLDTO.class);
        PhotoURLDTO photoURLDTO1 = new PhotoURLDTO();
        photoURLDTO1.setId(1L);
        PhotoURLDTO photoURLDTO2 = new PhotoURLDTO();
        assertThat(photoURLDTO1).isNotEqualTo(photoURLDTO2);
        photoURLDTO2.setId(photoURLDTO1.getId());
        assertThat(photoURLDTO1).isEqualTo(photoURLDTO2);
        photoURLDTO2.setId(2L);
        assertThat(photoURLDTO1).isNotEqualTo(photoURLDTO2);
        photoURLDTO1.setId(null);
        assertThat(photoURLDTO1).isNotEqualTo(photoURLDTO2);
    }
}
