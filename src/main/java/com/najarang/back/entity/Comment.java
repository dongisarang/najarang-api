package com.najarang.back.entity;

import com.najarang.back.dto.CommentDTO;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@ToString
@Builder
@Table(name = "comment")
public class Comment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column()
    private String content;
    @ManyToOne()
    @JoinColumn(name = "USER_ID")
    private User user;
    @Column(name = "BOARD_ID")
    private Long boardId;

    public CommentDTO toDTO(){
        return new CommentDTO(this);
    }
}