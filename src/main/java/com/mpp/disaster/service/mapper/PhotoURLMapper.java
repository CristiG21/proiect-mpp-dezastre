package com.mpp.disaster.service.mapper;

import com.mpp.disaster.domain.Center;
import com.mpp.disaster.domain.PhotoURL;
import com.mpp.disaster.service.dto.CenterDTO;
import com.mpp.disaster.service.dto.PhotoURLDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PhotoURL} and its DTO {@link PhotoURLDTO}.
 */
@Mapper(componentModel = "spring")
public interface PhotoURLMapper extends EntityMapper<PhotoURLDTO, PhotoURL> {
    @Mapping(target = "center", source = "center", qualifiedByName = "centerId")
    PhotoURLDTO toDto(PhotoURL s);

    @Named("centerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CenterDTO toDtoCenterId(Center center);
}
