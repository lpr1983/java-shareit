package ru.practicum.shareit.user.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.user.dto.CreateUserDTO;
import ru.practicum.shareit.user.dto.UpdateUserDTO;
import ru.practicum.shareit.user.dto.ResponseUserDTO;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    ResponseUserDTO toResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserDTO dto);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget User user, UpdateUserDTO dto);
}