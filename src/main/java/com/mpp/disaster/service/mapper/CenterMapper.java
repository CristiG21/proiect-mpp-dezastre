package com.mpp.disaster.service.mapper;

import com.mpp.disaster.domain.Center;
import com.mpp.disaster.domain.User;
import com.mpp.disaster.service.dto.CenterDTO;
import com.mpp.disaster.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Center} and its DTO {@link CenterDTO}.
 */
@Mapper(componentModel = "spring")
public interface CenterMapper extends EntityMapper<CenterDTO, Center> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    CenterDTO toDto(Center s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
