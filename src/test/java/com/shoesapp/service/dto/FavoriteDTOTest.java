package com.shoesapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shoesapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FavoriteDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FavoriteDTO.class);
        FavoriteDTO favoriteDTO1 = new FavoriteDTO();
        favoriteDTO1.setId(1L);
        FavoriteDTO favoriteDTO2 = new FavoriteDTO();
        assertThat(favoriteDTO1).isNotEqualTo(favoriteDTO2);
        favoriteDTO2.setId(favoriteDTO1.getId());
        assertThat(favoriteDTO1).isEqualTo(favoriteDTO2);
        favoriteDTO2.setId(2L);
        assertThat(favoriteDTO1).isNotEqualTo(favoriteDTO2);
        favoriteDTO1.setId(null);
        assertThat(favoriteDTO1).isNotEqualTo(favoriteDTO2);
    }
}
