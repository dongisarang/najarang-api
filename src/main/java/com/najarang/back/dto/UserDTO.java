package com.najarang.back.dto;

import com.najarang.back.entity.User;
import lombok.*;

@Data
@NoArgsConstructor
public class UserDTO extends BaseTimeDTO {
    private Long id;
    private String nickname;
    private String email;
    private String provider;
    private String role;

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
