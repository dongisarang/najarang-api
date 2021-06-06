package com.najarang.back.repo;

import com.najarang.back.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepo extends JpaRepository<Comment, Long> {
    Page<Comment> findByBoardId(long boardId, Pageable pageable);
}
