package com.najarang.back.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.najarang.back.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDTO extends BaseTimeDTO{
    private Long id;
    private String content;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long boardId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDTO user;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BoardDTO board;

    public CommentDTO(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.user = comment.getUser().toDTO();
        this.boardId = comment.getBoardId();
        setCreated(comment.getCreated());
        setModified(comment.getModified());
    }

    public Comment toEntity(){
        Comment comment = Comment.builder()
                .id(id)
                .content(content)
                .user(user.toEntity())
                .boardId(boardId)
                .build();
        comment.setCreated(comment.getCreated());
        comment.setModified(comment.getModified());
        return comment;
    }
}
