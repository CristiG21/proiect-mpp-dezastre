package com.mpp.disaster.service.mapper;

import com.mpp.disaster.domain.Center;
import com.mpp.disaster.domain.Review;
import com.mpp.disaster.domain.User;
import com.mpp.disaster.service.dto.CenterDTO;
import com.mpp.disaster.service.dto.ReviewDTO;
import com.mpp.disaster.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Review} and its DTO {@link ReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper extends EntityMapper<ReviewDTO, Review> {
    @Mapping(target = "center", source = "center", qualifiedByName = "centerId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    ReviewDTO toDto(Review s);

    @Named("centerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CenterDTO toDtoCenterId(Center center);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
