package com.najarang.back.repo;

import com.najarang.back.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageJpaRepo extends JpaRepository<Image, Long> {
    Optional<Image> findById(Long id);
}
