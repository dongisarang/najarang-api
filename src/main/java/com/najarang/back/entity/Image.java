package com.najarang.back.entity;

import com.najarang.back.dto.ImageDTO;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@DynamicInsert
@Getter
@ToString
@Builder
@Table(name = "image")
public class Image extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // board:image = 1:N을 표현하기 위해 읽기전용으로 만듬
    @ManyToOne
    @JoinColumn(name = "board_id", insertable = false, updatable = false)
    private Board board;

    @Column(name = "file_name")
    private String fileName;

    public ImageDTO toDTO(){
        return new ImageDTO(this);
    }
}