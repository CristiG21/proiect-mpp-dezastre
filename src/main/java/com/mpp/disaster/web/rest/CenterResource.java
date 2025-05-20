package com.mpp.disaster.web.rest;

import com.mpp.disaster.domain.CenterTypeWrapper;
import com.mpp.disaster.domain.PhotoURL;
import com.mpp.disaster.domain.enumeration.CenterType;
import com.mpp.disaster.repository.CenterRepository;
import com.mpp.disaster.repository.CenterTypeWrapperRepository;
import com.mpp.disaster.repository.PhotoURLRepository;
import com.mpp.disaster.service.CenterService;
import com.mpp.disaster.service.dto.CenterDTO;
import com.mpp.disaster.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
 * REST controller for managing {@link com.mpp.disaster.domain.Center}.
 */
@RestController
@RequestMapping("/api/centers")
public class CenterResource {

    private static final Logger LOG = LoggerFactory.getLogger(CenterResource.class);

    private static final String ENTITY_NAME = "center";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CenterService centerService;

    private final CenterRepository centerRepository;
    private final CenterTypeWrapperRepository centerTypeWrapperRepository;

    private final PhotoURLRepository photoURLRepository;

    public CenterResource(
        CenterService centerService,
        CenterRepository centerRepository,
        CenterTypeWrapperRepository centerTypeWrapperRepository,
        PhotoURLRepository photoURLRepository
    ) {
        this.centerService = centerService;
        this.centerRepository = centerRepository;
        this.centerTypeWrapperRepository = centerTypeWrapperRepository;
        this.photoURLRepository = photoURLRepository;
    }

    /**
     * {@code POST  /centers} : Create a new center.
     *
     * @param centerDTO the centerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new centerDTO, or with status {@code 400 (Bad Request)} if the center has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CenterDTO> createCenter(@RequestBody CenterDTO centerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Center : {}", centerDTO);
        if (centerDTO.getId() != null) {
            throw new BadRequestAlertException("A new center cannot already have an ID", ENTITY_NAME, "idexists");
        }
        centerDTO = centerService.save(centerDTO);
        return ResponseEntity.created(new URI("/api/centers/" + centerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, centerDTO.getId().toString()))
            .body(centerDTO);
    }

    /**
     * {@code PUT  /centers/:id} : Updates an existing center.
     *
     * @param id        the id of the centerDTO to save.
     * @param centerDTO the centerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centerDTO,
     * or with status {@code 400 (Bad Request)} if the centerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the centerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CenterDTO> updateCenter(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CenterDTO centerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Center : {}, {}", id, centerDTO);
        if (centerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!centerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        centerDTO = centerService.update(centerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, centerDTO.getId().toString()))
            .body(centerDTO);
    }

    /**
     * {@code PATCH  /centers/:id} : Partial updates given fields of an existing center, field will ignore if it is null
     *
     * @param id        the id of the centerDTO to save.
     * @param centerDTO the centerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centerDTO,
     * or with status {@code 400 (Bad Request)} if the centerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the centerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the centerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CenterDTO> partialUpdateCenter(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CenterDTO centerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Center partially : {}, {}", id, centerDTO);
        if (centerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!centerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CenterDTO> result = centerService.partialUpdate(centerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, centerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /centers} : get all the centers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of centers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CenterDTO>> getAllCenters(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Centers");
        Page<CenterDTO> page = centerService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /centers/:id} : get the "id" center.
     *
     * @param id the id of the centerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the centerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CenterDTO> getCenter(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Center : {}", id);
        Optional<CenterDTO> centerDTO = centerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(centerDTO);
    }

    /**
     * {@code DELETE  /centers/:id} : delete the "id" center.
     *
     * @param id the id of the centerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCenter(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Center : {}", id);
        centerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/{id}/photos")
    public ResponseEntity<List<PhotoURL>> getPhotosForCenter(@PathVariable Long id) {
        List<PhotoURL> photos = photoURLRepository.findAllByCenterId(id);
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/{id}/types")
    public ResponseEntity<List<CenterType>> getCenterTypes(@PathVariable Long id) {
        List<CenterTypeWrapper> centerTypeWrappers = centerTypeWrapperRepository.findAllByCenterId(id);
        List<CenterType> centerTypes = new ArrayList<>();
        for (CenterTypeWrapper centerTypeWrapper : centerTypeWrappers) {
            centerTypes.add(centerTypeWrapper.getType());
        }
        return ResponseEntity.ok(centerTypes);
    }
}
