package com.najarang.back.repo;

import com.najarang.back.dto.BoardDTO;
import com.najarang.back.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardJpaRepo extends JpaRepository<Board, Long>, BoardJpaRepoCustom {
}
