package com.mpp.disaster.web.rest;

import com.mpp.disaster.repository.OfficialMessageRepository;
import com.mpp.disaster.service.OfficialMessageService;
import com.mpp.disaster.service.dto.OfficialMessageDTO;
import com.mpp.disaster.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.mpp.disaster.domain.OfficialMessage}.
 */
@RestController
@RequestMapping("/api/official-messages")
public class OfficialMessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(OfficialMessageResource.class);

    private static final String ENTITY_NAME = "officialMessage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OfficialMessageService officialMessageService;

    private final OfficialMessageRepository officialMessageRepository;

    public OfficialMessageResource(OfficialMessageService officialMessageService, OfficialMessageRepository officialMessageRepository) {
        this.officialMessageService = officialMessageService;
        this.officialMessageRepository = officialMessageRepository;
    }

    /**
     * {@code POST  /official-messages} : Create a new officialMessage.
     *
     * @param officialMessageDTO the officialMessageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new officialMessageDTO, or with status {@code 400 (Bad Request)} if the officialMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OfficialMessageDTO> createOfficialMessage(@Valid @RequestBody OfficialMessageDTO officialMessageDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save OfficialMessage : {}", officialMessageDTO);
        if (officialMessageDTO.getId() != null) {
            throw new BadRequestAlertException("A new officialMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        officialMessageDTO = officialMessageService.save(officialMessageDTO);
        return ResponseEntity.created(new URI("/api/official-messages/" + officialMessageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, officialMessageDTO.getId().toString()))
            .body(officialMessageDTO);
    }

    /**
     * {@code PUT  /official-messages/:id} : Updates an existing officialMessage.
     *
     * @param id the id of the officialMessageDTO to save.
     * @param officialMessageDTO the officialMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated officialMessageDTO,
     * or with status {@code 400 (Bad Request)} if the officialMessageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the officialMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OfficialMessageDTO> updateOfficialMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OfficialMessageDTO officialMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OfficialMessage : {}, {}", id, officialMessageDTO);
        if (officialMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, officialMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!officialMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        officialMessageDTO = officialMessageService.update(officialMessageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, officialMessageDTO.getId().toString()))
            .body(officialMessageDTO);
    }

    /**
     * {@code PATCH  /official-messages/:id} : Partial updates given fields of an existing officialMessage, field will ignore if it is null
     *
     * @param id the id of the officialMessageDTO to save.
     * @param officialMessageDTO the officialMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated officialMessageDTO,
     * or with status {@code 400 (Bad Request)} if the officialMessageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the officialMessageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the officialMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OfficialMessageDTO> partialUpdateOfficialMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OfficialMessageDTO officialMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OfficialMessage partially : {}, {}", id, officialMessageDTO);
        if (officialMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, officialMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!officialMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OfficialMessageDTO> result = officialMessageService.partialUpdate(officialMessageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, officialMessageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /official-messages} : get all the officialMessages.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of officialMessages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OfficialMessageDTO>> getAllOfficialMessages(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of OfficialMessages");
        Page<OfficialMessageDTO> page;
        if (eagerload) {
            page = officialMessageService.findAllWithEagerRelationships(pageable);
        } else {
            page = officialMessageService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /official-messages/:id} : get the "id" officialMessage.
     *
     * @param id the id of the officialMessageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the officialMessageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OfficialMessageDTO> getOfficialMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OfficialMessage : {}", id);
        Optional<OfficialMessageDTO> officialMessageDTO = officialMessageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(officialMessageDTO);
    }

    /**
     * {@code DELETE  /official-messages/:id} : delete the "id" officialMessage.
     *
     * @param id the id of the officialMessageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOfficialMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OfficialMessage : {}", id);
        officialMessageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
