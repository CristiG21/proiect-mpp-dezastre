package com.mpp.disaster.service.mapper;

import com.mpp.disaster.domain.OfficialMessage;
import com.mpp.disaster.domain.User;
import com.mpp.disaster.service.dto.OfficialMessageDTO;
import com.mpp.disaster.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OfficialMessage} and its DTO {@link OfficialMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface OfficialMessageMapper extends EntityMapper<OfficialMessageDTO, OfficialMessage> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    OfficialMessageDTO toDto(OfficialMessage s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
