package com.mpp.disaster.repository;

import com.mpp.disaster.domain.CenterTypeWrapper;
import com.mpp.disaster.domain.PhotoURL;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CenterTypeWrapper entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CenterTypeWrapperRepository extends JpaRepository<CenterTypeWrapper, Long> {
    List<CenterTypeWrapper> findAllByCenterId(Long centerId);
}
