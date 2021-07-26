package com.najarang.back.entity;

import com.najarang.back.dto.UserDTO;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;


/*
* @Builder와 @NoArgsConstructor를 함께 사용하려면,
* @AllArgsConstructor도 함께 사용하거나 모든 필드를 가지는 생성자를 직접 만들어 줘야 함
*
* @DynamicUpdate : 실제 값이 변경된 컬럼으로만 update 쿼리를 만드는 기능
 * */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
@DynamicUpdate
@Table(name = "user") // 'user' 테이블과 매핑
public class User extends BaseTime {

    @Id // primaryKey
    @GeneratedValue(strategy = GenerationType.IDENTITY) //  pk 필드를 auto_increment로 설정
    private Long id;

    @Column()
    private String nickname;

    @Column()
    private String email;

    @Column()
    private String provider;

    @Column()
    private String role;

    // 다대다를 위한 topics 사용
    @OneToMany(mappedBy = "user")
    private Collection<UserTopic> topics = new ArrayList<>();

    public UserDTO toDTO(){
        return new UserDTO(this);
    }

    // oneToMany 때문에 lomboc의 toString은 무한루프 발생해서 재정의
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", provider='" + provider + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}