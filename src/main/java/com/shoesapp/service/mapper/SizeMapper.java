package com.shoesapp.service.mapper;

import com.shoesapp.domain.*;
import com.shoesapp.service.dto.SizeDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Size} and its DTO {@link SizeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SizeMapper extends EntityMapper<SizeDTO, Size> {
    @Named("nameSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Set<SizeDTO> toDtoNameSet(Set<Size> size);
}
