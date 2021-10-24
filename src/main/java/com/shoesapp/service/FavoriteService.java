package com.shoesapp.service;

import com.shoesapp.domain.Favorite;
import com.shoesapp.domain.User;
import com.shoesapp.repository.FavoriteRepository;
import com.shoesapp.repository.UserRepository;
import com.shoesapp.security.SecurityUtils;
import com.shoesapp.service.dto.FavoriteDTO;
import com.shoesapp.service.mapper.FavoriteMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

/**
 * Service Implementation for managing {@link Favorite}.
 */
@Service
@Transactional
public class FavoriteService {

    private final Logger log = LoggerFactory.getLogger(FavoriteService.class);

    private final FavoriteRepository favoriteRepository;

    private final FavoriteMapper favoriteMapper;

    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, FavoriteMapper favoriteMapper, UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
        this.userRepository = userRepository;
    }

    /**
     * Save a favorite.
     *
     * @param favoriteDTO the entity to save.
     * @return the persisted entity.
     */
    public FavoriteDTO save(FavoriteDTO favoriteDTO) {
        log.debug("Request to save Favorite : {}", favoriteDTO);
        Favorite favorite = favoriteMapper.toEntity(favoriteDTO);
        favorite = favoriteRepository.save(favorite);
        return favoriteMapper.toDto(favorite);
    }

    /**
     * Partially update a favorite.
     *
     * @param favoriteDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FavoriteDTO> partialUpdate(FavoriteDTO favoriteDTO) {
        log.debug("Request to partially update Favorite : {}", favoriteDTO);

        return favoriteRepository
            .findById(favoriteDTO.getId())
            .map(existingFavorite -> {
                favoriteMapper.partialUpdate(existingFavorite, favoriteDTO);

                return existingFavorite;
            })
            .map(favoriteRepository::save)
            .map(favoriteMapper::toDto);
    }

    /**
     * Get all the favorites.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FavoriteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Favorites");
        return favoriteRepository.findAll(pageable).map(favoriteMapper::toDto);
    }

    /**
     * Get one favorite by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FavoriteDTO> findOne(Long id) {
        log.debug("Request to get Favorite : {}", id);
        return favoriteRepository.findById(id).map(favoriteMapper::toDto);
    }

    /**
     * Delete the favorite by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Favorite : {}", id);
        favoriteRepository.deleteById(id);
    }

    public void unlike(Long productId) throws AuthenticationException {
        Optional<User> loggedUser = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);

        if (!loggedUser.isPresent()) {
            throw new AuthenticationException("Chưa đăng nhập");
        }
        favoriteRepository.deleteByProductIdAndUserId(productId, loggedUser.get().getId());
    }

    public List<FavoriteDTO> getFavoritesOfLoggedUser() {
        return favoriteRepository.findByUserIsCurrentUser().stream().map(favoriteMapper::toDto).collect(Collectors.toList());
    }
}
