package com.najarang.back.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.najarang.back.entity.User;
import com.najarang.back.entity.UserTopic;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
public class UserDTO extends BaseTimeDTO {
    private Long id;
    private String nickname;
    private String email;
    private String provider;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String role;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Collection<Long> topicList;

    public UserDTO(User user){
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.provider = user.getProvider();
        this.role = user.getRole();
        this.setCreated(user.getCreated());
        this.setModified(user.getModified());
    }

    public User toEntity(){
        User user = User.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .provider(provider)
                .role(role)
                .build();
        user.setCreated(getCreated());
        user.setModified(getModified());
        return user;
    }
}
