package com.mpp.disaster.service;

import com.mpp.disaster.domain.CommunityMessage;
import com.mpp.disaster.repository.CommunityMessageRepository;
import com.mpp.disaster.service.dto.CommunityMessageDTO;
import com.mpp.disaster.service.mapper.CommunityMessageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mpp.disaster.domain.CommunityMessage}.
 */
@Service
@Transactional
public class CommunityMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(CommunityMessageService.class);

    private final CommunityMessageRepository communityMessageRepository;

    private final CommunityMessageMapper communityMessageMapper;

    public CommunityMessageService(CommunityMessageRepository communityMessageRepository, CommunityMessageMapper communityMessageMapper) {
        this.communityMessageRepository = communityMessageRepository;
        this.communityMessageMapper = communityMessageMapper;
    }

    /**
     * Save a communityMessage.
     *
     * @param communityMessageDTO the entity to save.
     * @return the persisted entity.
     */
    public CommunityMessageDTO save(CommunityMessageDTO communityMessageDTO) {
        LOG.debug("Request to save CommunityMessage : {}", communityMessageDTO);
        CommunityMessage communityMessage = communityMessageMapper.toEntity(communityMessageDTO);
        communityMessage = communityMessageRepository.save(communityMessage);
        return communityMessageMapper.toDto(communityMessage);
    }

    /**
     * Update a communityMessage.
     *
     * @param communityMessageDTO the entity to save.
     * @return the persisted entity.
     */
    public CommunityMessageDTO update(CommunityMessageDTO communityMessageDTO) {
        LOG.debug("Request to update CommunityMessage : {}", communityMessageDTO);
        CommunityMessage communityMessage = communityMessageMapper.toEntity(communityMessageDTO);
        communityMessage = communityMessageRepository.save(communityMessage);
        return communityMessageMapper.toDto(communityMessage);
    }

    /**
     * Partially update a communityMessage.
     *
     * @param communityMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CommunityMessageDTO> partialUpdate(CommunityMessageDTO communityMessageDTO) {
        LOG.debug("Request to partially update CommunityMessage : {}", communityMessageDTO);

        return communityMessageRepository
            .findById(communityMessageDTO.getId())
            .map(existingCommunityMessage -> {
                communityMessageMapper.partialUpdate(existingCommunityMessage, communityMessageDTO);

                return existingCommunityMessage;
            })
            .map(communityMessageRepository::save)
            .map(communityMessageMapper::toDto);
    }

    /**
     * Get all the communityMessages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CommunityMessageDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CommunityMessages");
        return communityMessageRepository.findAll(pageable).map(communityMessageMapper::toDto);
    }

    /**
     * Get all the communityMessages with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CommunityMessageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return communityMessageRepository.findAllWithEagerRelationships(pageable).map(communityMessageMapper::toDto);
    }

    /**
     * Get one communityMessage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CommunityMessageDTO> findOne(Long id) {
        LOG.debug("Request to get CommunityMessage : {}", id);
        return communityMessageRepository.findOneWithEagerRelationships(id).map(communityMessageMapper::toDto);
    }

    /**
     * Delete the communityMessage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CommunityMessage : {}", id);
        communityMessageRepository.deleteById(id);
    }
}
