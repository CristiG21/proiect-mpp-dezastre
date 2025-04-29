package com.mpp.disaster.service;

import com.mpp.disaster.domain.Disaster;
import com.mpp.disaster.repository.DisasterRepository;
import com.mpp.disaster.service.dto.DisasterDTO;
import com.mpp.disaster.service.mapper.DisasterMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mpp.disaster.domain.Disaster}.
 */
@Service
@Transactional
public class DisasterService {

    private static final Logger LOG = LoggerFactory.getLogger(DisasterService.class);

    private final DisasterRepository disasterRepository;

    private final DisasterMapper disasterMapper;

    public DisasterService(DisasterRepository disasterRepository, DisasterMapper disasterMapper) {
        this.disasterRepository = disasterRepository;
        this.disasterMapper = disasterMapper;
    }

    /**
     * Save a disaster.
     *
     * @param disasterDTO the entity to save.
     * @return the persisted entity.
     */
    public DisasterDTO save(DisasterDTO disasterDTO) {
        LOG.debug("Request to save Disaster : {}", disasterDTO);
        Disaster disaster = disasterMapper.toEntity(disasterDTO);
        disaster = disasterRepository.save(disaster);
        return disasterMapper.toDto(disaster);
    }

    /**
     * Update a disaster.
     *
     * @param disasterDTO the entity to save.
     * @return the persisted entity.
     */
    public DisasterDTO update(DisasterDTO disasterDTO) {
        LOG.debug("Request to update Disaster : {}", disasterDTO);
        Disaster disaster = disasterMapper.toEntity(disasterDTO);
        disaster = disasterRepository.save(disaster);
        return disasterMapper.toDto(disaster);
    }

    /**
     * Partially update a disaster.
     *
     * @param disasterDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DisasterDTO> partialUpdate(DisasterDTO disasterDTO) {
        LOG.debug("Request to partially update Disaster : {}", disasterDTO);

        return disasterRepository
            .findById(disasterDTO.getId())
            .map(existingDisaster -> {
                disasterMapper.partialUpdate(existingDisaster, disasterDTO);

                return existingDisaster;
            })
            .map(disasterRepository::save)
            .map(disasterMapper::toDto);
    }

    /**
     * Get all the disasters.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DisasterDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Disasters");
        return disasterRepository.findAll(pageable).map(disasterMapper::toDto);
    }

    /**
     * Get one disaster by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DisasterDTO> findOne(Long id) {
        LOG.debug("Request to get Disaster : {}", id);
        return disasterRepository.findById(id).map(disasterMapper::toDto);
    }

    /**
     * Delete the disaster by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Disaster : {}", id);
        disasterRepository.deleteById(id);
    }
}
