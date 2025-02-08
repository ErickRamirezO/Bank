package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Log;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.LogDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Log} and its DTO {@link LogDTO}.
 */
@Mapper(componentModel = "spring")
public interface LogMapper extends EntityMapper<LogDTO, Log> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    LogDTO toDto(Log s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
