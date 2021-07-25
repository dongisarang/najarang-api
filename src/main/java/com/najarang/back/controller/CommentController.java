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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final ResponseService responseService;

    @GetMapping()
    public ListResult<CommentDTO> findAllComment(final Pageable pageable, @RequestParam(required = false) Long boardId) {
        ListResult<CommentDTO> result;
        if (boardId == null)
            result = commentService.list(pageable);
        else
            result = commentService.getCommentsByBoardId(boardId, pageable);
        return result;
    }

    // @RequestBody : Json(application/json) 형태의 HTTP Body 내용을 Java Object로 변환시켜주는 어노테이션
    // - 요청받은 데이터를 변환시키는 것이기 때문에, Setter함수가 없어도 값이 매핑
    @PostMapping()
    public CommonResult save(@AuthenticationPrincipal CustomUserDetails customUserDetail, @RequestBody CommentDTO comment) {
        comment.setUser(customUserDetail.getUser());
        return commentService.save(comment);
    }

    @PutMapping()
    public CommonResult modify(@AuthenticationPrincipal CustomUserDetails customUserDetail, @RequestBody CommentDTO comment, @PathVariable long id) {
        comment.setId(id);
        comment.setUser(customUserDetail.getUser());
        return commentService.modify(comment);
    }

    @DeleteMapping(value = "/comments/{id}")
    public CommonResult delete(@PathVariable long id) {
        return commentService.delete(id);
    }
}
