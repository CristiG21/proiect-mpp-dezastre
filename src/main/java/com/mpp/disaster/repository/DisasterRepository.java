package com.mpp.disaster.repository;

import com.mpp.disaster.domain.Disaster;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Disaster entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DisasterRepository extends JpaRepository<Disaster, Long> {}
