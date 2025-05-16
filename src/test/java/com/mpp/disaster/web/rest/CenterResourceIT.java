package com.mpp.disaster.web.rest;

import static com.mpp.disaster.domain.CenterAsserts.*;
import static com.mpp.disaster.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpp.disaster.IntegrationTest;
import com.mpp.disaster.domain.Center;
import com.mpp.disaster.repository.CenterRepository;
import com.mpp.disaster.service.dto.CenterDTO;
import com.mpp.disaster.service.mapper.CenterMapper;
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
 * Integration tests for the {@link CenterResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CenterResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_AVAILABLE_SEATS = 1;
    private static final Integer UPDATED_AVAILABLE_SEATS = 2;

    private static final String ENTITY_API_URL = "/api/centers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CenterRepository centerRepository;

    @Autowired
    private CenterMapper centerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCenterMockMvc;

    private Center center;

    private Center insertedCenter;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Center createEntity() {
        return new Center()
            .name(DEFAULT_NAME)
            .longitude(DEFAULT_LONGITUDE)
            .latitude(DEFAULT_LATITUDE)
            .status(DEFAULT_STATUS)
            .description(DEFAULT_DESCRIPTION)
            .availableSeats(DEFAULT_AVAILABLE_SEATS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Center createUpdatedEntity() {
        return new Center()
            .name(UPDATED_NAME)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .status(UPDATED_STATUS)
            .description(UPDATED_DESCRIPTION)
            .availableSeats(UPDATED_AVAILABLE_SEATS);
    }

    @BeforeEach
    void initTest() {
        center = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCenter != null) {
            centerRepository.delete(insertedCenter);
            insertedCenter = null;
        }
    }

    @Test
    @Transactional
    void createCenter() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Center
        CenterDTO centerDTO = centerMapper.toDto(center);
        var returnedCenterDTO = om.readValue(
            restCenterMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(centerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CenterDTO.class
        );

        // Validate the Center in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCenter = centerMapper.toEntity(returnedCenterDTO);
        assertCenterUpdatableFieldsEquals(returnedCenter, getPersistedCenter(returnedCenter));

        insertedCenter = returnedCenter;
    }

    @Test
    @Transactional
    void createCenterWithExistingId() throws Exception {
        // Create the Center with an existing ID
        center.setId(1L);
        CenterDTO centerDTO = centerMapper.toDto(center);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCenterMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(centerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Center in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCenters() throws Exception {
        // Initialize the database
        insertedCenter = centerRepository.saveAndFlush(center);

        // Get all the centerList
        restCenterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(center.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].availableSeats").value(hasItem(DEFAULT_AVAILABLE_SEATS)));
    }

    @Test
    @Transactional
    void getCenter() throws Exception {
        // Initialize the database
        insertedCenter = centerRepository.saveAndFlush(center);

        // Get the center
        restCenterMockMvc
            .perform(get(ENTITY_API_URL_ID, center.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(center.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.availableSeats").value(DEFAULT_AVAILABLE_SEATS));
    }

    @Test
    @Transactional
    void getNonExistingCenter() throws Exception {
        // Get the center
        restCenterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCenter() throws Exception {
        // Initialize the database
        insertedCenter = centerRepository.saveAndFlush(center);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the center
        Center updatedCenter = centerRepository.findById(center.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCenter are not directly saved in db
        em.detach(updatedCenter);
        updatedCenter
            .name(UPDATED_NAME)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .status(UPDATED_STATUS)
            .description(UPDATED_DESCRIPTION)
            .availableSeats(UPDATED_AVAILABLE_SEATS);
        CenterDTO centerDTO = centerMapper.toDto(updatedCenter);

        restCenterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, centerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(centerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Center in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCenterToMatchAllProperties(updatedCenter);
    }

    @Test
    @Transactional
    void putNonExistingCenter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        center.setId(longCount.incrementAndGet());

        // Create the Center
        CenterDTO centerDTO = centerMapper.toDto(center);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCenterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, centerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(centerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Center in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCenter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        center.setId(longCount.incrementAndGet());

        // Create the Center
        CenterDTO centerDTO = centerMapper.toDto(center);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCenterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(centerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Center in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCenter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        center.setId(longCount.incrementAndGet());

        // Create the Center
        CenterDTO centerDTO = centerMapper.toDto(center);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCenterMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(centerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Center in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCenterWithPatch() throws Exception {
        // Initialize the database
        insertedCenter = centerRepository.saveAndFlush(center);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the center using partial update
        Center partialUpdatedCenter = new Center();
        partialUpdatedCenter.setId(center.getId());

        partialUpdatedCenter
            .name(UPDATED_NAME)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .description(UPDATED_DESCRIPTION)
            .availableSeats(UPDATED_AVAILABLE_SEATS);

        restCenterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCenter.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCenter))
            )
            .andExpect(status().isOk());

        // Validate the Center in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCenterUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCenter, center), getPersistedCenter(center));
    }

    @Test
    @Transactional
    void fullUpdateCenterWithPatch() throws Exception {
        // Initialize the database
        insertedCenter = centerRepository.saveAndFlush(center);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the center using partial update
        Center partialUpdatedCenter = new Center();
        partialUpdatedCenter.setId(center.getId());

        partialUpdatedCenter
            .name(UPDATED_NAME)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .status(UPDATED_STATUS)
            .description(UPDATED_DESCRIPTION)
            .availableSeats(UPDATED_AVAILABLE_SEATS);

        restCenterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCenter.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCenter))
            )
            .andExpect(status().isOk());

        // Validate the Center in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCenterUpdatableFieldsEquals(partialUpdatedCenter, getPersistedCenter(partialUpdatedCenter));
    }

    @Test
    @Transactional
    void patchNonExistingCenter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        center.setId(longCount.incrementAndGet());

        // Create the Center
        CenterDTO centerDTO = centerMapper.toDto(center);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCenterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, centerDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(centerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Center in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCenter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        center.setId(longCount.incrementAndGet());

        // Create the Center
        CenterDTO centerDTO = centerMapper.toDto(center);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCenterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(centerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Center in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCenter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        center.setId(longCount.incrementAndGet());

        // Create the Center
        CenterDTO centerDTO = centerMapper.toDto(center);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCenterMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(centerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Center in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCenter() throws Exception {
        // Initialize the database
        insertedCenter = centerRepository.saveAndFlush(center);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the center
        restCenterMockMvc
            .perform(delete(ENTITY_API_URL_ID, center.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return centerRepository.count();
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

    protected Center getPersistedCenter(Center center) {
        return centerRepository.findById(center.getId()).orElseThrow();
    }

    protected void assertPersistedCenterToMatchAllProperties(Center expectedCenter) {
        assertCenterAllPropertiesEquals(expectedCenter, getPersistedCenter(expectedCenter));
    }

    protected void assertPersistedCenterToMatchUpdatableProperties(Center expectedCenter) {
        assertCenterAllUpdatablePropertiesEquals(expectedCenter, getPersistedCenter(expectedCenter));
    }
}
