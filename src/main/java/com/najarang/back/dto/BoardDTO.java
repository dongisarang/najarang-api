package com.najarang.back.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.najarang.back.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO extends BaseTimeDTO {
    private Long id;
    private String title;
    private String content;
    private User user;
    private Topic topic;
    private Long likeCount;
    private Long hitCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long topicId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Collection<Image> images;
    private Collection<String> imageUrls = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MultipartFile[] files;

    @Builder
    public BoardDTO(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.user = board.getUser();
        this.topic = board.getTopic();
        this.likeCount = board.getLikeCount();
        this.hitCount = board.getHitCount();
        this.images = board.getImage();
        setCreated(board.getCreated());
        setModified(board.getModified());
    }

    public Board toEntity(){
        Board board = Board.builder()
                .id(id)
                .title(title)
                .content(content)
                .user(user)
                .topic(topic)
                .likeCount(likeCount)
                .hitCount(hitCount)
                .image(images)
                .build();
        board.setCreated(getCreated());
        board.setModified(getModified());
        return board;
    }
}
