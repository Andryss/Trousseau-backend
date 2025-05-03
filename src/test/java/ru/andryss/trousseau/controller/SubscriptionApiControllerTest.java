package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.SneakyThrows;
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

    @Test
    @SneakyThrows
    void createSubscriptionTest() {
        mockMvc.perform(
                        post("/public/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "some-name",
                                    "data": {
                                        "categoryIds": ["all"]
                                    }
                                }
                                """)
                )
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "id": "20240520_123001000",
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

        mockMvc.perform(
                        get("/public/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "subscriptions": [
                                {
                                    "id": "20240520_123001000",
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
        SubscriptionDto subscription = createSubscription(
                new SubscriptionInfo("test-name", new SubscriptionDataInfo(List.of("all")))
        );

        mockMvc.perform(
                        put("/public/subscriptions/{subscriptionId}", subscription.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "another-name",
                                    "data": {
                                        "categoryIds": ["all", "all", "all"]
                                    }
                                }
                                """)
                )
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "id": "20240520_123001000",
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
        createSubscription(new SubscriptionInfo("test-name-0", new SubscriptionDataInfo(List.of("all"))));
        createSubscription(new SubscriptionInfo("test-name-1", new SubscriptionDataInfo(List.of("all", "all"))));

        mockMvc.perform(
                        get("/public/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "subscriptions": [
                                {
                                    "id": "20240520_123001000",
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
                                    "id": "20240520_123002000",
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
        SubscriptionDto subscription = createSubscription(
                new SubscriptionInfo("test-name", new SubscriptionDataInfo(List.of("all")))
        );

        mockMvc.perform(
                        delete("/public/subscriptions/{subscriptionId}", subscription.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/public/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON)
                )
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
    private SubscriptionDto createSubscription(SubscriptionInfo info) {
        MvcResult result = mockMvc.perform(
                        post("/public/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(info))
                )
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), SubscriptionDto.class);
    }

    private record SubscriptionDataInfo(List<String> categoryIds) { }
    private record SubscriptionInfo(String name, SubscriptionDataInfo data) { }
}