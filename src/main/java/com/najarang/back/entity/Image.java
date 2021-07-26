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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "file_name")
    private String fileName;

    public ImageDTO toDTO(){
        return new ImageDTO(this);
    }
}