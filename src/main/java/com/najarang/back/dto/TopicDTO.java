package com.najarang.back.dto;

import com.najarang.back.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TopicDTO{
    private Long id;
    private String name;

    public TopicDTO(Topic topic){
        this.id = topic.getId();
        this.name = topic.getName();
    }

    public Topic toEntity(){
        return Topic.builder()
                .id(id)
                .name(name)
                .build();
    }
}
