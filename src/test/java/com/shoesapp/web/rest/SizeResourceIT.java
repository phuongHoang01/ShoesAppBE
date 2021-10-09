package com.shoesapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.shoesapp.IntegrationTest;
import com.shoesapp.domain.Size;
import com.shoesapp.repository.SizeRepository;
import com.shoesapp.service.criteria.SizeCriteria;
import com.shoesapp.service.dto.SizeDTO;
import com.shoesapp.service.mapper.SizeMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SizeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SizeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sizes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private SizeMapper sizeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSizeMockMvc;

    private Size size;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Size createEntity(EntityManager em) {
        Size size = new Size().name(DEFAULT_NAME);
        return size;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Size createUpdatedEntity(EntityManager em) {
        Size size = new Size().name(UPDATED_NAME);
        return size;
    }

    @BeforeEach
    public void initTest() {
        size = createEntity(em);
    }

    @Test
    @Transactional
    void createSize() throws Exception {
        int databaseSizeBeforeCreate = sizeRepository.findAll().size();
        // Create the Size
        SizeDTO sizeDTO = sizeMapper.toDto(size);
        restSizeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sizeDTO)))
            .andExpect(status().isCreated());

        // Validate the Size in the database
        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeCreate + 1);
        Size testSize = sizeList.get(sizeList.size() - 1);
        assertThat(testSize.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createSizeWithExistingId() throws Exception {
        // Create the Size with an existing ID
        size.setId(1L);
        SizeDTO sizeDTO = sizeMapper.toDto(size);

        int databaseSizeBeforeCreate = sizeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSizeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sizeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Size in the database
        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = sizeRepository.findAll().size();
        // set the field null
        size.setName(null);

        // Create the Size, which fails.
        SizeDTO sizeDTO = sizeMapper.toDto(size);

        restSizeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sizeDTO)))
            .andExpect(status().isBadRequest());

        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSizes() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        // Get all the sizeList
        restSizeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(size.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getSize() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        // Get the size
        restSizeMockMvc
            .perform(get(ENTITY_API_URL_ID, size.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(size.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getSizesByIdFiltering() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        Long id = size.getId();

        defaultSizeShouldBeFound("id.equals=" + id);
        defaultSizeShouldNotBeFound("id.notEquals=" + id);

        defaultSizeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSizeShouldNotBeFound("id.greaterThan=" + id);

        defaultSizeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSizeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSizesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        // Get all the sizeList where name equals to DEFAULT_NAME
        defaultSizeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the sizeList where name equals to UPDATED_NAME
        defaultSizeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSizesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        // Get all the sizeList where name not equals to DEFAULT_NAME
        defaultSizeShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the sizeList where name not equals to UPDATED_NAME
        defaultSizeShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSizesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        // Get all the sizeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSizeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the sizeList where name equals to UPDATED_NAME
        defaultSizeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSizesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        // Get all the sizeList where name is not null
        defaultSizeShouldBeFound("name.specified=true");

        // Get all the sizeList where name is null
        defaultSizeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllSizesByNameContainsSomething() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        // Get all the sizeList where name contains DEFAULT_NAME
        defaultSizeShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the sizeList where name contains UPDATED_NAME
        defaultSizeShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSizesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        // Get all the sizeList where name does not contain DEFAULT_NAME
        defaultSizeShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the sizeList where name does not contain UPDATED_NAME
        defaultSizeShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSizeShouldBeFound(String filter) throws Exception {
        restSizeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(size.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restSizeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSizeShouldNotBeFound(String filter) throws Exception {
        restSizeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSizeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSize() throws Exception {
        // Get the size
        restSizeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSize() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        int databaseSizeBeforeUpdate = sizeRepository.findAll().size();

        // Update the size
        Size updatedSize = sizeRepository.findById(size.getId()).get();
        // Disconnect from session so that the updates on updatedSize are not directly saved in db
        em.detach(updatedSize);
        updatedSize.name(UPDATED_NAME);
        SizeDTO sizeDTO = sizeMapper.toDto(updatedSize);

        restSizeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sizeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sizeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Size in the database
        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeUpdate);
        Size testSize = sizeList.get(sizeList.size() - 1);
        assertThat(testSize.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingSize() throws Exception {
        int databaseSizeBeforeUpdate = sizeRepository.findAll().size();
        size.setId(count.incrementAndGet());

        // Create the Size
        SizeDTO sizeDTO = sizeMapper.toDto(size);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSizeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sizeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sizeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Size in the database
        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSize() throws Exception {
        int databaseSizeBeforeUpdate = sizeRepository.findAll().size();
        size.setId(count.incrementAndGet());

        // Create the Size
        SizeDTO sizeDTO = sizeMapper.toDto(size);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSizeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sizeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Size in the database
        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSize() throws Exception {
        int databaseSizeBeforeUpdate = sizeRepository.findAll().size();
        size.setId(count.incrementAndGet());

        // Create the Size
        SizeDTO sizeDTO = sizeMapper.toDto(size);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSizeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sizeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Size in the database
        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSizeWithPatch() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        int databaseSizeBeforeUpdate = sizeRepository.findAll().size();

        // Update the size using partial update
        Size partialUpdatedSize = new Size();
        partialUpdatedSize.setId(size.getId());

        restSizeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSize.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSize))
            )
            .andExpect(status().isOk());

        // Validate the Size in the database
        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeUpdate);
        Size testSize = sizeList.get(sizeList.size() - 1);
        assertThat(testSize.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateSizeWithPatch() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        int databaseSizeBeforeUpdate = sizeRepository.findAll().size();

        // Update the size using partial update
        Size partialUpdatedSize = new Size();
        partialUpdatedSize.setId(size.getId());

        partialUpdatedSize.name(UPDATED_NAME);

        restSizeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSize.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSize))
            )
            .andExpect(status().isOk());

        // Validate the Size in the database
        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeUpdate);
        Size testSize = sizeList.get(sizeList.size() - 1);
        assertThat(testSize.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingSize() throws Exception {
        int databaseSizeBeforeUpdate = sizeRepository.findAll().size();
        size.setId(count.incrementAndGet());

        // Create the Size
        SizeDTO sizeDTO = sizeMapper.toDto(size);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSizeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sizeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sizeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Size in the database
        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSize() throws Exception {
        int databaseSizeBeforeUpdate = sizeRepository.findAll().size();
        size.setId(count.incrementAndGet());

        // Create the Size
        SizeDTO sizeDTO = sizeMapper.toDto(size);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSizeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sizeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Size in the database
        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSize() throws Exception {
        int databaseSizeBeforeUpdate = sizeRepository.findAll().size();
        size.setId(count.incrementAndGet());

        // Create the Size
        SizeDTO sizeDTO = sizeMapper.toDto(size);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSizeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(sizeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Size in the database
        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSize() throws Exception {
        // Initialize the database
        sizeRepository.saveAndFlush(size);

        int databaseSizeBeforeDelete = sizeRepository.findAll().size();

        // Delete the size
        restSizeMockMvc
            .perform(delete(ENTITY_API_URL_ID, size.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Size> sizeList = sizeRepository.findAll();
        assertThat(sizeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
