package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Alert;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.AlertDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Alert} and its DTO {@link AlertDTO}.
 */
@Mapper(componentModel = "spring")
public interface AlertMapper extends EntityMapper<AlertDTO, Alert> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    AlertDTO toDto(Alert s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
