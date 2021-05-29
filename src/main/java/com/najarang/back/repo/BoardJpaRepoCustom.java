package com.najarang.back.repo;

import com.najarang.back.dto.BoardDTO;
import com.najarang.back.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardJpaRepoCustom {
    Page<Board> findByTopicId(long topicId, Pageable pageable);
}
