package com.mpp.disaster.service;

import com.mpp.disaster.domain.Center;
import com.mpp.disaster.repository.CenterRepository;
import com.mpp.disaster.service.dto.CenterDTO;
import com.mpp.disaster.service.mapper.CenterMapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mpp.disaster.domain.Center}.
 */
@Service
@Transactional
public class CenterService {

    private static final Logger LOG = LoggerFactory.getLogger(CenterService.class);

    private final CenterRepository centerRepository;

    private final CenterMapper centerMapper;

    public CenterService(CenterRepository centerRepository, CenterMapper centerMapper) {
        this.centerRepository = centerRepository;
        this.centerMapper = centerMapper;
    }

    /**
     * Save a center.
     *
     * @param centerDTO the entity to save.
     * @return the persisted entity.
     */
    public CenterDTO save(CenterDTO centerDTO) {
        LOG.debug("Request to save Center : {}", centerDTO);
        Center center = centerMapper.toEntity(centerDTO);
        center = centerRepository.save(center);
        return centerMapper.toDto(center);
    }

    /**
     * Update a center.
     *
     * @param centerDTO the entity to save.
     * @return the persisted entity.
     */
    public CenterDTO update(CenterDTO centerDTO) {
        LOG.debug("Request to update Center : {}", centerDTO);
        Center center = centerMapper.toEntity(centerDTO);
        center = centerRepository.save(center);
        return centerMapper.toDto(center);
    }

    /**
     * Partially update a center.
     *
     * @param centerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CenterDTO> partialUpdate(CenterDTO centerDTO) {
        LOG.debug("Request to partially update Center : {}", centerDTO);

        return centerRepository
            .findById(centerDTO.getId())
            .map(existingCenter -> {
                centerMapper.partialUpdate(existingCenter, centerDTO);

                return existingCenter;
            })
            .map(centerRepository::save)
            .map(centerMapper::toDto);
    }

    /**
     * Get all the centers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CenterDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Centers");
        return centerRepository.findAll(pageable).map(centerMapper::toDto);
    }

    /**
     * Get all the centers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CenterDTO> findAllWithEagerRelationships(Pageable pageable) {
        return centerRepository.findAllWithEagerRelationships(pageable).map(centerMapper::toDto);
    }

    /**
     * Get one center by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CenterDTO> findOne(Long id) {
        LOG.debug("Request to get Center : {}", id);
        return centerRepository.findOneWithEagerRelationships(id).map(centerMapper::toDto);
    }

    /**
     * Delete the center by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Center : {}", id);
        centerRepository.deleteById(id);
    }

    public boolean isCenterOpenNow(Long centerId) {
        Optional<Center> centerOpt = centerRepository.findById(centerId);
        if (centerOpt.isEmpty()) {
            throw new EntityNotFoundException("Center not found with id: " + centerId);
        }

        Center center = centerOpt.get();
        LocalTime now = LocalTime.now();

        LocalTime open = center.getOpenTime();
        LocalTime close = center.getCloseTime();

        if (open.isBefore(close)) {
            return !now.isBefore(open) && !now.isAfter(close);
        } else {
            LOG.debug("is open");
            return !now.isBefore(open) || !now.isAfter(close);
        }
    }
}
