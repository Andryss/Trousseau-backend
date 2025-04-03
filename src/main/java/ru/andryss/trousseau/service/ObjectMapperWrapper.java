package ru.andryss.trousseau.service;

public interface ObjectMapperWrapper {
    String writeValueAsString(Object obj);
    <T> T readValue(String data);
    <T> T readValue(String data, Class<T> clazz);
}
