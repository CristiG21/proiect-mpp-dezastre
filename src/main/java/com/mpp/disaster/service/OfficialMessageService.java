package com.mpp.disaster.service;

import com.mpp.disaster.domain.OfficialMessage;
import com.mpp.disaster.repository.OfficialMessageRepository;
import com.mpp.disaster.service.dto.OfficialMessageDTO;
import com.mpp.disaster.service.mapper.OfficialMessageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mpp.disaster.domain.OfficialMessage}.
 */
@Service
@Transactional
public class OfficialMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(OfficialMessageService.class);

    private final OfficialMessageRepository officialMessageRepository;

    private final OfficialMessageMapper officialMessageMapper;

    public OfficialMessageService(OfficialMessageRepository officialMessageRepository, OfficialMessageMapper officialMessageMapper) {
        this.officialMessageRepository = officialMessageRepository;
        this.officialMessageMapper = officialMessageMapper;
    }

    /**
     * Save a officialMessage.
     *
     * @param officialMessageDTO the entity to save.
     * @return the persisted entity.
     */
    public OfficialMessageDTO save(OfficialMessageDTO officialMessageDTO) {
        LOG.debug("Request to save OfficialMessage : {}", officialMessageDTO);
        OfficialMessage officialMessage = officialMessageMapper.toEntity(officialMessageDTO);
        officialMessage = officialMessageRepository.save(officialMessage);
        return officialMessageMapper.toDto(officialMessage);
    }

    /**
     * Update a officialMessage.
     *
     * @param officialMessageDTO the entity to save.
     * @return the persisted entity.
     */
    public OfficialMessageDTO update(OfficialMessageDTO officialMessageDTO) {
        LOG.debug("Request to update OfficialMessage : {}", officialMessageDTO);
        OfficialMessage officialMessage = officialMessageMapper.toEntity(officialMessageDTO);
        officialMessage = officialMessageRepository.save(officialMessage);
        return officialMessageMapper.toDto(officialMessage);
    }

    /**
     * Partially update a officialMessage.
     *
     * @param officialMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OfficialMessageDTO> partialUpdate(OfficialMessageDTO officialMessageDTO) {
        LOG.debug("Request to partially update OfficialMessage : {}", officialMessageDTO);

        return officialMessageRepository
            .findById(officialMessageDTO.getId())
            .map(existingOfficialMessage -> {
                officialMessageMapper.partialUpdate(existingOfficialMessage, officialMessageDTO);

                return existingOfficialMessage;
            })
            .map(officialMessageRepository::save)
            .map(officialMessageMapper::toDto);
    }

    /**
     * Get all the officialMessages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OfficialMessageDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all OfficialMessages");
        return officialMessageRepository.findAll(pageable).map(officialMessageMapper::toDto);
    }

    /**
     * Get all the officialMessages with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<OfficialMessageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return officialMessageRepository.findAllWithEagerRelationships(pageable).map(officialMessageMapper::toDto);
    }

    /**
     * Get one officialMessage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OfficialMessageDTO> findOne(Long id) {
        LOG.debug("Request to get OfficialMessage : {}", id);
        return officialMessageRepository.findOneWithEagerRelationships(id).map(officialMessageMapper::toDto);
    }

    /**
     * Delete the officialMessage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete OfficialMessage : {}", id);
        officialMessageRepository.deleteById(id);
    }
}
