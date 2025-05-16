package com.mpp.disaster.web.rest;

import static com.mpp.disaster.domain.CommunityMessageAsserts.*;
import static com.mpp.disaster.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpp.disaster.IntegrationTest;
import com.mpp.disaster.domain.CommunityMessage;
import com.mpp.disaster.domain.User;
import com.mpp.disaster.domain.enumeration.MessageType;
import com.mpp.disaster.repository.CommunityMessageRepository;
import com.mpp.disaster.repository.UserRepository;
import com.mpp.disaster.service.CommunityMessageService;
import com.mpp.disaster.service.dto.CommunityMessageDTO;
import com.mpp.disaster.service.mapper.CommunityMessageMapper;
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
 * Integration tests for the {@link CommunityMessageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CommunityMessageResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIME_POSTED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME_POSTED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final MessageType DEFAULT_TYPE = MessageType.COMMUNITY;
    private static final MessageType UPDATED_TYPE = MessageType.OFFICIAL;

    private static final Integer DEFAULT_PARENT_ID = 1;
    private static final Integer UPDATED_PARENT_ID = 2;

    private static final Boolean DEFAULT_APPROVED = false;
    private static final Boolean UPDATED_APPROVED = true;

    private static final String ENTITY_API_URL = "/api/community-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CommunityMessageRepository communityMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CommunityMessageRepository communityMessageRepositoryMock;

    @Autowired
    private CommunityMessageMapper communityMessageMapper;

    @Mock
    private CommunityMessageService communityMessageServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommunityMessageMockMvc;

    private CommunityMessage communityMessage;

    private CommunityMessage insertedCommunityMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommunityMessage createEntity(EntityManager em) {
        CommunityMessage communityMessage = new CommunityMessage()
            .content(DEFAULT_CONTENT)
            .time_posted(DEFAULT_TIME_POSTED)
            .type(DEFAULT_TYPE)
            .parentId(DEFAULT_PARENT_ID)
            .approved(DEFAULT_APPROVED);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        communityMessage.setUser(user);
        return communityMessage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommunityMessage createUpdatedEntity(EntityManager em) {
        CommunityMessage updatedCommunityMessage = new CommunityMessage()
            .content(UPDATED_CONTENT)
            .time_posted(UPDATED_TIME_POSTED)
            .type(UPDATED_TYPE)
            .parentId(UPDATED_PARENT_ID)
            .approved(UPDATED_APPROVED);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedCommunityMessage.setUser(user);
        return updatedCommunityMessage;
    }

    @BeforeEach
    void initTest() {
        communityMessage = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCommunityMessage != null) {
            communityMessageRepository.delete(insertedCommunityMessage);
            insertedCommunityMessage = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createCommunityMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CommunityMessage
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(communityMessage);
        var returnedCommunityMessageDTO = om.readValue(
            restCommunityMessageMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(communityMessageDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CommunityMessageDTO.class
        );

        // Validate the CommunityMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCommunityMessage = communityMessageMapper.toEntity(returnedCommunityMessageDTO);
        assertCommunityMessageUpdatableFieldsEquals(returnedCommunityMessage, getPersistedCommunityMessage(returnedCommunityMessage));

        insertedCommunityMessage = returnedCommunityMessage;
    }

    @Test
    @Transactional
    void createCommunityMessageWithExistingId() throws Exception {
        // Create the CommunityMessage with an existing ID
        communityMessage.setId(1L);
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(communityMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommunityMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        communityMessage.setContent(null);

        // Create the CommunityMessage, which fails.
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(communityMessage);

        restCommunityMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityMessageDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTime_postedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        communityMessage.setTime_posted(null);

        // Create the CommunityMessage, which fails.
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(communityMessage);

        restCommunityMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityMessageDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        communityMessage.setType(null);

        // Create the CommunityMessage, which fails.
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(communityMessage);

        restCommunityMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityMessageDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkApprovedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        communityMessage.setApproved(null);

        // Create the CommunityMessage, which fails.
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(communityMessage);

        restCommunityMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityMessageDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCommunityMessages() throws Exception {
        // Initialize the database
        insertedCommunityMessage = communityMessageRepository.saveAndFlush(communityMessage);

        // Get all the communityMessageList
        restCommunityMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(communityMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].time_posted").value(hasItem(DEFAULT_TIME_POSTED.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].parentId").value(hasItem(DEFAULT_PARENT_ID)))
            .andExpect(jsonPath("$.[*].approved").value(hasItem(DEFAULT_APPROVED)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCommunityMessagesWithEagerRelationshipsIsEnabled() throws Exception {
        when(communityMessageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCommunityMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(communityMessageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCommunityMessagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(communityMessageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCommunityMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(communityMessageRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCommunityMessage() throws Exception {
        // Initialize the database
        insertedCommunityMessage = communityMessageRepository.saveAndFlush(communityMessage);

        // Get the communityMessage
        restCommunityMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, communityMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(communityMessage.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.time_posted").value(DEFAULT_TIME_POSTED.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.parentId").value(DEFAULT_PARENT_ID))
            .andExpect(jsonPath("$.approved").value(DEFAULT_APPROVED));
    }

    @Test
    @Transactional
    void getNonExistingCommunityMessage() throws Exception {
        // Get the communityMessage
        restCommunityMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCommunityMessage() throws Exception {
        // Initialize the database
        insertedCommunityMessage = communityMessageRepository.saveAndFlush(communityMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the communityMessage
        CommunityMessage updatedCommunityMessage = communityMessageRepository.findById(communityMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCommunityMessage are not directly saved in db
        em.detach(updatedCommunityMessage);
        updatedCommunityMessage
            .content(UPDATED_CONTENT)
            .time_posted(UPDATED_TIME_POSTED)
            .type(UPDATED_TYPE)
            .parentId(UPDATED_PARENT_ID)
            .approved(UPDATED_APPROVED);
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(updatedCommunityMessage);

        restCommunityMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, communityMessageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(communityMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the CommunityMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCommunityMessageToMatchAllProperties(updatedCommunityMessage);
    }

    @Test
    @Transactional
    void putNonExistingCommunityMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityMessage.setId(longCount.incrementAndGet());

        // Create the CommunityMessage
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(communityMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommunityMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, communityMessageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(communityMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommunityMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityMessage.setId(longCount.incrementAndGet());

        // Create the CommunityMessage
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(communityMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(communityMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommunityMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityMessage.setId(longCount.incrementAndGet());

        // Create the CommunityMessage
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(communityMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityMessageMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityMessageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CommunityMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommunityMessageWithPatch() throws Exception {
        // Initialize the database
        insertedCommunityMessage = communityMessageRepository.saveAndFlush(communityMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the communityMessage using partial update
        CommunityMessage partialUpdatedCommunityMessage = new CommunityMessage();
        partialUpdatedCommunityMessage.setId(communityMessage.getId());

        partialUpdatedCommunityMessage
            .time_posted(UPDATED_TIME_POSTED)
            .type(UPDATED_TYPE)
            .parentId(UPDATED_PARENT_ID)
            .approved(UPDATED_APPROVED);

        restCommunityMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommunityMessage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCommunityMessage))
            )
            .andExpect(status().isOk());

        // Validate the CommunityMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommunityMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCommunityMessage, communityMessage),
            getPersistedCommunityMessage(communityMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateCommunityMessageWithPatch() throws Exception {
        // Initialize the database
        insertedCommunityMessage = communityMessageRepository.saveAndFlush(communityMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the communityMessage using partial update
        CommunityMessage partialUpdatedCommunityMessage = new CommunityMessage();
        partialUpdatedCommunityMessage.setId(communityMessage.getId());

        partialUpdatedCommunityMessage
            .content(UPDATED_CONTENT)
            .time_posted(UPDATED_TIME_POSTED)
            .type(UPDATED_TYPE)
            .parentId(UPDATED_PARENT_ID)
            .approved(UPDATED_APPROVED);

        restCommunityMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommunityMessage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCommunityMessage))
            )
            .andExpect(status().isOk());

        // Validate the CommunityMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommunityMessageUpdatableFieldsEquals(
            partialUpdatedCommunityMessage,
            getPersistedCommunityMessage(partialUpdatedCommunityMessage)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCommunityMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityMessage.setId(longCount.incrementAndGet());

        // Create the CommunityMessage
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(communityMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommunityMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, communityMessageDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(communityMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommunityMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityMessage.setId(longCount.incrementAndGet());

        // Create the CommunityMessage
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(communityMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(communityMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommunityMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityMessage.setId(longCount.incrementAndGet());

        // Create the CommunityMessage
        CommunityMessageDTO communityMessageDTO = communityMessageMapper.toDto(communityMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityMessageMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(communityMessageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CommunityMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommunityMessage() throws Exception {
        // Initialize the database
        insertedCommunityMessage = communityMessageRepository.saveAndFlush(communityMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the communityMessage
        restCommunityMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, communityMessage.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return communityMessageRepository.count();
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

    protected CommunityMessage getPersistedCommunityMessage(CommunityMessage communityMessage) {
        return communityMessageRepository.findById(communityMessage.getId()).orElseThrow();
    }

    protected void assertPersistedCommunityMessageToMatchAllProperties(CommunityMessage expectedCommunityMessage) {
        assertCommunityMessageAllPropertiesEquals(expectedCommunityMessage, getPersistedCommunityMessage(expectedCommunityMessage));
    }

    protected void assertPersistedCommunityMessageToMatchUpdatableProperties(CommunityMessage expectedCommunityMessage) {
        assertCommunityMessageAllUpdatablePropertiesEquals(
            expectedCommunityMessage,
            getPersistedCommunityMessage(expectedCommunityMessage)
        );
    }
}
