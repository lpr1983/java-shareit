package ru.practicum.shareit.server.request.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.common.dto.ItemRequestDTO;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ItemRequestMapper {
    ItemRequestDTO toResponseDto(ItemRequest itemRequest, List<Item> items);
}