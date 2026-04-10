package ru.practicum.shareit.server.item.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.common.dto.CreateItemDTO;
import ru.practicum.shareit.common.dto.ResponseItemDTO;
import ru.practicum.shareit.common.dto.UpdateItemDTO;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.model.ItemRequest;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ItemMapper {

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ResponseItemDTO toResponseDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "description", source = "dto.description")
    Item toEntity(CreateItemDTO dto, ItemRequest itemRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "itemRequest", ignore = true)
    void update(@MappingTarget Item item, UpdateItemDTO dto);
}