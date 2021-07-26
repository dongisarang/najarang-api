package com.najarang.back.entity;

import com.najarang.back.dto.UserTopicDTO;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
@DynamicUpdate
@Table(name = "user_topic")
public class UserTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    public UserTopicDTO toDTO(){
        return new UserTopicDTO(this);
    }

    public void setBasicInfo(User user, Topic topic) {
        this.user = user;
        this.topic = topic;
    }
}