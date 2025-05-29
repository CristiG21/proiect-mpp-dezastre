package com.mpp.disaster.service;

import com.mpp.disaster.domain.PhotoURL;
import com.mpp.disaster.service.dto.PhotoURLDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mpp.disaster.domain.PhotoURL}.
 */
public interface PhotoURLService {
    /**
     * Save a photoURL.
     *
     * @param photoURLDTO the entity to save.
     * @return the persisted entity.
     */
    PhotoURLDTO save(PhotoURLDTO photoURLDTO);

    /**
     * Updates a photoURL.
     *
     * @param photoURLDTO the entity to update.
     * @return the persisted entity.
     */
    PhotoURLDTO update(PhotoURLDTO photoURLDTO);

    /**
     * Partially updates a photoURL.
     *
     * @param photoURLDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PhotoURLDTO> partialUpdate(PhotoURLDTO photoURLDTO);

    /**
     * Get all the photoURLS.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PhotoURLDTO> findAll(Pageable pageable);

    /**
     * Get the "id" photoURL.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PhotoURLDTO> findOne(Long id);

    /**
     * Delete the "id" photoURL.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<PhotoURL> findAllByCenterId(Long centerId);
}
