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
@Table(name = "board_image")
public class Image extends BaseTime {

    @Id // primaryKey임
    @GeneratedValue(strategy = GenerationType.IDENTITY) // pk 필드를 auto_increment로 설정
    private Long id;

    @Column(name = "BOARD_ID")
    private Long boardId;
    @Column(name = "FILE_NAME")
    private String fileName;

    public ImageDTO toDTO(){
        return new ImageDTO(this);
    }
}