package com.shoesapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shoesapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SizeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SizeDTO.class);
        SizeDTO sizeDTO1 = new SizeDTO();
        sizeDTO1.setId(1L);
        SizeDTO sizeDTO2 = new SizeDTO();
        assertThat(sizeDTO1).isNotEqualTo(sizeDTO2);
        sizeDTO2.setId(sizeDTO1.getId());
        assertThat(sizeDTO1).isEqualTo(sizeDTO2);
        sizeDTO2.setId(2L);
        assertThat(sizeDTO1).isNotEqualTo(sizeDTO2);
        sizeDTO1.setId(null);
        assertThat(sizeDTO1).isNotEqualTo(sizeDTO2);
    }
}
