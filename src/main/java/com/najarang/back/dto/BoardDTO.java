package com.najarang.back.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.najarang.back.entity.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;

/*
* @NoArgsConstructor : 파라미터가 없는 기본 생성자를 생성
*
* @JsonInclude : 값 존재 유무에 따라 Serialize 시 동작을 지정 (default는 ALWAYS)
* - ALWAYS : 속성의 값에 의존하지 말고 항상 포함
* - NOT_EMPTY : null 또는 값이 빈 경우가 아니면 포함
* - NOT_NULL : null 이 아니면 포함
* - NOT_DEFAULT : bean의 기본생성자로 정의된 필드값과 다르게 변경된 필드만 포함
* */
@Data
@NoArgsConstructor
public class BoardDTO extends BaseTimeDTO {
    private Long id;
    private String title;
    private String content;
    private UserDTO user;
    private TopicDTO topic;
    private Long hitCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long topicId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Collection<ImageDTO> images;
    private Collection<String> imageUrls = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MultipartFile[] files;

    public BoardDTO(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.user = board.getUser().toDTO();
        this.topic = board.getTopic().toDTO();
        this.hitCount = board.getHitCount();
        setCreated(board.getCreated());
        setModified(board.getModified());
    }

    public Board toEntity(){
        Board board = Board.builder()
                .id(id)
                .title(title)
                .content(content)
                .user(user.toEntity())
                .topic(topic.toEntity())
                .hitCount(hitCount)
                .build();
        board.setCreated(getCreated());
        board.setModified(getModified());
        return board;
    }
}
