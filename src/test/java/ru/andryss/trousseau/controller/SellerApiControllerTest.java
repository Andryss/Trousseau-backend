package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.generated.model.ItemStatus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SellerApiControllerTest extends BaseApiTest {

    @BeforeEach
    void before() {
        registerSeller();
    }

    @ParameterizedTest
    @MethodSource("createItemData")
    @SneakyThrows
    public void createItemTest(String content, String expectedStatus) {
        String token = loginAsSeller();

        mockMvc.perform(addAuthorization(
                        post("/seller/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "id": "20240520_123009000",
                            "status": "%s"
                        }
                        """.formatted(expectedStatus))
                );
    }

    @ParameterizedTest
    @MethodSource("updateItemData")
    @SneakyThrows
    public void updateItemTest(String content, String expectedStatus) {
        String token = loginAsSeller();

        ItemDto response = createEmptyItem(token);

        String itemId = response.getId();

        mockMvc.perform(addAuthorization(
                        put("/seller/items/{itemId}", itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "id": "20240520_123009000",
                            "status": "%s"
                        }
                        """.formatted(expectedStatus))
                );
    }

    @Test
    @SneakyThrows
    public void getItemsTest() {
        String token = loginAsSeller();
        createEmptyItem(token);
        createEmptyItem(token);

        mockMvc.perform(addAuthorization(
                        get("/seller/items")
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "items": [
                                {
                                    "id": "20240520_123013000",
                                    "status": "DRAFT"
                                },
                                {
                                    "id": "20240520_123009000",
                                    "status": "DRAFT"
                                }
                            ]
                        }
                        """)
                );
    }

    @Test
    @SneakyThrows
    public void getItemTest() {
        String token = loginAsSeller();

        ItemDto response = createEmptyItem(token);

        String id = response.getId();

        mockMvc.perform(addAuthorization(
                        get("/seller/items/{itemId}", id)
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "id": "20240520_123009000",
                            "title": null,
                            "media": [],
                            "description": null,
                            "category": null,
                            "cost": 0,
                            "status": "DRAFT"
                        }
                        """)
                );
    }

    @Test
    @SneakyThrows
    public void changeItemStatus() {
        String token = loginAsSeller();
        ItemDto response = createEmptyItem(token);
        String id = response.getId();

        ItemDto updated = updateItem(token, id, new ItemInfo(
                "some-title",
                List.of("media-1"),
                "some-description",
                "clothes",
                30L
        ));

        Assertions.assertEquals(ItemStatus.READY, updated.getStatus());

        mockMvc.perform(addAuthorization(
                        put("/seller/items/{itemId}/status", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"status\": \"PUBLISHED\" }"),
                        token
                ))
                .andExpect(status().isOk());

        mockMvc.perform(addAuthorization(
                        put("/seller/items/{itemId}/status", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"status\": \"READY\" }"),
                        token
                ))
                .andExpect(status().isOk());
    }

    public static List<Arguments> createItemData() {
        return List.of(
                Arguments.of("{}", "DRAFT"),
                Arguments.of("""
                {
                    "title": ""
                }
                """, "DRAFT"),
                Arguments.of("""
                {
                    "title": "some title",
                    "media": ["some media id"],
                    "description": "some description",
                    "category": "clothes"
                }
                """, "READY")
        );
    }

    public static List<Arguments> updateItemData() {
        return List.of(
                Arguments.of("{}", "DRAFT"),
                Arguments.of("""
                {
                    "title": ""
                }
                """, "DRAFT"),
                Arguments.of("""
                {
                    "title": "some title",
                    "media": ["some media id"],
                    "description": "some description",
                    "category": "clothes"
                }
                """, "READY")
        );
    }
}