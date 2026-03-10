package ru.practicum.shareit.user.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.user.dto.CreateUserDTO;
import ru.practicum.shareit.user.dto.UpdateUserDTO;
import ru.practicum.shareit.user.dto.ResponseUserDTO;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    ResponseUserDTO toResponseDto(User user);

    User toEntity(CreateUserDTO dto);

    void update(@MappingTarget User user, UpdateUserDTO dto);
}