package com.example.demo.repository;

import com.example.demo.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKeyValueAndEnabledTrue(String keyValue);

    boolean existsByKeyValueAndEnabled(String keyValue, boolean enabled);
}
