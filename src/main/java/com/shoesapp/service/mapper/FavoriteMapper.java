package com.shoesapp.service.mapper;

import com.shoesapp.domain.*;
import com.shoesapp.service.dto.FavoriteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Favorite} and its DTO {@link FavoriteDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, ProductMapper.class })
public interface FavoriteMapper extends EntityMapper<FavoriteDTO, Favorite> {
    @Mapping(target = "user", source = "user", qualifiedByName = "login")
    @Mapping(target = "product", source = "product", qualifiedByName = "id")
    FavoriteDTO toDto(Favorite s);
}
