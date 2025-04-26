package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.andryss.trousseau.generated.model.ItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FavouritesApiControllerTest extends BaseApiTest {

    @Test
    @SneakyThrows
    public void addFavouriteTest() {
        ItemDto item = createPublicItem(new BaseApiTest.ItemInfo("title", List.of("media-0", "media-1"), "description", "clothes"));

        mockMvc.perform(
                        get("/public/items/favourites")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.items").isArray(),
                        jsonPath("$.items.size()").value(0)
                );

        mockMvc.perform(
                        post("/public/items/" + item.getId() + "/favourite")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"isFavourite\": \"true\" }")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/public/items/favourites")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.items").isArray(),
                        jsonPath("$.items.size()").value(1),
                        jsonPath("$.items[0].id").value(item.getId()),
                        jsonPath("$.items[0].author.username").value("test-username"),
                        jsonPath("$.items[0].author.contacts.size()").value(2),
                        jsonPath("$.items[0].author.contacts[0]").value("test-contact-1"),
                        jsonPath("$.items[0].author.contacts[1]").value("test-contact-2"),
                        jsonPath("$.items[0].author.room").value("test-room"),
                        jsonPath("$.items[0].title").value("title"),
                        jsonPath("$.items[0].media").isArray(),
                        jsonPath("$.items[0].media.size()").value(2),
                        jsonPath("$.items[0].media[0].id").value("media-0"),
                        jsonPath("$.items[0].media[1].id").value("media-1"),
                        jsonPath("$.items[0].description").value("description"),
                        jsonPath("$.items[0].category.id").value("clothes"),
                        jsonPath("$.items[0].category.name").value("Одежда и обувь"),
                        jsonPath("$.items[0].status").value("PUBLISHED"),
                        jsonPath("$.items[0].isFavourite").value("true"),
                        jsonPath("$.items[0].publishedAt").value("2024-05-20T12:30:02Z")
                );

        mockMvc.perform(
                        post("/public/items/" + item.getId() + "/favourite")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"isFavourite\": \"false\" }")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/public/items/favourites")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.items").isArray(),
                        jsonPath("$.items.size()").value(0)
                );
    }

}