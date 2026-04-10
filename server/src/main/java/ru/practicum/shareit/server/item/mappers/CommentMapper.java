package ru.practicum.shareit.server.item.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.common.dto.CreateCommentDTO;
import ru.practicum.shareit.common.dto.ResponseCommentDTO;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CommentMapper {

    @Mapping(target = "authorName", source = "author.name")
    ResponseCommentDTO toResponseDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "dto.text")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "created", source = "created")
    Comment toEntity(CreateCommentDTO dto, User author, Item item, LocalDateTime created);
}