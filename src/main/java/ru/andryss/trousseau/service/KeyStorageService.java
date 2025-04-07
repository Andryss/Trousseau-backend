package ru.andryss.trousseau.service;

public interface KeyStorageService {
    <T> void put(String key, T value);
    <T> T get(String key, T defaultValue);
}
