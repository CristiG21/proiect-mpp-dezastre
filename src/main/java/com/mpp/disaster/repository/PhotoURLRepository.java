package com.mpp.disaster.repository;

import com.mpp.disaster.domain.PhotoURL;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PhotoURL entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PhotoURLRepository extends JpaRepository<PhotoURL, Long> {
    List<PhotoURL> findAllByCenterId(Long centerId);
}
