package ru.andryss.trousseau.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConstantsApiControllerTest extends BaseApiTest {

    @Test
    @SneakyThrows
    public void getCategoryTreeTest() {
        mockMvc.perform(
                        get("/public/categories/tree")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.root.id").value("all"),
                        jsonPath("$.root.name").value("Все категории"),
                        jsonPath("$.root.children").isArray(),
                        jsonPath("$.root.children.size()").value(9)
                );
    }

}