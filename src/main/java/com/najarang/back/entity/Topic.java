package com.najarang.back.entity;

import com.najarang.back.dto.TopicDTO;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@ToString
@Builder
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String name;

    public TopicDTO toDTO(){
        return new TopicDTO(this);
    }
}