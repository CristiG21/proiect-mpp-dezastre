package com.mpp.disaster.web.rest;

import com.mpp.disaster.repository.DisasterRepository;
import com.mpp.disaster.service.DisasterService;
import com.mpp.disaster.service.dto.DisasterDTO;
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
 * REST controller for managing {@link com.mpp.disaster.domain.Disaster}.
 */
@RestController
@RequestMapping("/api/disasters")
public class DisasterResource {

    private static final Logger LOG = LoggerFactory.getLogger(DisasterResource.class);

    private static final String ENTITY_NAME = "disaster";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DisasterService disasterService;

    private final DisasterRepository disasterRepository;

    public DisasterResource(DisasterService disasterService, DisasterRepository disasterRepository) {
        this.disasterService = disasterService;
        this.disasterRepository = disasterRepository;
    }

    /**
     * {@code POST  /disasters} : Create a new disaster.
     *
     * @param disasterDTO the disasterDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new disasterDTO, or with status {@code 400 (Bad Request)} if the disaster has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DisasterDTO> createDisaster(@RequestBody DisasterDTO disasterDTO) throws URISyntaxException {
        LOG.debug("REST request to save Disaster : {}", disasterDTO);
        if (disasterDTO.getId() != null) {
            throw new BadRequestAlertException("A new disaster cannot already have an ID", ENTITY_NAME, "idexists");
        }
        disasterDTO = disasterService.save(disasterDTO);
        return ResponseEntity.created(new URI("/api/disasters/" + disasterDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, disasterDTO.getId().toString()))
            .body(disasterDTO);
    }

    /**
     * {@code PUT  /disasters/:id} : Updates an existing disaster.
     *
     * @param id the id of the disasterDTO to save.
     * @param disasterDTO the disasterDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated disasterDTO,
     * or with status {@code 400 (Bad Request)} if the disasterDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the disasterDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DisasterDTO> updateDisaster(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DisasterDTO disasterDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Disaster : {}, {}", id, disasterDTO);
        if (disasterDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, disasterDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!disasterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        disasterDTO = disasterService.update(disasterDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, disasterDTO.getId().toString()))
            .body(disasterDTO);
    }

    /**
     * {@code PATCH  /disasters/:id} : Partial updates given fields of an existing disaster, field will ignore if it is null
     *
     * @param id the id of the disasterDTO to save.
     * @param disasterDTO the disasterDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated disasterDTO,
     * or with status {@code 400 (Bad Request)} if the disasterDTO is not valid,
     * or with status {@code 404 (Not Found)} if the disasterDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the disasterDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DisasterDTO> partialUpdateDisaster(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DisasterDTO disasterDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Disaster partially : {}, {}", id, disasterDTO);
        if (disasterDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, disasterDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!disasterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DisasterDTO> result = disasterService.partialUpdate(disasterDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, disasterDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /disasters} : get all the disasters.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of disasters in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DisasterDTO>> getAllDisasters(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Disasters");
        Page<DisasterDTO> page = disasterService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /disasters/:id} : get the "id" disaster.
     *
     * @param id the id of the disasterDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the disasterDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DisasterDTO> getDisaster(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Disaster : {}", id);
        Optional<DisasterDTO> disasterDTO = disasterService.findOne(id);
        return ResponseUtil.wrapOrNotFound(disasterDTO);
    }

    /**
     * {@code DELETE  /disasters/:id} : delete the "id" disaster.
     *
     * @param id the id of the disasterDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisaster(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Disaster : {}", id);
        disasterService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
