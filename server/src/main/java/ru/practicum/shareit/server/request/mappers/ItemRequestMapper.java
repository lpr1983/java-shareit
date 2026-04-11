package ru.practicum.shareit.server.request.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.common.dto.CreateItemRequestDTO;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.common.dto.ItemRequestDTO;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ItemRequestMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "created", source = "created")
    ItemRequest toEntity(CreateItemRequestDTO dto, User user, LocalDateTime created);

    ItemRequestDTO toResponseDto(ItemRequest itemRequest, List<Item> items);
}