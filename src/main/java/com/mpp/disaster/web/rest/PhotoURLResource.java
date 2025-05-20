package com.mpp.disaster.web.rest;

import com.mpp.disaster.repository.PhotoURLRepository;
import com.mpp.disaster.service.PhotoURLService;
import com.mpp.disaster.service.dto.PhotoURLDTO;
import com.mpp.disaster.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mpp.disaster.domain.PhotoURL}.
 */
@RestController
@RequestMapping("/api/photo-urls")
public class PhotoURLResource {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoURLResource.class);

    private static final String ENTITY_NAME = "photoURL";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PhotoURLService photoURLService;

    private final PhotoURLRepository photoURLRepository;

    public PhotoURLResource(PhotoURLService photoURLService, PhotoURLRepository photoURLRepository) {
        this.photoURLService = photoURLService;
        this.photoURLRepository = photoURLRepository;
    }

    /**
     * {@code POST  /photo-urls} : Create a new photoURL.
     *
     * @param photoURLDTO the photoURLDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new photoURLDTO, or with status {@code 400 (Bad Request)} if the photoURL has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PhotoURLDTO> createPhotoURL(@RequestBody PhotoURLDTO photoURLDTO) throws URISyntaxException {
        LOG.debug("REST request to save PhotoURL : {}", photoURLDTO);
        if (photoURLDTO.getId() != null) {
            throw new BadRequestAlertException("A new photoURL cannot already have an ID", ENTITY_NAME, "idexists");
        }
        photoURLDTO = photoURLService.save(photoURLDTO);
        return ResponseEntity.created(new URI("/api/photo-urls/" + photoURLDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, photoURLDTO.getId().toString()))
            .body(photoURLDTO);
    }

    /**
     * {@code PUT  /photo-urls/:id} : Updates an existing photoURL.
     *
     * @param id the id of the photoURLDTO to save.
     * @param photoURLDTO the photoURLDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated photoURLDTO,
     * or with status {@code 400 (Bad Request)} if the photoURLDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the photoURLDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PhotoURLDTO> updatePhotoURL(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PhotoURLDTO photoURLDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PhotoURL : {}, {}", id, photoURLDTO);
        if (photoURLDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, photoURLDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!photoURLRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        photoURLDTO = photoURLService.update(photoURLDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, photoURLDTO.getId().toString()))
            .body(photoURLDTO);
    }

    /**
     * {@code PATCH  /photo-urls/:id} : Partial updates given fields of an existing photoURL, field will ignore if it is null
     *
     * @param id the id of the photoURLDTO to save.
     * @param photoURLDTO the photoURLDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated photoURLDTO,
     * or with status {@code 400 (Bad Request)} if the photoURLDTO is not valid,
     * or with status {@code 404 (Not Found)} if the photoURLDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the photoURLDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PhotoURLDTO> partialUpdatePhotoURL(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PhotoURLDTO photoURLDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PhotoURL partially : {}, {}", id, photoURLDTO);
        if (photoURLDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, photoURLDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!photoURLRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PhotoURLDTO> result = photoURLService.partialUpdate(photoURLDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, photoURLDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /photo-urls} : get all the photoURLS.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of photoURLS in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PhotoURLDTO>> getAllPhotoURLS(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of PhotoURLS");
        Page<PhotoURLDTO> page = photoURLService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /photo-urls/:id} : get the "id" photoURL.
     *
     * @param id the id of the photoURLDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the photoURLDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PhotoURLDTO> getPhotoURL(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PhotoURL : {}", id);
        Optional<PhotoURLDTO> photoURLDTO = photoURLService.findOne(id);
        return ResponseUtil.wrapOrNotFound(photoURLDTO);
    }

    /**
     * {@code DELETE  /photo-urls/:id} : delete the "id" photoURL.
     *
     * @param id the id of the photoURLDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhotoURL(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PhotoURL : {}", id);
        photoURLService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
