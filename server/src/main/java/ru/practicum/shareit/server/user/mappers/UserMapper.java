package ru.practicum.shareit.server.user.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.common.dto.CreateUserDTO;
import ru.practicum.shareit.common.dto.UpdateUserDTO;
import ru.practicum.shareit.common.dto.ResponseUserDTO;
import ru.practicum.shareit.server.user.model.User;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    ResponseUserDTO toResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget User user, UpdateUserDTO dto);
}