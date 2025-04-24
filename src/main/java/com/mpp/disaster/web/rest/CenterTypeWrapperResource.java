package com.mpp.disaster.web.rest;

import com.mpp.disaster.repository.CenterTypeWrapperRepository;
import com.mpp.disaster.service.CenterTypeWrapperService;
import com.mpp.disaster.service.dto.CenterTypeWrapperDTO;
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
 * REST controller for managing {@link com.mpp.disaster.domain.CenterTypeWrapper}.
 */
@RestController
@RequestMapping("/api/center-type-wrappers")
public class CenterTypeWrapperResource {

    private static final Logger LOG = LoggerFactory.getLogger(CenterTypeWrapperResource.class);

    private static final String ENTITY_NAME = "centerTypeWrapper";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CenterTypeWrapperService centerTypeWrapperService;

    private final CenterTypeWrapperRepository centerTypeWrapperRepository;

    public CenterTypeWrapperResource(
        CenterTypeWrapperService centerTypeWrapperService,
        CenterTypeWrapperRepository centerTypeWrapperRepository
    ) {
        this.centerTypeWrapperService = centerTypeWrapperService;
        this.centerTypeWrapperRepository = centerTypeWrapperRepository;
    }

    /**
     * {@code POST  /center-type-wrappers} : Create a new centerTypeWrapper.
     *
     * @param centerTypeWrapperDTO the centerTypeWrapperDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new centerTypeWrapperDTO, or with status {@code 400 (Bad Request)} if the centerTypeWrapper has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CenterTypeWrapperDTO> createCenterTypeWrapper(@RequestBody CenterTypeWrapperDTO centerTypeWrapperDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CenterTypeWrapper : {}", centerTypeWrapperDTO);
        if (centerTypeWrapperDTO.getId() != null) {
            throw new BadRequestAlertException("A new centerTypeWrapper cannot already have an ID", ENTITY_NAME, "idexists");
        }
        centerTypeWrapperDTO = centerTypeWrapperService.save(centerTypeWrapperDTO);
        return ResponseEntity.created(new URI("/api/center-type-wrappers/" + centerTypeWrapperDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, centerTypeWrapperDTO.getId().toString()))
            .body(centerTypeWrapperDTO);
    }

    /**
     * {@code PUT  /center-type-wrappers/:id} : Updates an existing centerTypeWrapper.
     *
     * @param id the id of the centerTypeWrapperDTO to save.
     * @param centerTypeWrapperDTO the centerTypeWrapperDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centerTypeWrapperDTO,
     * or with status {@code 400 (Bad Request)} if the centerTypeWrapperDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the centerTypeWrapperDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CenterTypeWrapperDTO> updateCenterTypeWrapper(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CenterTypeWrapperDTO centerTypeWrapperDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CenterTypeWrapper : {}, {}", id, centerTypeWrapperDTO);
        if (centerTypeWrapperDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centerTypeWrapperDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!centerTypeWrapperRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        centerTypeWrapperDTO = centerTypeWrapperService.update(centerTypeWrapperDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, centerTypeWrapperDTO.getId().toString()))
            .body(centerTypeWrapperDTO);
    }

    /**
     * {@code PATCH  /center-type-wrappers/:id} : Partial updates given fields of an existing centerTypeWrapper, field will ignore if it is null
     *
     * @param id the id of the centerTypeWrapperDTO to save.
     * @param centerTypeWrapperDTO the centerTypeWrapperDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centerTypeWrapperDTO,
     * or with status {@code 400 (Bad Request)} if the centerTypeWrapperDTO is not valid,
     * or with status {@code 404 (Not Found)} if the centerTypeWrapperDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the centerTypeWrapperDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CenterTypeWrapperDTO> partialUpdateCenterTypeWrapper(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CenterTypeWrapperDTO centerTypeWrapperDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CenterTypeWrapper partially : {}, {}", id, centerTypeWrapperDTO);
        if (centerTypeWrapperDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centerTypeWrapperDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!centerTypeWrapperRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CenterTypeWrapperDTO> result = centerTypeWrapperService.partialUpdate(centerTypeWrapperDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, centerTypeWrapperDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /center-type-wrappers} : get all the centerTypeWrappers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of centerTypeWrappers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CenterTypeWrapperDTO>> getAllCenterTypeWrappers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of CenterTypeWrappers");
        Page<CenterTypeWrapperDTO> page = centerTypeWrapperService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /center-type-wrappers/:id} : get the "id" centerTypeWrapper.
     *
     * @param id the id of the centerTypeWrapperDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the centerTypeWrapperDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CenterTypeWrapperDTO> getCenterTypeWrapper(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CenterTypeWrapper : {}", id);
        Optional<CenterTypeWrapperDTO> centerTypeWrapperDTO = centerTypeWrapperService.findOne(id);
        return ResponseUtil.wrapOrNotFound(centerTypeWrapperDTO);
    }

    /**
     * {@code DELETE  /center-type-wrappers/:id} : delete the "id" centerTypeWrapper.
     *
     * @param id the id of the centerTypeWrapperDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCenterTypeWrapper(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CenterTypeWrapper : {}", id);
        centerTypeWrapperService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
