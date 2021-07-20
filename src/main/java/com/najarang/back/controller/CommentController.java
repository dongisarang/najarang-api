package com.najarang.back.controller;

import com.najarang.back.dto.CommentDTO;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.model.response.ListResult;
import com.najarang.back.security.CustomUserDetails;
import com.najarang.back.service.CommentService;
import com.najarang.back.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final ResponseService responseService;

    @GetMapping(value = "/comments")
    public ListResult<CommentDTO> findAllComment(final Pageable pageable, @RequestParam(required = false) Long boardId) {
        ListResult<CommentDTO> result;
        if (boardId == null)
            result = commentService.list(pageable);
        else
            result = commentService.getCommentsByBoardId(boardId, pageable);
        return result;
    }

    @PostMapping(value = "/comments")
    public CommonResult save(@AuthenticationPrincipal CustomUserDetails customUserDetail, @RequestBody CommentDTO comment) {
        try {
            comment.setUser(customUserDetail.getUser());
            return commentService.save(comment);
        } catch (Exception e) {
            return responseService.getFailResult(500, e.toString());
        }
    }

    @PutMapping(value = "/comments/{id}")
    public CommonResult modify(@AuthenticationPrincipal CustomUserDetails customUserDetail, @RequestBody CommentDTO comment, @PathVariable long id) {
        try {
            comment.setId(id);
            comment.setUser(customUserDetail.getUser());

            return commentService.modify(comment);
        } catch (Exception e) {
            e.printStackTrace();
            return responseService.getFailResult(500, e.toString());
        }
    }

    @DeleteMapping(value = "/comments/{id}")
    public CommonResult delete(@PathVariable long id) {
        return commentService.delete(id);
    }
}
