package com.shoesapp.service;

import com.shoesapp.domain.*; // for static metamodels
import com.shoesapp.domain.Size;
import com.shoesapp.repository.SizeRepository;
import com.shoesapp.service.criteria.SizeCriteria;
import com.shoesapp.service.dto.SizeDTO;
import com.shoesapp.service.mapper.SizeMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Size} entities in the database.
 * The main input is a {@link SizeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SizeDTO} or a {@link Page} of {@link SizeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SizeQueryService extends QueryService<Size> {

    private final Logger log = LoggerFactory.getLogger(SizeQueryService.class);

    private final SizeRepository sizeRepository;

    private final SizeMapper sizeMapper;

    public SizeQueryService(SizeRepository sizeRepository, SizeMapper sizeMapper) {
        this.sizeRepository = sizeRepository;
        this.sizeMapper = sizeMapper;
    }

    /**
     * Return a {@link List} of {@link SizeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SizeDTO> findByCriteria(SizeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Size> specification = createSpecification(criteria);
        return sizeMapper.toDto(sizeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SizeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SizeDTO> findByCriteria(SizeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Size> specification = createSpecification(criteria);
        return sizeRepository.findAll(specification, page).map(sizeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SizeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Size> specification = createSpecification(criteria);
        return sizeRepository.count(specification);
    }

    /**
     * Function to convert {@link SizeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Size> createSpecification(SizeCriteria criteria) {
        Specification<Size> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Size_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Size_.name));
            }
        }
        return specification;
    }
}
