package ru.andryss.trousseau.controller;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.andryss.trousseau.BaseDbTest;
import ru.andryss.trousseau.generated.model.ItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public abstract class BaseApiTest extends BaseDbTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @SneakyThrows
    protected ItemDto createEmptyItem() {
        MvcResult mvcResult = mockMvc.perform(
                        post("/seller/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), ItemDto.class);
    }

    @SneakyThrows
    protected ItemDto updateItem(String id, ItemInfo request) {
        MvcResult mvcResult = mockMvc.perform(
                        put("/seller/items/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), ItemDto.class);
    }

    @SneakyThrows
    protected void publishItem(String id) {
        mockMvc.perform(
                        put("/seller/items/{itemId}/status", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"status\": \"PUBLISHED\" }")
                )
                .andExpect(status().isOk());
    }

    protected ItemDto createPublicItem(ItemInfo itemInfo) {
        String id = createEmptyItem().getId();
        ItemDto item = updateItem(id, itemInfo);
        publishItem(id);
        return item;
    }

    protected record ItemInfo(String title, List<String> media, String description, String category) {
    }
}
