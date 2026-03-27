package ru.practicum.shareit.item.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.item.dto.CreateCommentDTO;
import ru.practicum.shareit.item.dto.ResponseCommentDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "authorName", source = "author.name")
    ResponseCommentDTO toResponseDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "dto.text")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "created", source = "created")
    Comment toEntity(CreateCommentDTO dto, User author, Item item, Instant created);
}