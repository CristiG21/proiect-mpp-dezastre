package com.mpp.disaster.service.mapper;

import com.mpp.disaster.domain.Center;
import com.mpp.disaster.service.dto.CenterDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Center} and its DTO {@link CenterDTO}.
 */
@Mapper(componentModel = "spring")
public interface CenterMapper extends EntityMapper<CenterDTO, Center> {}
