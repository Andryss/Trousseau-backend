package ru.andryss.trousseau.controller;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ru.andryss.trousseau.generated.model.UpdateItemResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemApiControllerTest extends BaseApiTest {

    @Autowired
    private ObjectMapper objectMapper;

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
        MvcResult mvcResult = mockMvc.perform(
                        post("/seller/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isOk())
                .andReturn();

        UpdateItemResponse response =
                objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), UpdateItemResponse.class);

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
                    "description": "some description"
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
                    "description": "some description"
                }
                """, "READY")
        );
    }
}