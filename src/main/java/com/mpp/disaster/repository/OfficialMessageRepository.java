package com.mpp.disaster.repository;

import com.mpp.disaster.domain.OfficialMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OfficialMessage entity.
 */
@Repository
public interface OfficialMessageRepository extends JpaRepository<OfficialMessage, Long> {
    @Query("select officialMessage from OfficialMessage officialMessage where officialMessage.user.login = ?#{authentication.name}")
    List<OfficialMessage> findByUserIsCurrentUser();

    default Optional<OfficialMessage> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<OfficialMessage> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<OfficialMessage> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select officialMessage from OfficialMessage officialMessage left join fetch officialMessage.user",
        countQuery = "select count(officialMessage) from OfficialMessage officialMessage"
    )
    Page<OfficialMessage> findAllWithToOneRelationships(Pageable pageable);

    @Query("select officialMessage from OfficialMessage officialMessage left join fetch officialMessage.user")
    List<OfficialMessage> findAllWithToOneRelationships();

    @Query("select officialMessage from OfficialMessage officialMessage left join fetch officialMessage.user where officialMessage.id =:id")
    Optional<OfficialMessage> findOneWithToOneRelationships(@Param("id") Long id);
}
