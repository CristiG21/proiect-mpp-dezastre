package com.mpp.disaster.service.mapper;

import com.mpp.disaster.domain.CommunityMessage;
import com.mpp.disaster.domain.User;
import com.mpp.disaster.service.dto.CommunityMessageDTO;
import com.mpp.disaster.service.dto.UserDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CommunityMessage} and its DTO {@link CommunityMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommunityMessageMapper extends EntityMapper<CommunityMessageDTO, CommunityMessage> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "parent", source = "parent", qualifiedByName = "communityMessageId")
    CommunityMessageDTO toDto(CommunityMessage s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("communityMessageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommunityMessageDTO toDtoCommunityMessageId(CommunityMessage communityMessage);
}
