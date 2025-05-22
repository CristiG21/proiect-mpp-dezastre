package com.mpp.disaster.repository;

import com.mpp.disaster.domain.CommunityMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CommunityMessage entity.
 */
@Repository
public interface CommunityMessageRepository extends JpaRepository<CommunityMessage, Long> {
    @Query("select communityMessage from CommunityMessage communityMessage where communityMessage.user.login = ?#{authentication.name}")
    List<CommunityMessage> findByUserIsCurrentUser();

    default Optional<CommunityMessage> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    Page<CommunityMessage> findAllByParentIsNull(Pageable pageable);

    default List<CommunityMessage> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CommunityMessage> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select communityMessage from CommunityMessage communityMessage left join fetch communityMessage.user",
        countQuery = "select count(communityMessage) from CommunityMessage communityMessage"
    )
    Page<CommunityMessage> findAllWithToOneRelationships(Pageable pageable);

    @Query("select communityMessage from CommunityMessage communityMessage left join fetch communityMessage.user")
    List<CommunityMessage> findAllWithToOneRelationships();

    @Query(
        "select communityMessage from CommunityMessage communityMessage left join fetch communityMessage.user where communityMessage.id =:id"
    )
    Optional<CommunityMessage> findOneWithToOneRelationships(@Param("id") Long id);

    Page<CommunityMessage> findByParentIsNullAndApprovedTrue(Pageable pageable);

    List<CommunityMessage> findByParentIdInAndApprovedTrueOrderByTimePostedDesc(List<Long> parentIds);
}
