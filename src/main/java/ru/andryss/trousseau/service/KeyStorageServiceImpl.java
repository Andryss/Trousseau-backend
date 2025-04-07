package ru.andryss.trousseau.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.repository.KeyStorageRepository;

@Service
@RequiredArgsConstructor
public class KeyStorageServiceImpl implements KeyStorageService {

    private final KeyStorageRepository keyStorageRepository;
    private final ObjectMapperWrapper objectMapper;

    @Override
    public <T> void put(String key, T value) {
        String valueStr = objectMapper.writeValueAsString(value);
        keyStorageRepository.upsert(key, valueStr);
    }

    @Override
    public <T> T get(String key, T defaultValue) {
        Optional<String> optional = keyStorageRepository.get(key);
        if (optional.isEmpty()) {
            return defaultValue;
        }
        return objectMapper.readValue(optional.get());
    }
}
