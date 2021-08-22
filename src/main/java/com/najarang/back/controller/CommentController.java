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

    // @RequestBody : HTTP Body 내용을 Java Object로 변환시켜주는 어노테이션
    // - accept-header 정보를 참조하여 Json이나 XML같은 형태의 data를 jackson등의 MessageConverter를 활용하여 Java Object로 변환
    // - JSON -> Java Object로 변환은 Jackson2HttpMessageConverter에서 해줌. -> Setter함수가 없어도 값이 매핑
    @PostMapping()
    public CommonResult save(@AuthenticationPrincipal CustomUserDetails customUserDetail, @RequestBody CommentDTO comment) {
        comment.setUser(customUserDetail.getUser());
        return commentService.save(comment);
    }

    @PutMapping(value = "/{id}")
    public CommonResult modify(@AuthenticationPrincipal CustomUserDetails customUserDetail, @RequestBody CommentDTO comment, @PathVariable long id) {
        comment.setId(id);
        comment.setUser(customUserDetail.getUser());
        return commentService.modify(comment);
    }

    @DeleteMapping(value = "/{id}")
    public CommonResult delete(@PathVariable long id) {
        return commentService.delete(id);
    }
}
