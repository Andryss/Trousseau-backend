package ru.andryss.trousseau.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiControllerTest extends BaseApiTest {

    @BeforeEach
    void before() {
        registerUser();
        registerSeller();
    }

    @Test
    @SneakyThrows
    void getProfileInfoAsUserTest() {
        String token = loginAsUser();

        mockMvc.perform(addAuthorization(
                        get("/auth/profile")
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "username": "test-user",
                            "contacts": [
                                "test-contact-0"
                            ],
                            "privileges": [
                                "ROLE_USER",
                                "ITEMS_PUBLISHED_VIEW",
                                "ITEMS_PUBLISHED_STATUS_CHANGED",
                                "ITEMS_BOOKINGS_VIEW",
                                "CATEGORY_TREE_VIEW",
                                "ITEMS_FAVOURITES",
                                "SUBSCRIPTIONS_VIEW",
                                "SUBSCRIPTIONS_EDIT",
                                "NOTIFICATIONS_VIEW"
                            ]
                        }
                        """)
                );
    }

    @Test
    @SneakyThrows
    void getProfileInfoAsSellerTest() {
        String token = loginAsSeller();

        mockMvc.perform(addAuthorization(
                        get("/auth/profile")
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "username": "test-seller",
                            "contacts": [
                                "test-contact-0",
                                "test-contact-1"
                            ],
                            "privileges": [
                                "ROLE_USER",
                                "ROLE_SELLER",
                                "MEDIA_UPLOAD",
                                "ITEMS_CREATE",
                                "ITEMS_CREATED_VIEW",
                                "ITEMS_PUBLISHED_VIEW",
                                "ITEMS_PUBLISHED_STATUS_CHANGED",
                                "ITEMS_BOOKINGS_VIEW",
                                "CATEGORY_TREE_VIEW",
                                "ITEMS_FAVOURITES",
                                "SUBSCRIPTIONS_VIEW",
                                "SUBSCRIPTIONS_EDIT",
                                "NOTIFICATIONS_VIEW"
                            ]
                        }
                        """)
                );
    }

    @Test
    @SneakyThrows
    void getProfileInfoWithoutLoginTest() {
        mockMvc.perform(
                        get("/auth/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isBadRequest(),
                        content().json("""
                        {
                            "code": 401,
                            "message": "user.unauthorized",
                            "humanMessage": "Пользователь не авторизован"
                        }
                        """)
                );
    }

    @Test
    @SneakyThrows
    void getProfileInfoAsLoggedOutTest() {
        String token = loginAsUser();

        logout(token);

        mockMvc.perform(addAuthorization(
                        get("/auth/profile")
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
                .andExpectAll(
                        status().isBadRequest(),
                        content().json("""
                        {
                            "code": 401,
                            "message": "user.unauthorized.session.absent",
                            "humanMessage": "Пользователь не авторизован"
                        }
                        """)
                );
    }

}