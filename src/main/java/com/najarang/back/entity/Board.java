package com.najarang.back.entity;

import com.najarang.back.dto.BoardDTO;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@DynamicInsert
@Getter
@ToString
@Builder
@Table(name = "board")
public class Board extends BaseTime {

    @Id // primaryKey임
    @GeneratedValue(strategy = GenerationType.IDENTITY) //  pk 필드를 auto_increment로 설정
    private Long id;

    @Column()
    private String title;
    @Column()
    private String content;
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    @ManyToOne
    @JoinColumn(name = "TOPIC_ID")
    private Topic topic;
    // @ColumnDefault("0") ddl로 default 설정
    @Column(name = "LIKE_COUNT")
    private Long likeCount;
    @Column(name = "HIT_COUNT")
    private Long hitCount;
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="board_id")
    private Collection<Image> image;

    @PrePersist
    public void prePersist() {
        this.hitCount = this.hitCount == null ? 0 : this.hitCount;
        this.likeCount = this.likeCount == null ? 0 : this.likeCount;
    }

    public BoardDTO toDTO(){
        return new BoardDTO(this);
    }
}