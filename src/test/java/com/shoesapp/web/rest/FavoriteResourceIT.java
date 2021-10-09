package com.shoesapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.shoesapp.IntegrationTest;
import com.shoesapp.domain.Favorite;
import com.shoesapp.repository.FavoriteRepository;
import com.shoesapp.service.FavoriteService;
import com.shoesapp.service.dto.FavoriteDTO;
import com.shoesapp.service.mapper.FavoriteMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FavoriteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FavoriteResourceIT {

    private static final LocalDate DEFAULT_CREATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/favorites";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Mock
    private FavoriteRepository favoriteRepositoryMock;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Mock
    private FavoriteService favoriteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFavoriteMockMvc;

    private Favorite favorite;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Favorite createEntity(EntityManager em) {
        Favorite favorite = new Favorite().createdDate(DEFAULT_CREATED_DATE);
        return favorite;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Favorite createUpdatedEntity(EntityManager em) {
        Favorite favorite = new Favorite().createdDate(UPDATED_CREATED_DATE);
        return favorite;
    }

    @BeforeEach
    public void initTest() {
        favorite = createEntity(em);
    }

    @Test
    @Transactional
    void createFavorite() throws Exception {
        int databaseSizeBeforeCreate = favoriteRepository.findAll().size();
        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);
        restFavoriteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(favoriteDTO)))
            .andExpect(status().isCreated());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeCreate + 1);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
    }

    @Test
    @Transactional
    void createFavoriteWithExistingId() throws Exception {
        // Create the Favorite with an existing ID
        favorite.setId(1L);
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        int databaseSizeBeforeCreate = favoriteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFavoriteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(favoriteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFavorites() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(favorite.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFavoritesWithEagerRelationshipsIsEnabled() throws Exception {
        when(favoriteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFavoriteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(favoriteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFavoritesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(favoriteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFavoriteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(favoriteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get the favorite
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL_ID, favorite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(favorite.getId().intValue()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFavorite() throws Exception {
        // Get the favorite
        restFavoriteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

        // Update the favorite
        Favorite updatedFavorite = favoriteRepository.findById(favorite.getId()).get();
        // Disconnect from session so that the updates on updatedFavorite are not directly saved in db
        em.detach(updatedFavorite);
        updatedFavorite.createdDate(UPDATED_CREATED_DATE);
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(updatedFavorite);

        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, favoriteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, favoriteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(favoriteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFavoriteWithPatch() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

        // Update the favorite using partial update
        Favorite partialUpdatedFavorite = new Favorite();
        partialUpdatedFavorite.setId(favorite.getId());

        partialUpdatedFavorite.createdDate(UPDATED_CREATED_DATE);

        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFavorite.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFavorite))
            )
            .andExpect(status().isOk());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateFavoriteWithPatch() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

        // Update the favorite using partial update
        Favorite partialUpdatedFavorite = new Favorite();
        partialUpdatedFavorite.setId(favorite.getId());

        partialUpdatedFavorite.createdDate(UPDATED_CREATED_DATE);

        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFavorite.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFavorite))
            )
            .andExpect(status().isOk());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, favoriteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeDelete = favoriteRepository.findAll().size();

        // Delete the favorite
        restFavoriteMockMvc
            .perform(delete(ENTITY_API_URL_ID, favorite.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
