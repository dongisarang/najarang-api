package com.najarang.back.controller.v1;

import com.najarang.back.dto.BoardDTO;
import com.najarang.back.entity.Board;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.model.response.ListResult;
import com.najarang.back.model.response.SingleResult;
import com.najarang.back.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class BoardController {

    private final BoardService boardService;

    @GetMapping(value = "/boards")
    public ListResult<Board> findAllBoard(final Pageable pageable) {
        return boardService.list(pageable);
    }

    @PostMapping(value = "/board")
    public SingleResult<Board> save(@RequestBody BoardDTO board) {
        return boardService.save(board);
    }

    @PutMapping(value = "/board")
    public SingleResult<Board> modify(@RequestBody BoardDTO board) {
        return boardService.modify(board);
    }

    @DeleteMapping(value = "/board/{id}")
    public CommonResult delete(@PathVariable long id) {
        return boardService.delete(id);
    }
}
