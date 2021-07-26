package com.najarang.back.controller;

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

/*
* 게시물 사용을 위한 Controller
*
* @Slf4j : 로그를 남기기위해 추가한 Lombok 어노테이션 중 하나
* 
* @RestController : @Controller에 @ResponseBody가 결합된 어노테이션
* - @Controller와 달리 @RestController는 컨트롤러 클래스의 각 메서드마다 @ResponseBody를 추가할 필요가 없어짐
* - @Controller : 해당 클래스가 Controller 임을 나타내기 위한 어노테이션
* - @ResponseBody : 자바객체를 HTTP요청의 바디내용으로 매핑하여 클라이언트로 전송
*
* @RequestMapping : 요청에 대해 어떤 Controller, 어떤 메소드가 처리할지를 매핑하기 위한 어노테이션
* */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;
    private final ResponseService responseService;

    // @RequestParam : Controller 메소드의 파라미터와 웹요청 파라미터를 매핑하기 위한 어노테이션
    @GetMapping()
    public ListResult<BoardDTO> findAllBoard(final Pageable pageable, @RequestParam(required = false) Long topicId) {
        ListResult<BoardDTO> result;
        if (topicId == null)
            result = boardService.getBoards(pageable);
        else
            result = boardService.getBoardsByTopicId(topicId, pageable);
        return result;
    }

    // @PathVariable : @RequestMapping의 URL 정의 부분과 Method 내의 Parameter 부분에 정의를 하여 사용가능하도록 해주는 어노테이션
    @GetMapping(value = "/{id}")
    public SingleResult<BoardDTO> findBoardById(@PathVariable long id) {
        return boardService.getBoard(id);
    }

    // @AuthenticationPrincipal : 현재 인증된 세션유저를 가져오기 위해 UserDetails 인터페이스를 구현한 유저 객체를 주입할 때 사용
    // @ModelAttribute : Controller 메소드의 파라미터나 리턴값을 Model 객체와 바인딩하기 위한 어노테이션
    // - 클라이언트가 전송하는 multipart/form-data 형태의 HTTP Body 내용과 HTTP 파라미터들을 Setter를 통해 1대1로 객체에 바인딩하기 위해 사용
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public CommonResult save(@AuthenticationPrincipal CustomUserDetails customUserDetail,
                                    @ModelAttribute BoardDTO board) {
        board.setUser(customUserDetail.getUser());
        return boardService.save(board);
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public CommonResult modify(@AuthenticationPrincipal CustomUserDetails customUserDetail, @ModelAttribute BoardDTO board, @PathVariable long id) {
        board.setId(id);
        board.setUser(customUserDetail.getUser());
        return boardService.modify(board);
    }

    @DeleteMapping(value = "/{id}")
    public CommonResult delete(@PathVariable long id) throws Exception {
        return boardService.delete(id);
    }
}
