package com.mpp.disaster.web.rest;

import static com.mpp.disaster.domain.DisasterAsserts.*;
import static com.mpp.disaster.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpp.disaster.IntegrationTest;
import com.mpp.disaster.domain.Disaster;
import com.mpp.disaster.domain.enumeration.DisasterType;
import com.mpp.disaster.repository.DisasterRepository;
import com.mpp.disaster.service.dto.DisasterDTO;
import com.mpp.disaster.service.mapper.DisasterMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DisasterResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DisasterResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Double DEFAULT_RADIUS = 1D;
    private static final Double UPDATED_RADIUS = 2D;

    private static final DisasterType DEFAULT_TYPE = DisasterType.MINOR;
    private static final DisasterType UPDATED_TYPE = DisasterType.MODERAT;

    private static final String ENTITY_API_URL = "/api/disasters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DisasterRepository disasterRepository;

    @Autowired
    private DisasterMapper disasterMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDisasterMockMvc;

    private Disaster disaster;

    private Disaster insertedDisaster;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Disaster createEntity() {
        return new Disaster()
            .name(DEFAULT_NAME)
            .longitude(DEFAULT_LONGITUDE)
            .latitude(DEFAULT_LATITUDE)
            .radius(DEFAULT_RADIUS)
            .type(DEFAULT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Disaster createUpdatedEntity() {
        return new Disaster()
            .name(UPDATED_NAME)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .radius(UPDATED_RADIUS)
            .type(UPDATED_TYPE);
    }

    @BeforeEach
    void initTest() {
        disaster = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDisaster != null) {
            disasterRepository.delete(insertedDisaster);
            insertedDisaster = null;
        }
    }

    @Test
    @Transactional
    void createDisaster() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Disaster
        DisasterDTO disasterDTO = disasterMapper.toDto(disaster);
        var returnedDisasterDTO = om.readValue(
            restDisasterMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disasterDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DisasterDTO.class
        );

        // Validate the Disaster in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDisaster = disasterMapper.toEntity(returnedDisasterDTO);
        assertDisasterUpdatableFieldsEquals(returnedDisaster, getPersistedDisaster(returnedDisaster));

        insertedDisaster = returnedDisaster;
    }

    @Test
    @Transactional
    void createDisasterWithExistingId() throws Exception {
        // Create the Disaster with an existing ID
        disaster.setId(1L);
        DisasterDTO disasterDTO = disasterMapper.toDto(disaster);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDisasterMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disasterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Disaster in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDisasters() throws Exception {
        // Initialize the database
        insertedDisaster = disasterRepository.saveAndFlush(disaster);

        // Get all the disasterList
        restDisasterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(disaster.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].radius").value(hasItem(DEFAULT_RADIUS)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getDisaster() throws Exception {
        // Initialize the database
        insertedDisaster = disasterRepository.saveAndFlush(disaster);

        // Get the disaster
        restDisasterMockMvc
            .perform(get(ENTITY_API_URL_ID, disaster.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(disaster.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.radius").value(DEFAULT_RADIUS))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDisaster() throws Exception {
        // Get the disaster
        restDisasterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDisaster() throws Exception {
        // Initialize the database
        insertedDisaster = disasterRepository.saveAndFlush(disaster);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the disaster
        Disaster updatedDisaster = disasterRepository.findById(disaster.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDisaster are not directly saved in db
        em.detach(updatedDisaster);
        updatedDisaster
            .name(UPDATED_NAME)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .radius(UPDATED_RADIUS)
            .type(UPDATED_TYPE);
        DisasterDTO disasterDTO = disasterMapper.toDto(updatedDisaster);

        restDisasterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, disasterDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(disasterDTO))
            )
            .andExpect(status().isOk());

        // Validate the Disaster in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDisasterToMatchAllProperties(updatedDisaster);
    }

    @Test
    @Transactional
    void putNonExistingDisaster() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disaster.setId(longCount.incrementAndGet());

        // Create the Disaster
        DisasterDTO disasterDTO = disasterMapper.toDto(disaster);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDisasterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, disasterDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(disasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Disaster in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDisaster() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disaster.setId(longCount.incrementAndGet());

        // Create the Disaster
        DisasterDTO disasterDTO = disasterMapper.toDto(disaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisasterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(disasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Disaster in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDisaster() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disaster.setId(longCount.incrementAndGet());

        // Create the Disaster
        DisasterDTO disasterDTO = disasterMapper.toDto(disaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisasterMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disasterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Disaster in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDisasterWithPatch() throws Exception {
        // Initialize the database
        insertedDisaster = disasterRepository.saveAndFlush(disaster);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the disaster using partial update
        Disaster partialUpdatedDisaster = new Disaster();
        partialUpdatedDisaster.setId(disaster.getId());

        partialUpdatedDisaster.latitude(UPDATED_LATITUDE).radius(UPDATED_RADIUS).type(UPDATED_TYPE);

        restDisasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDisaster.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDisaster))
            )
            .andExpect(status().isOk());

        // Validate the Disaster in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisasterUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDisaster, disaster), getPersistedDisaster(disaster));
    }

    @Test
    @Transactional
    void fullUpdateDisasterWithPatch() throws Exception {
        // Initialize the database
        insertedDisaster = disasterRepository.saveAndFlush(disaster);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the disaster using partial update
        Disaster partialUpdatedDisaster = new Disaster();
        partialUpdatedDisaster.setId(disaster.getId());

        partialUpdatedDisaster
            .name(UPDATED_NAME)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .radius(UPDATED_RADIUS)
            .type(UPDATED_TYPE);

        restDisasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDisaster.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDisaster))
            )
            .andExpect(status().isOk());

        // Validate the Disaster in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisasterUpdatableFieldsEquals(partialUpdatedDisaster, getPersistedDisaster(partialUpdatedDisaster));
    }

    @Test
    @Transactional
    void patchNonExistingDisaster() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disaster.setId(longCount.incrementAndGet());

        // Create the Disaster
        DisasterDTO disasterDTO = disasterMapper.toDto(disaster);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDisasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, disasterDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(disasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Disaster in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDisaster() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disaster.setId(longCount.incrementAndGet());

        // Create the Disaster
        DisasterDTO disasterDTO = disasterMapper.toDto(disaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(disasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Disaster in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDisaster() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disaster.setId(longCount.incrementAndGet());

        // Create the Disaster
        DisasterDTO disasterDTO = disasterMapper.toDto(disaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisasterMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(disasterDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Disaster in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDisaster() throws Exception {
        // Initialize the database
        insertedDisaster = disasterRepository.saveAndFlush(disaster);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the disaster
        restDisasterMockMvc
            .perform(delete(ENTITY_API_URL_ID, disaster.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return disasterRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Disaster getPersistedDisaster(Disaster disaster) {
        return disasterRepository.findById(disaster.getId()).orElseThrow();
    }

    protected void assertPersistedDisasterToMatchAllProperties(Disaster expectedDisaster) {
        assertDisasterAllPropertiesEquals(expectedDisaster, getPersistedDisaster(expectedDisaster));
    }

    protected void assertPersistedDisasterToMatchUpdatableProperties(Disaster expectedDisaster) {
        assertDisasterAllUpdatablePropertiesEquals(expectedDisaster, getPersistedDisaster(expectedDisaster));
    }
}
