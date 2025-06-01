package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ru.andryss.trousseau.generated.model.SubscriptionDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SubscriptionApiControllerTest extends BaseApiTest {

    @BeforeEach
    void before() {
        registerUser();
    }

    @Test
    @SneakyThrows
    void createSubscriptionTest() {
        String token = loginAsUser();

        mockMvc.perform(addAuthorization(
                        post("/public/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "some-name",
                                    "data": {
                                        "categoryIds": ["all"]
                                    }
                                }
                                """),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "id": "20240520_123009000",
                            "name": "some-name",
                            "data": {
                                "categories": [
                                    {
                                        "id": "all",
                                        "name": "Все категории"
                                    }
                                ]
                            }
                        }
                        """)
                );

        mockMvc.perform(addAuthorization(
                        get("/public/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "subscriptions": [
                                {
                                    "id": "20240520_123009000",
                                    "name": "some-name",
                                    "data": {
                                        "categories": [
                                            {
                                                "id": "all",
                                                "name": "Все категории"
                                            }
                                        ]
                                    }
                                }
                            ]
                        }
                        """)
                );
    }

    @Test
    @SneakyThrows
    void updateSubscriptionTest() {
        String token = loginAsUser();

        SubscriptionDto subscription = createSubscription(token, new SubscriptionInfo("test-name", new SubscriptionDataInfo(List.of("all"))));

        mockMvc.perform(addAuthorization(
                        put("/public/subscriptions/{subscriptionId}", subscription.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "another-name",
                                    "data": {
                                        "categoryIds": ["all", "all", "all"]
                                    }
                                }
                                """),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "id": "20240520_123009000",
                            "name": "another-name",
                            "data": {
                                "categories": [
                                    {
                                        "id": "all",
                                        "name": "Все категории"
                                    },
                                    {
                                        "id": "all",
                                        "name": "Все категории"
                                    },
                                    {
                                        "id": "all",
                                        "name": "Все категории"
                                    }
                                ]
                            }
                        }
                        """)
                );
    }

    @Test
    @SneakyThrows
    void getSubscriptionsTest() {
        String token = loginAsUser();

        createSubscription(token, new SubscriptionInfo("test-name-0", new SubscriptionDataInfo(List.of("all"))));
        createSubscription(token, new SubscriptionInfo("test-name-1", new SubscriptionDataInfo(List.of("all", "all"))));

        mockMvc.perform(addAuthorization(
                        get("/public/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "subscriptions": [
                                {
                                    "id": "20240520_123009000",
                                    "name": "test-name-0",
                                    "data": {
                                        "categories": [
                                            {
                                                "id": "all",
                                                "name": "Все категории"
                                            }
                                        ]
                                    }
                                },
                                {
                                    "id": "20240520_123013000",
                                    "name": "test-name-1",
                                    "data": {
                                        "categories": [
                                            {
                                                "id": "all",
                                                "name": "Все категории"
                                            },
                                            {
                                                "id": "all",
                                                "name": "Все категории"
                                            }
                                        ]
                                    }
                                }
                            ]
                        }
                        """)
                );
    }

    @Test
    @SneakyThrows
    void deleteSubscriptionTest() {
        String token = loginAsUser();

        SubscriptionDto subscription = createSubscription(token, new SubscriptionInfo("test-name", new SubscriptionDataInfo(List.of("all"))));

        mockMvc.perform(addAuthorization(
                        delete("/public/subscriptions/{subscriptionId}", subscription.getId())
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
                .andExpect(status().isOk());

        mockMvc.perform(addAuthorization(
                        get("/public/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "subscriptions": [ ]
                        }
                        """)
                );
    }

    @SneakyThrows
    private SubscriptionDto createSubscription(String token, SubscriptionInfo info) {
        MvcResult result = mockMvc.perform(addAuthorization(
                        post("/public/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(info)),
                        token
                ))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), SubscriptionDto.class);
    }

    private record SubscriptionDataInfo(List<String> categoryIds) { }
    private record SubscriptionInfo(String name, SubscriptionDataInfo data) { }
}