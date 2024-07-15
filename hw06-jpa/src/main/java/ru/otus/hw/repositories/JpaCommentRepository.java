package ru.otus.hw.repositories;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JpaCommentRepository implements CommentRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<Comment> findAllByBookId(long bookId) {
        String sql = "SELECT c FROM Comment c where c.book.id = :id";
        TypedQuery<Comment> query = em.createQuery(sql, Comment.class);
        query.setParameter("id", bookId);

        return query.getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Optional<Comment> findById(long id) {
        try {
            String sql = "select c from Comment c where c.id = :id";
            TypedQuery<Comment> query = em.createQuery(sql, Comment.class);
            query.setParameter("id", id);

            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(long id) {
        String sql = "delete from Comment c where c.id = :id";
        Query query = em.createQuery(sql);
        query.setParameter("id", id);
        query.executeUpdate();
    }
}
