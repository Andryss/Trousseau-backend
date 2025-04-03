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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
                        jsonPath("$.id").isNotEmpty(),
                        jsonPath("$.name").value("some-name"),
                        jsonPath("$.data.categoryIds").isArray(),
                        jsonPath("$.data.categoryIds.size()").value(1),
                        jsonPath("$.data.categoryIds[0]").value("all")
                );

        mockMvc.perform(
                        get("/public/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.subscriptions").isArray(),
                        jsonPath("$.subscriptions.size()").value(1),
                        jsonPath("$.subscriptions[0].id").isNotEmpty(),
                        jsonPath("$.subscriptions[0].name").value("some-name"),
                        jsonPath("$.subscriptions[0].data.categoryIds").isArray(),
                        jsonPath("$.subscriptions[0].data.categoryIds.size()").value(1),
                        jsonPath("$.subscriptions[0].data.categoryIds[0]").value("all")
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
                        jsonPath("$.id").value(subscription.getId()),
                        jsonPath("$.name").value("another-name"),
                        jsonPath("$.data.categoryIds").isArray(),
                        jsonPath("$.data.categoryIds.size()").value(3),
                        jsonPath("$.data.categoryIds[0]").value("all"),
                        jsonPath("$.data.categoryIds[1]").value("all"),
                        jsonPath("$.data.categoryIds[2]").value("all")
                );
    }

    @Test
    @SneakyThrows
    void getSubscriptionsTest() {
        SubscriptionDto subscription0 = createSubscription(
                new SubscriptionInfo("test-name-0", new SubscriptionDataInfo(List.of("all")))
        );
        SubscriptionDto subscription1 = createSubscription(
                new SubscriptionInfo("test-name-1", new SubscriptionDataInfo(List.of("all", "all")))
        );

        mockMvc.perform(
                        get("/public/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.subscriptions").isArray(),
                        jsonPath("$.subscriptions.size()").value(2),
                        jsonPath("$.subscriptions[0].id").value(subscription0.getId()),
                        jsonPath("$.subscriptions[0].name").value("test-name-0"),
                        jsonPath("$.subscriptions[0].data.categoryIds").isArray(),
                        jsonPath("$.subscriptions[0].data.categoryIds.size()").value(1),
                        jsonPath("$.subscriptions[0].data.categoryIds[0]").value("all"),
                        jsonPath("$.subscriptions[1].id").value(subscription1.getId()),
                        jsonPath("$.subscriptions[1].name").value("test-name-1"),
                        jsonPath("$.subscriptions[1].data.categoryIds").isArray(),
                        jsonPath("$.subscriptions[1].data.categoryIds.size()").value(2),
                        jsonPath("$.subscriptions[1].data.categoryIds[0]").value("all"),
                        jsonPath("$.subscriptions[1].data.categoryIds[1]").value("all")
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
                        jsonPath("$.subscriptions").isArray(),
                        jsonPath("$.subscriptions.size()").value(0)
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