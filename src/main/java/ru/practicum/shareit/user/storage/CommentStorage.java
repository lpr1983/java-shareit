package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Integer> {
    @Query("""
            select c from Comment c
            join fetch c.author
            where c.item.id in ?1
            order by c.created asc
            """)
    List<Comment> findAllWithAuthorByItemIds(List<Integer> ids);
}
