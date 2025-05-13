package com.mpp.disaster.web.rest;

import static com.mpp.disaster.domain.PhotoURLAsserts.*;
import static com.mpp.disaster.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpp.disaster.IntegrationTest;
import com.mpp.disaster.domain.PhotoURL;
import com.mpp.disaster.repository.PhotoURLRepository;
import com.mpp.disaster.service.dto.PhotoURLDTO;
import com.mpp.disaster.service.mapper.PhotoURLMapper;
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
 * Integration tests for the {@link PhotoURLResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PhotoURLResourceIT {

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/photo-urls";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PhotoURLRepository photoURLRepository;

    @Autowired
    private PhotoURLMapper photoURLMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPhotoURLMockMvc;

    private PhotoURL photoURL;

    private PhotoURL insertedPhotoURL;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PhotoURL createEntity() {
        return new PhotoURL().url(DEFAULT_URL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PhotoURL createUpdatedEntity() {
        return new PhotoURL().url(UPDATED_URL);
    }

    @BeforeEach
    void initTest() {
        photoURL = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPhotoURL != null) {
            photoURLRepository.delete(insertedPhotoURL);
            insertedPhotoURL = null;
        }
    }

    @Test
    @Transactional
    void createPhotoURL() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PhotoURL
        PhotoURLDTO photoURLDTO = photoURLMapper.toDto(photoURL);
        var returnedPhotoURLDTO = om.readValue(
            restPhotoURLMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(photoURLDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PhotoURLDTO.class
        );

        // Validate the PhotoURL in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPhotoURL = photoURLMapper.toEntity(returnedPhotoURLDTO);
        assertPhotoURLUpdatableFieldsEquals(returnedPhotoURL, getPersistedPhotoURL(returnedPhotoURL));

        insertedPhotoURL = returnedPhotoURL;
    }

    @Test
    @Transactional
    void createPhotoURLWithExistingId() throws Exception {
        // Create the PhotoURL with an existing ID
        photoURL.setId(1L);
        PhotoURLDTO photoURLDTO = photoURLMapper.toDto(photoURL);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPhotoURLMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(photoURLDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PhotoURL in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPhotoURLS() throws Exception {
        // Initialize the database
        insertedPhotoURL = photoURLRepository.saveAndFlush(photoURL);

        // Get all the photoURLList
        restPhotoURLMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(photoURL.getId().intValue())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));
    }

    @Test
    @Transactional
    void getPhotoURL() throws Exception {
        // Initialize the database
        insertedPhotoURL = photoURLRepository.saveAndFlush(photoURL);

        // Get the photoURL
        restPhotoURLMockMvc
            .perform(get(ENTITY_API_URL_ID, photoURL.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(photoURL.getId().intValue()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL));
    }

    @Test
    @Transactional
    void getNonExistingPhotoURL() throws Exception {
        // Get the photoURL
        restPhotoURLMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPhotoURL() throws Exception {
        // Initialize the database
        insertedPhotoURL = photoURLRepository.saveAndFlush(photoURL);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the photoURL
        PhotoURL updatedPhotoURL = photoURLRepository.findById(photoURL.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPhotoURL are not directly saved in db
        em.detach(updatedPhotoURL);
        updatedPhotoURL.url(UPDATED_URL);
        PhotoURLDTO photoURLDTO = photoURLMapper.toDto(updatedPhotoURL);

        restPhotoURLMockMvc
            .perform(
                put(ENTITY_API_URL_ID, photoURLDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(photoURLDTO))
            )
            .andExpect(status().isOk());

        // Validate the PhotoURL in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPhotoURLToMatchAllProperties(updatedPhotoURL);
    }

    @Test
    @Transactional
    void putNonExistingPhotoURL() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        photoURL.setId(longCount.incrementAndGet());

        // Create the PhotoURL
        PhotoURLDTO photoURLDTO = photoURLMapper.toDto(photoURL);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPhotoURLMockMvc
            .perform(
                put(ENTITY_API_URL_ID, photoURLDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(photoURLDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PhotoURL in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPhotoURL() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        photoURL.setId(longCount.incrementAndGet());

        // Create the PhotoURL
        PhotoURLDTO photoURLDTO = photoURLMapper.toDto(photoURL);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhotoURLMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(photoURLDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PhotoURL in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPhotoURL() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        photoURL.setId(longCount.incrementAndGet());

        // Create the PhotoURL
        PhotoURLDTO photoURLDTO = photoURLMapper.toDto(photoURL);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhotoURLMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(photoURLDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PhotoURL in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePhotoURLWithPatch() throws Exception {
        // Initialize the database
        insertedPhotoURL = photoURLRepository.saveAndFlush(photoURL);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the photoURL using partial update
        PhotoURL partialUpdatedPhotoURL = new PhotoURL();
        partialUpdatedPhotoURL.setId(photoURL.getId());

        partialUpdatedPhotoURL.url(UPDATED_URL);

        restPhotoURLMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPhotoURL.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPhotoURL))
            )
            .andExpect(status().isOk());

        // Validate the PhotoURL in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPhotoURLUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPhotoURL, photoURL), getPersistedPhotoURL(photoURL));
    }

    @Test
    @Transactional
    void fullUpdatePhotoURLWithPatch() throws Exception {
        // Initialize the database
        insertedPhotoURL = photoURLRepository.saveAndFlush(photoURL);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the photoURL using partial update
        PhotoURL partialUpdatedPhotoURL = new PhotoURL();
        partialUpdatedPhotoURL.setId(photoURL.getId());

        partialUpdatedPhotoURL.url(UPDATED_URL);

        restPhotoURLMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPhotoURL.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPhotoURL))
            )
            .andExpect(status().isOk());

        // Validate the PhotoURL in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPhotoURLUpdatableFieldsEquals(partialUpdatedPhotoURL, getPersistedPhotoURL(partialUpdatedPhotoURL));
    }

    @Test
    @Transactional
    void patchNonExistingPhotoURL() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        photoURL.setId(longCount.incrementAndGet());

        // Create the PhotoURL
        PhotoURLDTO photoURLDTO = photoURLMapper.toDto(photoURL);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPhotoURLMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, photoURLDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(photoURLDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PhotoURL in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPhotoURL() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        photoURL.setId(longCount.incrementAndGet());

        // Create the PhotoURL
        PhotoURLDTO photoURLDTO = photoURLMapper.toDto(photoURL);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhotoURLMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(photoURLDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PhotoURL in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPhotoURL() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        photoURL.setId(longCount.incrementAndGet());

        // Create the PhotoURL
        PhotoURLDTO photoURLDTO = photoURLMapper.toDto(photoURL);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhotoURLMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(photoURLDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PhotoURL in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePhotoURL() throws Exception {
        // Initialize the database
        insertedPhotoURL = photoURLRepository.saveAndFlush(photoURL);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the photoURL
        restPhotoURLMockMvc
            .perform(delete(ENTITY_API_URL_ID, photoURL.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return photoURLRepository.count();
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

    protected PhotoURL getPersistedPhotoURL(PhotoURL photoURL) {
        return photoURLRepository.findById(photoURL.getId()).orElseThrow();
    }

    protected void assertPersistedPhotoURLToMatchAllProperties(PhotoURL expectedPhotoURL) {
        assertPhotoURLAllPropertiesEquals(expectedPhotoURL, getPersistedPhotoURL(expectedPhotoURL));
    }

    protected void assertPersistedPhotoURLToMatchUpdatableProperties(PhotoURL expectedPhotoURL) {
        assertPhotoURLAllUpdatablePropertiesEquals(expectedPhotoURL, getPersistedPhotoURL(expectedPhotoURL));
    }
}
