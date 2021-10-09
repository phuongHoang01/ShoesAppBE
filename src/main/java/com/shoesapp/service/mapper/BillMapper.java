package com.shoesapp.service.mapper;

import com.shoesapp.domain.*;
import com.shoesapp.service.dto.BillDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Bill} and its DTO {@link BillDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface BillMapper extends EntityMapper<BillDTO, Bill> {
    @Mapping(target = "products", source = "products", qualifiedByName = "nameSet")
    BillDTO toDto(Bill s);

    @Mapping(target = "removeProduct", ignore = true)
    Bill toEntity(BillDTO billDTO);
}
