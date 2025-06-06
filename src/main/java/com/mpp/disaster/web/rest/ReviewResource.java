package com.mpp.disaster.web.rest;

import com.mpp.disaster.repository.ReviewRepository;
import com.mpp.disaster.service.ReviewService;
import com.mpp.disaster.service.dto.ReviewDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mpp.disaster.domain.Review}.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewResource.class);

    private static final String ENTITY_NAME = "review";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReviewService reviewService;

    private final ReviewRepository reviewRepository;

    public ReviewResource(ReviewService reviewService, ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
    }

    /**
     * {@code POST  /reviews} : Create a new review.
     *
     * @param reviewDTO the reviewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reviewDTO, or with status {@code 400 (Bad Request)} if the review has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO reviewDTO) throws URISyntaxException {
        LOG.debug("REST request to save Review : {}", reviewDTO);
        if (reviewDTO.getId() != null) {
            throw new BadRequestAlertException("A new review cannot already have an ID", ENTITY_NAME, "idexists");
        }
        reviewDTO = reviewService.save(reviewDTO);
        return ResponseEntity.created(new URI("/api/reviews/" + reviewDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, reviewDTO.getId().toString()))
            .body(reviewDTO);
    }

    /**
     * {@code PUT  /reviews/:id} : Updates an existing review.
     *
     * @param id the id of the reviewDTO to save.
     * @param reviewDTO the reviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reviewDTO,
     * or with status {@code 400 (Bad Request)} if the reviewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReviewDTO reviewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Review : {}, {}", id, reviewDTO);
        if (reviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        reviewDTO = reviewService.update(reviewDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reviewDTO.getId().toString()))
            .body(reviewDTO);
    }

    /**
     * {@code PATCH  /reviews/:id} : Partial updates given fields of an existing review, field will ignore if it is null
     *
     * @param id the id of the reviewDTO to save.
     * @param reviewDTO the reviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reviewDTO,
     * or with status {@code 400 (Bad Request)} if the reviewDTO is not valid,
     * or with status {@code 404 (Not Found)} if the reviewDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the reviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReviewDTO> partialUpdateReview(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReviewDTO reviewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Review partially : {}, {}", id, reviewDTO);
        if (reviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReviewDTO> result = reviewService.partialUpdate(reviewDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reviewDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /reviews} : get all the reviews.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reviews in body.
     */
    @GetMapping("")
    public List<ReviewDTO> getAllReviews(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Reviews");
        return reviewService.findAll();
    }

    /**
     * {@code GET  /reviews/:id} : get the "id" review.
     *
     * @param id the id of the reviewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reviewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Review : {}", id);
        Optional<ReviewDTO> reviewDTO = reviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reviewDTO);
    }

    /**
     * {@code DELETE  /reviews/:id} : delete the "id" review.
     *
     * @param id the id of the reviewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Review : {}", id);
        reviewService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /reviews/by-center/:centerId} : get all reviews for a specific center.
     *
     * @param centerId the id of the center whose reviews you want to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reviews in body.
     */
    @GetMapping("/by-center/{centerId}")
    public ResponseEntity<List<ReviewDTO>> getAllReviewsByCenterId(@PathVariable Long centerId) {
        LOG.debug("REST request to get all Reviews for Center : {}", centerId);
        List<ReviewDTO> reviews = reviewService.findAllByCenterId(centerId);
        return ResponseEntity.ok(reviews);
    }
}
