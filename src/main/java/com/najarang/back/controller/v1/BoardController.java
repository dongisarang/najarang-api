package com.najarang.back.controller.v1;

import com.najarang.back.dto.BoardDTO;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.model.response.ListResult;
import com.najarang.back.model.response.SingleResult;
import com.najarang.back.security.CustomUserDetails;
import com.najarang.back.service.BoardService;
import com.najarang.back.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final ResponseService responseService;

    @GetMapping(value = "/boards")
    public ListResult<BoardDTO> findAllBoard(final Pageable pageable, @RequestParam(required = false) Long topicId) {
        ListResult<BoardDTO> result;
        if (topicId == null)
            result = boardService.getBoards(pageable);
        else
            result = boardService.getBoardsByTopicId(topicId, pageable);
        return result;
    }

    @GetMapping(value = "/board/{id}")
    public SingleResult<BoardDTO> findBoardById(@PathVariable long id) {
        return boardService.getBoard(id);
    }

    @PostMapping(value = "/board", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public CommonResult save(@AuthenticationPrincipal CustomUserDetails customUserDetail,
                                    @ModelAttribute BoardDTO board) {
        try {
            board.setUser(customUserDetail.getUser());
            return boardService.save(board);
        } catch (Exception e) {
            return responseService.getFailResult(500, e.toString());
        }
    }

    @PutMapping(value = "/board/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public CommonResult modify(@AuthenticationPrincipal CustomUserDetails customUserDetail, @ModelAttribute BoardDTO board, @PathVariable long id) {

        try {
            board.setId(id);
            board.setUser(customUserDetail.getUser());

            return boardService.modify(board);
        } catch (Exception e) {
            return responseService.getFailResult(500, e.toString());
        }
    }

    @DeleteMapping(value = "/board/{id}")
    public CommonResult delete(@PathVariable long id) {
        return boardService.delete(id);
    }
}
