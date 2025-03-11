package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.andryss.trousseau.generated.model.ItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicApiControllerTest extends BaseApiTest {

    @Test
    @SneakyThrows
    public void searchItemsTest() {
        ItemDto item0 = createPublicItem(new ItemInfo("title-0", List.of("media-00"), "description-0"));
        ItemDto item1 = createPublicItem(new ItemInfo("title-1", List.of("media-10", "media-11"), "description-1"));

        mockMvc.perform(
                        post("/public/items:search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.items").isArray(),
                        jsonPath("$.items.size()").value(2),
                        jsonPath("$.items[0].id").value(item1.getId()),
                        jsonPath("$.items[0].title").value("title-1"),
                        jsonPath("$.items[0].media").isArray(),
                        jsonPath("$.items[0].media.size()").value(2),
                        jsonPath("$.items[0].media[0].id").value("media-10"),
                        jsonPath("$.items[0].media[1].id").value("media-11"),
                        jsonPath("$.items[0].description").value("description-1"),
                        jsonPath("$.items[1].id").value(item0.getId()),
                        jsonPath("$.items[1].title").value("title-0"),
                        jsonPath("$.items[1].media").isArray(),
                        jsonPath("$.items[1].media.size()").value(1),
                        jsonPath("$.items[1].media[0].id").value("media-00"),
                        jsonPath("$.items[1].description").value("description-0")
                );
    }

}