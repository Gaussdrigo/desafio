package com.example.demo.service;

import com.example.demo.repository.ApiKeyRepository;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    public boolean isValidApiKey(String apiKey) {
        return apiKeyRepository.existsByKeyValueAndEnabled(apiKey, true);
    }
}
