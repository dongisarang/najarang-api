package com.najarang.back.dto;

import com.najarang.back.entity.UserTopic;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserTopicDTO {
    private Long id;
    private UserDTO user;
    private TopicDTO topic;

    public UserTopicDTO(UserTopic userTopic){
        this.id = userTopic.getId();
        this.user = userTopic.getUser().toDTO();
        this.topic = userTopic.getTopic().toDTO();
    }

    public UserTopic toEntity(){
        UserTopic userTopic = UserTopic.builder()
                .id(id)
                .user(user.toEntity())
                .topic(topic.toEntity())
                .build();
        return userTopic;
    }
}
