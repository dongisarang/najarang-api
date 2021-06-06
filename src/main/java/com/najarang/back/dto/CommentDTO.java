package com.najarang.back.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.najarang.back.entity.Board;
import com.najarang.back.entity.Comment;
import com.najarang.back.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long boardId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private User user;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Board board;

    public CommentDTO(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.user = comment.getUser();
        this.boardId = comment.getBoardId();
        comment.setCreated(comment.getCreated());
        comment.setModified(comment.getModified());
    }

    public Comment toEntity(){
        Comment comment = Comment.builder()
                .id(id)
                .content(content)
                .user(user)
                .boardId(boardId)
                .build();
        comment.setCreated(comment.getCreated());
        comment.setModified(comment.getModified());
        return comment;
    }
}
