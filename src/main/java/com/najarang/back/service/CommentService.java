package com.najarang.back.service;

import com.najarang.back.dto.CommentDTO;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.model.response.ListResult;
import com.najarang.back.model.response.SingleResult;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    ListResult<CommentDTO> list(Pageable pageable);
    SingleResult<CommentDTO> save(CommentDTO comment);
    ListResult<CommentDTO> getCommentsByBoardId(long boardId, Pageable pageable);
    SingleResult<CommentDTO> modify(CommentDTO comment);
    CommonResult delete(long id);
}