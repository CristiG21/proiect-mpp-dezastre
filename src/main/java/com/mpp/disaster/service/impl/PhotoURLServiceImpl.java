package com.mpp.disaster.service.impl;

import com.mpp.disaster.domain.PhotoURL;
import com.mpp.disaster.repository.PhotoURLRepository;
import com.mpp.disaster.service.PhotoURLService;
import com.mpp.disaster.service.dto.PhotoURLDTO;
import com.mpp.disaster.service.mapper.PhotoURLMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mpp.disaster.domain.PhotoURL}.
 */
@Service
@Transactional
public class PhotoURLServiceImpl implements PhotoURLService {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoURLServiceImpl.class);

    private final PhotoURLRepository photoURLRepository;

    private final PhotoURLMapper photoURLMapper;

    public PhotoURLServiceImpl(PhotoURLRepository photoURLRepository, PhotoURLMapper photoURLMapper) {
        this.photoURLRepository = photoURLRepository;
        this.photoURLMapper = photoURLMapper;
    }

    @Override
    public PhotoURLDTO save(PhotoURLDTO photoURLDTO) {
        LOG.debug("Request to save PhotoURL : {}", photoURLDTO);
        PhotoURL photoURL = photoURLMapper.toEntity(photoURLDTO);
        photoURL = photoURLRepository.save(photoURL);
        return photoURLMapper.toDto(photoURL);
    }

    @Override
    public PhotoURLDTO update(PhotoURLDTO photoURLDTO) {
        LOG.debug("Request to update PhotoURL : {}", photoURLDTO);
        PhotoURL photoURL = photoURLMapper.toEntity(photoURLDTO);
        photoURL = photoURLRepository.save(photoURL);
        return photoURLMapper.toDto(photoURL);
    }

    @Override
    public Optional<PhotoURLDTO> partialUpdate(PhotoURLDTO photoURLDTO) {
        LOG.debug("Request to partially update PhotoURL : {}", photoURLDTO);

        return photoURLRepository
            .findById(photoURLDTO.getId())
            .map(existingPhotoURL -> {
                photoURLMapper.partialUpdate(existingPhotoURL, photoURLDTO);

                return existingPhotoURL;
            })
            .map(photoURLRepository::save)
            .map(photoURLMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PhotoURLDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PhotoURLS");
        return photoURLRepository.findAll(pageable).map(photoURLMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PhotoURLDTO> findOne(Long id) {
        LOG.debug("Request to get PhotoURL : {}", id);
        return photoURLRepository.findById(id).map(photoURLMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PhotoURL : {}", id);
        photoURLRepository.deleteById(id);
    }

    @Override
    public List<PhotoURL> findAllByCenterId(Long centerId) {
        LOG.debug("Getting the PhotoURL for : {}", centerId);
        return photoURLRepository.findAllByCenterId(centerId);
    }
}
