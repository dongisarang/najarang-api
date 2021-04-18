package com.najarang.back.repo.impl;

import com.najarang.back.entity.Board;
import com.najarang.back.repo.BoardJpaRepoCustom;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static com.najarang.back.entity.QBoard.board;


@RequiredArgsConstructor
public class BoardJpaRepoImpl implements BoardJpaRepoCustom {

    private final JPAQueryFactory queryFactory;

    // NOTE querydsl로 만들어봄... 기본findby로 사용할예정
    @Override
    public Page<Board> findByTopicId(long topicId, Pageable pageable) {
        QueryResults<Board> result = queryFactory
                .selectFrom(board)
                .where(board.topic.id.eq(topicId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }
}
