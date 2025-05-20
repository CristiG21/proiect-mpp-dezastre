package com.mpp.disaster.web.rest;

import static com.mpp.disaster.domain.OfficialMessageAsserts.*;
import static com.mpp.disaster.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpp.disaster.IntegrationTest;
import com.mpp.disaster.domain.OfficialMessage;
import com.mpp.disaster.domain.User;
import com.mpp.disaster.repository.OfficialMessageRepository;
import com.mpp.disaster.repository.UserRepository;
import com.mpp.disaster.service.OfficialMessageService;
import com.mpp.disaster.service.dto.OfficialMessageDTO;
import com.mpp.disaster.service.mapper.OfficialMessageMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OfficialMessageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OfficialMessageResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIME_POSTED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME_POSTED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/official-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OfficialMessageRepository officialMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private OfficialMessageRepository officialMessageRepositoryMock;

    @Autowired
    private OfficialMessageMapper officialMessageMapper;

    @Mock
    private OfficialMessageService officialMessageServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOfficialMessageMockMvc;

    private OfficialMessage officialMessage;

    private OfficialMessage insertedOfficialMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OfficialMessage createEntity(EntityManager em) {
        OfficialMessage officialMessage = new OfficialMessage().title(DEFAULT_TITLE).body(DEFAULT_BODY).timePosted(DEFAULT_TIME_POSTED);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        officialMessage.setUser(user);
        return officialMessage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OfficialMessage createUpdatedEntity(EntityManager em) {
        OfficialMessage updatedOfficialMessage = new OfficialMessage()
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .timePosted(UPDATED_TIME_POSTED);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedOfficialMessage.setUser(user);
        return updatedOfficialMessage;
    }

    @BeforeEach
    void initTest() {
        officialMessage = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedOfficialMessage != null) {
            officialMessageRepository.delete(insertedOfficialMessage);
            insertedOfficialMessage = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createOfficialMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OfficialMessage
        OfficialMessageDTO officialMessageDTO = officialMessageMapper.toDto(officialMessage);
        var returnedOfficialMessageDTO = om.readValue(
            restOfficialMessageMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(officialMessageDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OfficialMessageDTO.class
        );

        // Validate the OfficialMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOfficialMessage = officialMessageMapper.toEntity(returnedOfficialMessageDTO);
        assertOfficialMessageUpdatableFieldsEquals(returnedOfficialMessage, getPersistedOfficialMessage(returnedOfficialMessage));

        insertedOfficialMessage = returnedOfficialMessage;
    }

    @Test
    @Transactional
    void createOfficialMessageWithExistingId() throws Exception {
        // Create the OfficialMessage with an existing ID
        officialMessage.setId(1L);
        OfficialMessageDTO officialMessageDTO = officialMessageMapper.toDto(officialMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOfficialMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(officialMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OfficialMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBodyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        officialMessage.setBody(null);

        // Create the OfficialMessage, which fails.
        OfficialMessageDTO officialMessageDTO = officialMessageMapper.toDto(officialMessage);

        restOfficialMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(officialMessageDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimePostedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        officialMessage.setTimePosted(null);

        // Create the OfficialMessage, which fails.
        OfficialMessageDTO officialMessageDTO = officialMessageMapper.toDto(officialMessage);

        restOfficialMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(officialMessageDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOfficialMessages() throws Exception {
        // Initialize the database
        insertedOfficialMessage = officialMessageRepository.saveAndFlush(officialMessage);

        // Get all the officialMessageList
        restOfficialMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(officialMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].timePosted").value(hasItem(DEFAULT_TIME_POSTED.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOfficialMessagesWithEagerRelationshipsIsEnabled() throws Exception {
        when(officialMessageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOfficialMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(officialMessageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOfficialMessagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(officialMessageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOfficialMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(officialMessageRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOfficialMessage() throws Exception {
        // Initialize the database
        insertedOfficialMessage = officialMessageRepository.saveAndFlush(officialMessage);

        // Get the officialMessage
        restOfficialMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, officialMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(officialMessage.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.timePosted").value(DEFAULT_TIME_POSTED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingOfficialMessage() throws Exception {
        // Get the officialMessage
        restOfficialMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOfficialMessage() throws Exception {
        // Initialize the database
        insertedOfficialMessage = officialMessageRepository.saveAndFlush(officialMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the officialMessage
        OfficialMessage updatedOfficialMessage = officialMessageRepository.findById(officialMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOfficialMessage are not directly saved in db
        em.detach(updatedOfficialMessage);
        updatedOfficialMessage.title(UPDATED_TITLE).body(UPDATED_BODY).timePosted(UPDATED_TIME_POSTED);
        OfficialMessageDTO officialMessageDTO = officialMessageMapper.toDto(updatedOfficialMessage);

        restOfficialMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, officialMessageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(officialMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the OfficialMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOfficialMessageToMatchAllProperties(updatedOfficialMessage);
    }

    @Test
    @Transactional
    void putNonExistingOfficialMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        officialMessage.setId(longCount.incrementAndGet());

        // Create the OfficialMessage
        OfficialMessageDTO officialMessageDTO = officialMessageMapper.toDto(officialMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOfficialMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, officialMessageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(officialMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OfficialMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOfficialMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        officialMessage.setId(longCount.incrementAndGet());

        // Create the OfficialMessage
        OfficialMessageDTO officialMessageDTO = officialMessageMapper.toDto(officialMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOfficialMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(officialMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OfficialMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOfficialMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        officialMessage.setId(longCount.incrementAndGet());

        // Create the OfficialMessage
        OfficialMessageDTO officialMessageDTO = officialMessageMapper.toDto(officialMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOfficialMessageMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(officialMessageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OfficialMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOfficialMessageWithPatch() throws Exception {
        // Initialize the database
        insertedOfficialMessage = officialMessageRepository.saveAndFlush(officialMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the officialMessage using partial update
        OfficialMessage partialUpdatedOfficialMessage = new OfficialMessage();
        partialUpdatedOfficialMessage.setId(officialMessage.getId());

        restOfficialMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOfficialMessage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOfficialMessage))
            )
            .andExpect(status().isOk());

        // Validate the OfficialMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOfficialMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOfficialMessage, officialMessage),
            getPersistedOfficialMessage(officialMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateOfficialMessageWithPatch() throws Exception {
        // Initialize the database
        insertedOfficialMessage = officialMessageRepository.saveAndFlush(officialMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the officialMessage using partial update
        OfficialMessage partialUpdatedOfficialMessage = new OfficialMessage();
        partialUpdatedOfficialMessage.setId(officialMessage.getId());

        partialUpdatedOfficialMessage.title(UPDATED_TITLE).body(UPDATED_BODY).timePosted(UPDATED_TIME_POSTED);

        restOfficialMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOfficialMessage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOfficialMessage))
            )
            .andExpect(status().isOk());

        // Validate the OfficialMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOfficialMessageUpdatableFieldsEquals(
            partialUpdatedOfficialMessage,
            getPersistedOfficialMessage(partialUpdatedOfficialMessage)
        );
    }

    @Test
    @Transactional
    void patchNonExistingOfficialMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        officialMessage.setId(longCount.incrementAndGet());

        // Create the OfficialMessage
        OfficialMessageDTO officialMessageDTO = officialMessageMapper.toDto(officialMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOfficialMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, officialMessageDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(officialMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OfficialMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOfficialMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        officialMessage.setId(longCount.incrementAndGet());

        // Create the OfficialMessage
        OfficialMessageDTO officialMessageDTO = officialMessageMapper.toDto(officialMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOfficialMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(officialMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OfficialMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOfficialMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        officialMessage.setId(longCount.incrementAndGet());

        // Create the OfficialMessage
        OfficialMessageDTO officialMessageDTO = officialMessageMapper.toDto(officialMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOfficialMessageMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(officialMessageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OfficialMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOfficialMessage() throws Exception {
        // Initialize the database
        insertedOfficialMessage = officialMessageRepository.saveAndFlush(officialMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the officialMessage
        restOfficialMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, officialMessage.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return officialMessageRepository.count();
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

    protected OfficialMessage getPersistedOfficialMessage(OfficialMessage officialMessage) {
        return officialMessageRepository.findById(officialMessage.getId()).orElseThrow();
    }

    protected void assertPersistedOfficialMessageToMatchAllProperties(OfficialMessage expectedOfficialMessage) {
        assertOfficialMessageAllPropertiesEquals(expectedOfficialMessage, getPersistedOfficialMessage(expectedOfficialMessage));
    }

    protected void assertPersistedOfficialMessageToMatchUpdatableProperties(OfficialMessage expectedOfficialMessage) {
        assertOfficialMessageAllUpdatablePropertiesEquals(expectedOfficialMessage, getPersistedOfficialMessage(expectedOfficialMessage));
    }
}
