package com.shoesapp.service.mapper;

import com.shoesapp.domain.*;
import com.shoesapp.service.dto.ProductDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring", uses = { CategoryMapper.class, SizeMapper.class })
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "category", source = "category", qualifiedByName = "name")
    @Mapping(target = "sizes", source = "sizes", qualifiedByName = "nameSet")
    ProductDTO toDto(Product s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoId(Product product);

    @Mapping(target = "removeSize", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @Named("nameSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Set<ProductDTO> toDtoNameSet(Set<Product> product);
}
