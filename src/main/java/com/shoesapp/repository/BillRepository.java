package com.shoesapp.repository;

import com.shoesapp.domain.Bill;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Bill entity.
 */
@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query(
        value = "select distinct bill from Bill bill left join fetch bill.products",
        countQuery = "select count(distinct bill) from Bill bill"
    )
    Page<Bill> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct bill from Bill bill left join fetch bill.products")
    List<Bill> findAllWithEagerRelationships();

    @Query("select bill from Bill bill left join fetch bill.products where bill.id =:id")
    Optional<Bill> findOneWithEagerRelationships(@Param("id") Long id);
}
