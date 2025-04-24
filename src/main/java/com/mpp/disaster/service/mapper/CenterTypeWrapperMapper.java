package com.mpp.disaster.service.mapper;

import com.mpp.disaster.domain.Center;
import com.mpp.disaster.domain.CenterTypeWrapper;
import com.mpp.disaster.service.dto.CenterDTO;
import com.mpp.disaster.service.dto.CenterTypeWrapperDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CenterTypeWrapper} and its DTO {@link CenterTypeWrapperDTO}.
 */
@Mapper(componentModel = "spring")
public interface CenterTypeWrapperMapper extends EntityMapper<CenterTypeWrapperDTO, CenterTypeWrapper> {
    @Mapping(target = "center", source = "center", qualifiedByName = "centerId")
    CenterTypeWrapperDTO toDto(CenterTypeWrapper s);

    @Named("centerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CenterDTO toDtoCenterId(Center center);
}
