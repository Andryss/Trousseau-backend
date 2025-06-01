package ru.andryss.trousseau.service;

/**
 * Класс, инкапсулирующий работу с {@link com.fasterxml.jackson.databind.ObjectMapper}
 */
public interface ObjectMapperWrapper {
    /**
     * Сериализовать объект в JSON-строку
     */
    String writeValueAsString(Object obj);

    /**
     * Десереализовать объект из JSON-строки
     */
    <T> T readValue(String data);

    /**
     * Десереализовать объект заданного класса из JSON-строки
     */
    <T> T readValue(String data, Class<T> clazz);
}
