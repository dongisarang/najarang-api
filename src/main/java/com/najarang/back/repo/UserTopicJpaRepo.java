package com.najarang.back.repo;

import com.najarang.back.entity.UserTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTopicJpaRepo extends JpaRepository<UserTopic, Long> {
}