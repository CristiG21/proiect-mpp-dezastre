package com.mpp.disaster.web.rest;

import com.mpp.disaster.repository.CommunityMessageRepository;
import com.mpp.disaster.service.CommunityMessageService;
import com.mpp.disaster.service.dto.CommunityMessageDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mpp.disaster.domain.CommunityMessage}.
 */
@PreAuthorize("hasRole('ROLE_USER')")
@RestController
@RequestMapping("/api/community-messages")
public class CommunityMessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(CommunityMessageResource.class);

    private static final String ENTITY_NAME = "communityMessage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommunityMessageService communityMessageService;

    private final CommunityMessageRepository communityMessageRepository;

    public CommunityMessageResource(
        CommunityMessageService communityMessageService,
        CommunityMessageRepository communityMessageRepository
    ) {
        this.communityMessageService = communityMessageService;
        this.communityMessageRepository = communityMessageRepository;
    }

    /**
     * {@code POST  /community-messages} : Create a new communityMessage.
     *
     * @param communityMessageDTO the communityMessageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new communityMessageDTO, or with status {@code 400 (Bad Request)} if the communityMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CommunityMessageDTO> createCommunityMessage(@Valid @RequestBody CommunityMessageDTO communityMessageDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CommunityMessage : {}", communityMessageDTO);
        if (communityMessageDTO.getId() != null) {
            throw new BadRequestAlertException("A new communityMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        communityMessageDTO = communityMessageService.save(communityMessageDTO);
        return ResponseEntity.created(new URI("/api/community-messages/" + communityMessageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, communityMessageDTO.getId().toString()))
            .body(communityMessageDTO);
    }

    /**
     * {@code PUT  /community-messages/:id} : Updates an existing communityMessage.
     *
     * @param id the id of the communityMessageDTO to save.
     * @param communityMessageDTO the communityMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated communityMessageDTO,
     * or with status {@code 400 (Bad Request)} if the communityMessageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the communityMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommunityMessageDTO> updateCommunityMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CommunityMessageDTO communityMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CommunityMessage : {}, {}", id, communityMessageDTO);
        if (communityMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, communityMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!communityMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        communityMessageDTO = communityMessageService.update(communityMessageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, communityMessageDTO.getId().toString()))
            .body(communityMessageDTO);
    }

    /**
     * {@code PATCH  /community-messages/:id} : Partial updates given fields of an existing communityMessage, field will ignore if it is null
     *
     * @param id the id of the communityMessageDTO to save.
     * @param communityMessageDTO the communityMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated communityMessageDTO,
     * or with status {@code 400 (Bad Request)} if the communityMessageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the communityMessageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the communityMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CommunityMessageDTO> partialUpdateCommunityMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CommunityMessageDTO communityMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CommunityMessage partially : {}, {}", id, communityMessageDTO);
        if (communityMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, communityMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!communityMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CommunityMessageDTO> result = communityMessageService.partialUpdate(communityMessageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, communityMessageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /community-messages} : get all the communityMessages.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of communityMessages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CommunityMessageDTO>> getAllCommunityMessages(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of CommunityMessages");
        Page<CommunityMessageDTO> page;
        if (eagerload) {
            page = communityMessageService.findAllWithEagerRelationships(pageable);
        } else {
            page = communityMessageService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /community-messages/:id} : get the "id" communityMessage.
     *
     * @param id the id of the communityMessageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the communityMessageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommunityMessageDTO> getCommunityMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CommunityMessage : {}", id);
        Optional<CommunityMessageDTO> communityMessageDTO = communityMessageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(communityMessageDTO);
    }

    /**
     * {@code DELETE  /community-messages/:id} : delete the "id" communityMessage.
     *
     * @param id the id of the communityMessageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunityMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CommunityMessage : {}", id);
        communityMessageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/paged")
    public ResponseEntity<?> getMessages(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "false") boolean onlyTopLevel
    ) {
        if (onlyTopLevel) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("timePosted").descending());
            Page<CommunityMessageDTO> result = communityMessageService.findTopLevelMessagesPaginated(pageable);
            return ResponseEntity.ok().body(result);
        }

        return ResponseEntity.badRequest().body("Fetching all messages is not implemented.");
    }
}
