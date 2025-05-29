package com.mpp.disaster.service.impl;

import com.mpp.disaster.domain.Center;
import com.mpp.disaster.domain.PhotoURL;
import com.mpp.disaster.repository.CenterRepository;
import com.mpp.disaster.repository.PhotoURLRepository;
import com.mpp.disaster.repository.UserRepository;
import com.mpp.disaster.service.PhotoURLService;
import com.mpp.disaster.service.dto.PhotoURLDTO;
import com.mpp.disaster.service.mapper.PhotoURLMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link com.mpp.disaster.domain.PhotoURL}.
 */
@Service
@Transactional
public class PhotoURLServiceImpl implements PhotoURLService {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoURLServiceImpl.class);

    private final PhotoURLRepository photoURLRepository;
    private final CenterRepository centerRepository;

    private final PhotoURLMapper photoURLMapper;

    public PhotoURLServiceImpl(PhotoURLRepository photoURLRepository, PhotoURLMapper photoURLMapper, CenterRepository userRepository) {
        this.photoURLRepository = photoURLRepository;
        this.photoURLMapper = photoURLMapper;
        this.centerRepository = userRepository;
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

    @Override
    public void saveFiles(Long centerId, List<MultipartFile> files) {
        Center center = centerRepository
            .findById(centerId)
            .orElseThrow(() -> new IllegalArgumentException("Center not found: " + centerId));

        String uploadFolder = System.getProperty("user.dir") + File.separator + "uploads";
        File folder = new File(uploadFolder);
        if (!folder.exists()) {
            folder.mkdirs(); // ✅ create directory if missing
        }

        for (MultipartFile file : files) {
            try {
                String uniqueFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File destination = new File(folder, uniqueFilename);
                file.transferTo(destination); // ✅ Will now succeed

                PhotoURL photo = new PhotoURL();
                photo.setUrl("/uploads/" + uniqueFilename);
                photo.setCenter(center);
                photoURLRepository.save(photo);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save file: " + file.getOriginalFilename(), e);
            }
        }
    }
}
