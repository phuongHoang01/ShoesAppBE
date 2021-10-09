package com.shoesapp.repository;

import com.shoesapp.domain.Favorite;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Favorite entity.
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("select favorite from Favorite favorite where favorite.user.login = ?#{principal.username}")
    List<Favorite> findByUserIsCurrentUser();

    @Query(
        value = "select distinct favorite from Favorite favorite left join fetch favorite.products",
        countQuery = "select count(distinct favorite) from Favorite favorite"
    )
    Page<Favorite> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct favorite from Favorite favorite left join fetch favorite.products")
    List<Favorite> findAllWithEagerRelationships();

    @Query("select favorite from Favorite favorite left join fetch favorite.products where favorite.id =:id")
    Optional<Favorite> findOneWithEagerRelationships(@Param("id") Long id);
}
