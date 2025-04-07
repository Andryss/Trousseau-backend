package ru.andryss.trousseau.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.andryss.trousseau.BaseDbTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyStorageServiceTest extends BaseDbTest {

    @Autowired
    KeyStorageService keyStorageService;

    @Test
    void putTest() {
        keyStorageService.put("some-key", "some-value");

        String value = keyStorageService.get("some-key", "another-value");

        assertEquals("some-value", value);
    }

    @Test
    void getAbsentTest() {
        String value = keyStorageService.get("some-absent-key", "default-value");

        assertEquals("default-value", value);
    }

}