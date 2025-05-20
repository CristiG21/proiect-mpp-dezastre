package com.mpp.disaster.web.rest;

import static com.mpp.disaster.domain.CenterTypeWrapperAsserts.*;
import static com.mpp.disaster.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpp.disaster.IntegrationTest;
import com.mpp.disaster.domain.CenterTypeWrapper;
import com.mpp.disaster.domain.enumeration.CenterType;
import com.mpp.disaster.repository.CenterTypeWrapperRepository;
import com.mpp.disaster.service.dto.CenterTypeWrapperDTO;
import com.mpp.disaster.service.mapper.CenterTypeWrapperMapper;
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
 * Integration tests for the {@link CenterTypeWrapperResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CenterTypeWrapperResourceIT {

    private static final CenterType DEFAULT_TYPE = CenterType.SHELTER;
    private static final CenterType UPDATED_TYPE = CenterType.MEDICAL;

    private static final String ENTITY_API_URL = "/api/center-type-wrappers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CenterTypeWrapperRepository centerTypeWrapperRepository;

    @Autowired
    private CenterTypeWrapperMapper centerTypeWrapperMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCenterTypeWrapperMockMvc;

    private CenterTypeWrapper centerTypeWrapper;

    private CenterTypeWrapper insertedCenterTypeWrapper;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CenterTypeWrapper createEntity() {
        return new CenterTypeWrapper().type(DEFAULT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CenterTypeWrapper createUpdatedEntity() {
        return new CenterTypeWrapper().type(UPDATED_TYPE);
    }

    @BeforeEach
    void initTest() {
        centerTypeWrapper = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCenterTypeWrapper != null) {
            centerTypeWrapperRepository.delete(insertedCenterTypeWrapper);
            insertedCenterTypeWrapper = null;
        }
    }

    @Test
    @Transactional
    void createCenterTypeWrapper() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CenterTypeWrapper
        CenterTypeWrapperDTO centerTypeWrapperDTO = centerTypeWrapperMapper.toDto(centerTypeWrapper);
        var returnedCenterTypeWrapperDTO = om.readValue(
            restCenterTypeWrapperMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(centerTypeWrapperDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CenterTypeWrapperDTO.class
        );

        // Validate the CenterTypeWrapper in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCenterTypeWrapper = centerTypeWrapperMapper.toEntity(returnedCenterTypeWrapperDTO);
        assertCenterTypeWrapperUpdatableFieldsEquals(returnedCenterTypeWrapper, getPersistedCenterTypeWrapper(returnedCenterTypeWrapper));

        insertedCenterTypeWrapper = returnedCenterTypeWrapper;
    }

    @Test
    @Transactional
    void createCenterTypeWrapperWithExistingId() throws Exception {
        // Create the CenterTypeWrapper with an existing ID
        centerTypeWrapper.setId(1L);
        CenterTypeWrapperDTO centerTypeWrapperDTO = centerTypeWrapperMapper.toDto(centerTypeWrapper);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCenterTypeWrapperMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(centerTypeWrapperDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CenterTypeWrapper in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCenterTypeWrappers() throws Exception {
        // Initialize the database
        insertedCenterTypeWrapper = centerTypeWrapperRepository.saveAndFlush(centerTypeWrapper);

        // Get all the centerTypeWrapperList
        restCenterTypeWrapperMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(centerTypeWrapper.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getCenterTypeWrapper() throws Exception {
        // Initialize the database
        insertedCenterTypeWrapper = centerTypeWrapperRepository.saveAndFlush(centerTypeWrapper);

        // Get the centerTypeWrapper
        restCenterTypeWrapperMockMvc
            .perform(get(ENTITY_API_URL_ID, centerTypeWrapper.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(centerTypeWrapper.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCenterTypeWrapper() throws Exception {
        // Get the centerTypeWrapper
        restCenterTypeWrapperMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCenterTypeWrapper() throws Exception {
        // Initialize the database
        insertedCenterTypeWrapper = centerTypeWrapperRepository.saveAndFlush(centerTypeWrapper);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the centerTypeWrapper
        CenterTypeWrapper updatedCenterTypeWrapper = centerTypeWrapperRepository.findById(centerTypeWrapper.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCenterTypeWrapper are not directly saved in db
        em.detach(updatedCenterTypeWrapper);
        updatedCenterTypeWrapper.type(UPDATED_TYPE);
        CenterTypeWrapperDTO centerTypeWrapperDTO = centerTypeWrapperMapper.toDto(updatedCenterTypeWrapper);

        restCenterTypeWrapperMockMvc
            .perform(
                put(ENTITY_API_URL_ID, centerTypeWrapperDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(centerTypeWrapperDTO))
            )
            .andExpect(status().isOk());

        // Validate the CenterTypeWrapper in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCenterTypeWrapperToMatchAllProperties(updatedCenterTypeWrapper);
    }

    @Test
    @Transactional
    void putNonExistingCenterTypeWrapper() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        centerTypeWrapper.setId(longCount.incrementAndGet());

        // Create the CenterTypeWrapper
        CenterTypeWrapperDTO centerTypeWrapperDTO = centerTypeWrapperMapper.toDto(centerTypeWrapper);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCenterTypeWrapperMockMvc
            .perform(
                put(ENTITY_API_URL_ID, centerTypeWrapperDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(centerTypeWrapperDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CenterTypeWrapper in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCenterTypeWrapper() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        centerTypeWrapper.setId(longCount.incrementAndGet());

        // Create the CenterTypeWrapper
        CenterTypeWrapperDTO centerTypeWrapperDTO = centerTypeWrapperMapper.toDto(centerTypeWrapper);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCenterTypeWrapperMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(centerTypeWrapperDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CenterTypeWrapper in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCenterTypeWrapper() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        centerTypeWrapper.setId(longCount.incrementAndGet());

        // Create the CenterTypeWrapper
        CenterTypeWrapperDTO centerTypeWrapperDTO = centerTypeWrapperMapper.toDto(centerTypeWrapper);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCenterTypeWrapperMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(centerTypeWrapperDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CenterTypeWrapper in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCenterTypeWrapperWithPatch() throws Exception {
        // Initialize the database
        insertedCenterTypeWrapper = centerTypeWrapperRepository.saveAndFlush(centerTypeWrapper);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the centerTypeWrapper using partial update
        CenterTypeWrapper partialUpdatedCenterTypeWrapper = new CenterTypeWrapper();
        partialUpdatedCenterTypeWrapper.setId(centerTypeWrapper.getId());

        partialUpdatedCenterTypeWrapper.type(UPDATED_TYPE);

        restCenterTypeWrapperMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCenterTypeWrapper.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCenterTypeWrapper))
            )
            .andExpect(status().isOk());

        // Validate the CenterTypeWrapper in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCenterTypeWrapperUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCenterTypeWrapper, centerTypeWrapper),
            getPersistedCenterTypeWrapper(centerTypeWrapper)
        );
    }

    @Test
    @Transactional
    void fullUpdateCenterTypeWrapperWithPatch() throws Exception {
        // Initialize the database
        insertedCenterTypeWrapper = centerTypeWrapperRepository.saveAndFlush(centerTypeWrapper);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the centerTypeWrapper using partial update
        CenterTypeWrapper partialUpdatedCenterTypeWrapper = new CenterTypeWrapper();
        partialUpdatedCenterTypeWrapper.setId(centerTypeWrapper.getId());

        partialUpdatedCenterTypeWrapper.type(UPDATED_TYPE);

        restCenterTypeWrapperMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCenterTypeWrapper.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCenterTypeWrapper))
            )
            .andExpect(status().isOk());

        // Validate the CenterTypeWrapper in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCenterTypeWrapperUpdatableFieldsEquals(
            partialUpdatedCenterTypeWrapper,
            getPersistedCenterTypeWrapper(partialUpdatedCenterTypeWrapper)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCenterTypeWrapper() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        centerTypeWrapper.setId(longCount.incrementAndGet());

        // Create the CenterTypeWrapper
        CenterTypeWrapperDTO centerTypeWrapperDTO = centerTypeWrapperMapper.toDto(centerTypeWrapper);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCenterTypeWrapperMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, centerTypeWrapperDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(centerTypeWrapperDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CenterTypeWrapper in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCenterTypeWrapper() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        centerTypeWrapper.setId(longCount.incrementAndGet());

        // Create the CenterTypeWrapper
        CenterTypeWrapperDTO centerTypeWrapperDTO = centerTypeWrapperMapper.toDto(centerTypeWrapper);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCenterTypeWrapperMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(centerTypeWrapperDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CenterTypeWrapper in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCenterTypeWrapper() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        centerTypeWrapper.setId(longCount.incrementAndGet());

        // Create the CenterTypeWrapper
        CenterTypeWrapperDTO centerTypeWrapperDTO = centerTypeWrapperMapper.toDto(centerTypeWrapper);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCenterTypeWrapperMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(centerTypeWrapperDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CenterTypeWrapper in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCenterTypeWrapper() throws Exception {
        // Initialize the database
        insertedCenterTypeWrapper = centerTypeWrapperRepository.saveAndFlush(centerTypeWrapper);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the centerTypeWrapper
        restCenterTypeWrapperMockMvc
            .perform(delete(ENTITY_API_URL_ID, centerTypeWrapper.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return centerTypeWrapperRepository.count();
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

    protected CenterTypeWrapper getPersistedCenterTypeWrapper(CenterTypeWrapper centerTypeWrapper) {
        return centerTypeWrapperRepository.findById(centerTypeWrapper.getId()).orElseThrow();
    }

    protected void assertPersistedCenterTypeWrapperToMatchAllProperties(CenterTypeWrapper expectedCenterTypeWrapper) {
        assertCenterTypeWrapperAllPropertiesEquals(expectedCenterTypeWrapper, getPersistedCenterTypeWrapper(expectedCenterTypeWrapper));
    }

    protected void assertPersistedCenterTypeWrapperToMatchUpdatableProperties(CenterTypeWrapper expectedCenterTypeWrapper) {
        assertCenterTypeWrapperAllUpdatablePropertiesEquals(
            expectedCenterTypeWrapper,
            getPersistedCenterTypeWrapper(expectedCenterTypeWrapper)
        );
    }
}
