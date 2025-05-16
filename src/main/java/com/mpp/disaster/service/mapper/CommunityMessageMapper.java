package com.mpp.disaster.service.mapper;

import com.mpp.disaster.domain.CommunityMessage;
import com.mpp.disaster.domain.User;
import com.mpp.disaster.service.dto.CommunityMessageDTO;
import com.mpp.disaster.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CommunityMessage} and its DTO {@link CommunityMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommunityMessageMapper extends EntityMapper<CommunityMessageDTO, CommunityMessage> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    CommunityMessageDTO toDto(CommunityMessage s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
