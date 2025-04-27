package ru.andryss.trousseau.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiControllerTest extends BaseApiTest {

    @Test
    @SneakyThrows
    void getProfileInfoTest() {
        mockMvc.perform(
                        get("/auth/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "username": "test-username",
                            "contacts": [
                                "test-contact-1",
                                "test-contact-2"
                            ],
                            "room": "test-room",
                            "privileges": [
                                "test-privilege-1",
                                "test-privilege-2"
                            ]
                        }
                        """)
                );
    }

}