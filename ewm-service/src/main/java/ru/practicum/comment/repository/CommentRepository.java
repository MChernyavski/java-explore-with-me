package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentCount;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findByIdAndAuthorId(Long commentId, Long userId);

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

    List<Comment> findAllByAuthorId(Long userId, Pageable pageable);

    @Query("select new ru.practicum.comment.model.CommentCount(c.event.id, COUNT(c.id)) " +
            "from Comment c " +
            "WHERE (c.event.id IN :ids) " +
            "GROUP BY c.event.id")
    List<CommentCount> getCommentCount(List<Long> ids);
}
