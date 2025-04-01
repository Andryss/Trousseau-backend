package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SellerApiControllerTest extends BaseApiTest {

    @ParameterizedTest
    @MethodSource("createItemData")
    @SneakyThrows
    public void createItemTest(String content, String expectedStatus) {
        mockMvc.perform(
                        post("/seller/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").isNotEmpty(),
                        jsonPath("$.status").value(expectedStatus)
                );
    }

    @ParameterizedTest
    @MethodSource("updateItemData")
    @SneakyThrows
    public void updateItemTest(String content, String expectedStatus) {
        ItemDto response = createEmptyItem();

        String itemId = response.getId();

        mockMvc.perform(
                        put("/seller/items/{itemId}", itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").isNotEmpty(),
                        jsonPath("$.status").value(expectedStatus)
                );
    }

    @Test
    @SneakyThrows
    public void getItemsTest() {
        ItemDto response0 = createEmptyItem();
        ItemDto response1 = createEmptyItem();

        mockMvc.perform(
                        get("/seller/items")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.items").isArray(),
                        jsonPath("$.items.size()").value(2),
                        jsonPath("$.items[0].id").value(response1.getId()),
                        jsonPath("$.items[0].status").value(response1.getStatus().getValue()),
                        jsonPath("$.items[1].id").value(response0.getId()),
                        jsonPath("$.items[1].status").value(response0.getStatus().getValue())
                );
    }

    @Test
    @SneakyThrows
    public void getItemTest() {
        ItemDto response = createEmptyItem();

        String id = response.getId();

        mockMvc.perform(
                        get("/seller/items/{itemId}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").isNotEmpty(),
                        jsonPath("$.title").isEmpty(),
                        jsonPath("$.media").isEmpty(),
                        jsonPath("$.description").isEmpty(),
                        jsonPath("$.status").value("DRAFT")
                );
    }

    @Test
    @SneakyThrows
    public void changeItemStatus() {
        ItemDto response = createEmptyItem();
        String id = response.getId();

        ItemDto updated = updateItem(id, new ItemInfo(
                "some-title",
                List.of("media-1"),
                "some-description",
                "clothes"
        ));

        Assertions.assertEquals(ItemStatus.READY, updated.getStatus());

        mockMvc.perform(
                        put("/seller/items/{itemId}/status", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"status\": \"PUBLISHED\" }")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/seller/items/{itemId}/status", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"status\": \"READY\" }")
                )
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