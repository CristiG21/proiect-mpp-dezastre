package com.mpp.disaster.repository;

import com.mpp.disaster.domain.Center;
import com.mpp.disaster.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Center entity.
 */
@Repository
public interface CenterRepository extends JpaRepository<Center, Long> {
    @Query("select center from Center center where center.user.login = ?#{authentication.name}")
    List<Center> findByUserIsCurrentUser();

    default Optional<Center> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Center> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Center> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select center from Center center left join fetch center.user", countQuery = "select count(center) from Center center")
    Page<Center> findAllWithToOneRelationships(Pageable pageable);

    @Query("select center from Center center left join fetch center.user")
    List<Center> findAllWithToOneRelationships();

    @Query("select center from Center center left join fetch center.user where center.id =:id")
    Optional<Center> findOneWithToOneRelationships(@Param("id") Long id);

    List<Center> findAllByUser(User user);
    List<Center> findAllByUserAndStatusTrue(User user);
}
