package com.najarang.back.service.impl;

import com.najarang.back.dto.CommentDTO;
import com.najarang.back.entity.Comment;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.model.response.ListResult;
import com.najarang.back.model.response.SingleResult;
import com.najarang.back.repo.CommentJpaRepo;
import com.najarang.back.service.ResponseService;
import com.najarang.back.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("commentService")
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentJpaRepo commentJpaRepo;
    private final ResponseService responseService;

    public ListResult<CommentDTO> list(Pageable pageable) {
        Page<Comment> pageComments = commentJpaRepo.findAll(pageable);
        return getCommentDTOListResult(pageable, pageComments);
    }

    public ListResult<CommentDTO> getCommentsByBoardId(long boardId, Pageable pageable) {
        Page<Comment> pageComments = commentJpaRepo.findByBoardId(boardId, pageable);
        return getCommentDTOListResult(pageable, pageComments);
    }

    private ListResult<CommentDTO> getCommentDTOListResult(Pageable pageable, Page<Comment> pageComments) {
        List<CommentDTO> commentDTOs = pageComments.getContent().stream().map((comment) -> {
            CommentDTO commentDTO = comment.toDTO();
            return commentDTO;
        }).collect(Collectors.toList());
        Page<CommentDTO> pageCommentDTOs = new PageImpl<>(commentDTOs, pageable, pageComments.getTotalElements());
        return responseService.getListResult(pageCommentDTOs);
    }

    public SingleResult<CommentDTO> save(CommentDTO comment) {
        CommentDTO insertedComment = commentJpaRepo.save(comment.toEntity()).toDTO();
        insertedComment.setUser(null);
        return responseService.getSingleResult(insertedComment);
    }

    public SingleResult<CommentDTO> modify(CommentDTO comment) {
        long commentId = comment.getId();
        Optional<Comment> newComment = commentJpaRepo.findById(commentId);
        CommentDTO commentDto = newComment.get().toDTO();
        if (comment.getContent() != null) commentDto.setContent(comment.getContent());
        CommentDTO updatedComment = commentJpaRepo.save(commentDto.toEntity()).toDTO();
        updatedComment.setUser(null);
        return responseService.getSingleResult(updatedComment);
    }

    public CommonResult delete(long id) {
        commentJpaRepo.deleteById(id);
        return responseService.getSuccessResult();
    }
}