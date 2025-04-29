package com.mpp.disaster.service.mapper;

import com.mpp.disaster.domain.Disaster;
import com.mpp.disaster.service.dto.DisasterDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Disaster} and its DTO {@link DisasterDTO}.
 */
@Mapper(componentModel = "spring")
public interface DisasterMapper extends EntityMapper<DisasterDTO, Disaster> {}
