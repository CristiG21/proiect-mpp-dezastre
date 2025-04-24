package com.mpp.disaster.service;

import com.mpp.disaster.domain.CenterTypeWrapper;
import com.mpp.disaster.repository.CenterTypeWrapperRepository;
import com.mpp.disaster.service.dto.CenterTypeWrapperDTO;
import com.mpp.disaster.service.mapper.CenterTypeWrapperMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mpp.disaster.domain.CenterTypeWrapper}.
 */
@Service
@Transactional
public class CenterTypeWrapperService {

    private static final Logger LOG = LoggerFactory.getLogger(CenterTypeWrapperService.class);

    private final CenterTypeWrapperRepository centerTypeWrapperRepository;

    private final CenterTypeWrapperMapper centerTypeWrapperMapper;

    public CenterTypeWrapperService(
        CenterTypeWrapperRepository centerTypeWrapperRepository,
        CenterTypeWrapperMapper centerTypeWrapperMapper
    ) {
        this.centerTypeWrapperRepository = centerTypeWrapperRepository;
        this.centerTypeWrapperMapper = centerTypeWrapperMapper;
    }

    /**
     * Save a centerTypeWrapper.
     *
     * @param centerTypeWrapperDTO the entity to save.
     * @return the persisted entity.
     */
    public CenterTypeWrapperDTO save(CenterTypeWrapperDTO centerTypeWrapperDTO) {
        LOG.debug("Request to save CenterTypeWrapper : {}", centerTypeWrapperDTO);
        CenterTypeWrapper centerTypeWrapper = centerTypeWrapperMapper.toEntity(centerTypeWrapperDTO);
        centerTypeWrapper = centerTypeWrapperRepository.save(centerTypeWrapper);
        return centerTypeWrapperMapper.toDto(centerTypeWrapper);
    }

    /**
     * Update a centerTypeWrapper.
     *
     * @param centerTypeWrapperDTO the entity to save.
     * @return the persisted entity.
     */
    public CenterTypeWrapperDTO update(CenterTypeWrapperDTO centerTypeWrapperDTO) {
        LOG.debug("Request to update CenterTypeWrapper : {}", centerTypeWrapperDTO);
        CenterTypeWrapper centerTypeWrapper = centerTypeWrapperMapper.toEntity(centerTypeWrapperDTO);
        centerTypeWrapper = centerTypeWrapperRepository.save(centerTypeWrapper);
        return centerTypeWrapperMapper.toDto(centerTypeWrapper);
    }

    /**
     * Partially update a centerTypeWrapper.
     *
     * @param centerTypeWrapperDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CenterTypeWrapperDTO> partialUpdate(CenterTypeWrapperDTO centerTypeWrapperDTO) {
        LOG.debug("Request to partially update CenterTypeWrapper : {}", centerTypeWrapperDTO);

        return centerTypeWrapperRepository
            .findById(centerTypeWrapperDTO.getId())
            .map(existingCenterTypeWrapper -> {
                centerTypeWrapperMapper.partialUpdate(existingCenterTypeWrapper, centerTypeWrapperDTO);

                return existingCenterTypeWrapper;
            })
            .map(centerTypeWrapperRepository::save)
            .map(centerTypeWrapperMapper::toDto);
    }

    /**
     * Get all the centerTypeWrappers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CenterTypeWrapperDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CenterTypeWrappers");
        return centerTypeWrapperRepository.findAll(pageable).map(centerTypeWrapperMapper::toDto);
    }

    /**
     * Get one centerTypeWrapper by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CenterTypeWrapperDTO> findOne(Long id) {
        LOG.debug("Request to get CenterTypeWrapper : {}", id);
        return centerTypeWrapperRepository.findById(id).map(centerTypeWrapperMapper::toDto);
    }

    /**
     * Delete the centerTypeWrapper by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CenterTypeWrapper : {}", id);
        centerTypeWrapperRepository.deleteById(id);
    }
}
