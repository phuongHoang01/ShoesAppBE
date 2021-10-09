package com.shoesapp.service;

import com.shoesapp.domain.Size;
import com.shoesapp.repository.SizeRepository;
import com.shoesapp.service.dto.SizeDTO;
import com.shoesapp.service.mapper.SizeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Size}.
 */
@Service
@Transactional
public class SizeService {

    private final Logger log = LoggerFactory.getLogger(SizeService.class);

    private final SizeRepository sizeRepository;

    private final SizeMapper sizeMapper;

    public SizeService(SizeRepository sizeRepository, SizeMapper sizeMapper) {
        this.sizeRepository = sizeRepository;
        this.sizeMapper = sizeMapper;
    }

    /**
     * Save a size.
     *
     * @param sizeDTO the entity to save.
     * @return the persisted entity.
     */
    public SizeDTO save(SizeDTO sizeDTO) {
        log.debug("Request to save Size : {}", sizeDTO);
        Size size = sizeMapper.toEntity(sizeDTO);
        size = sizeRepository.save(size);
        return sizeMapper.toDto(size);
    }

    /**
     * Partially update a size.
     *
     * @param sizeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SizeDTO> partialUpdate(SizeDTO sizeDTO) {
        log.debug("Request to partially update Size : {}", sizeDTO);

        return sizeRepository
            .findById(sizeDTO.getId())
            .map(existingSize -> {
                sizeMapper.partialUpdate(existingSize, sizeDTO);

                return existingSize;
            })
            .map(sizeRepository::save)
            .map(sizeMapper::toDto);
    }

    /**
     * Get all the sizes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SizeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sizes");
        return sizeRepository.findAll(pageable).map(sizeMapper::toDto);
    }

    /**
     * Get one size by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SizeDTO> findOne(Long id) {
        log.debug("Request to get Size : {}", id);
        return sizeRepository.findById(id).map(sizeMapper::toDto);
    }

    /**
     * Delete the size by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Size : {}", id);
        sizeRepository.deleteById(id);
    }
}
