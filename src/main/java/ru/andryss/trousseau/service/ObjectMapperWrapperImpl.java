package ru.andryss.trousseau.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObjectMapperWrapperImpl implements ObjectMapperWrapper {

    private final ObjectMapper mapper;

    @Override
    public String writeValueAsString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return String.valueOf(obj);
        }
    }

    @Override
    @SneakyThrows
    public <T> T readValue(String data) {
        return mapper.readValue(data, new TypeReference<>() {
        });
    }

    @Override
    @SneakyThrows
    public <T> T readValue(String data, Class<T> clazz) {
        return mapper.readValue(data, clazz);
    }
}
