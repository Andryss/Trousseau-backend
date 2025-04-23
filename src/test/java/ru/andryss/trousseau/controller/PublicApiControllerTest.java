package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.andryss.trousseau.generated.model.ItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicApiControllerTest extends BaseApiTest {

    @Test
    @SneakyThrows
    public void searchItemsTest() {
        ItemDto item0 = createPublicItem(new ItemInfo("title-0", List.of("media-00"), "description-0", "clothes"));
        ItemDto item1 = createPublicItem(new ItemInfo("title-1", List.of("media-10", "media-11"), "description-1", "clothes"));

        mockMvc.perform(
                        post("/public/items:search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "sort": {
                                        "field": "created_at",
                                        "order": "desc"
                                    },
                                    "filter": {
                                        "conditions": []
                                    },
                                    "page": {
                                        "size": 2
                                    }
                                }
                                """)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.items").isArray(),
                        jsonPath("$.items.size()").value(2),
                        jsonPath("$.items[0].id").value(item1.getId()),
                        jsonPath("$.items[0].author.username").value("test-username"),
                        jsonPath("$.items[0].author.contacts.size()").value(2),
                        jsonPath("$.items[0].author.contacts[0]").value("test-contact-1"),
                        jsonPath("$.items[0].author.contacts[1]").value("test-contact-2"),
                        jsonPath("$.items[0].author.room").value("test-room"),
                        jsonPath("$.items[0].title").value("title-1"),
                        jsonPath("$.items[0].media").isArray(),
                        jsonPath("$.items[0].media.size()").value(2),
                        jsonPath("$.items[0].media[0].id").value("media-10"),
                        jsonPath("$.items[0].media[1].id").value("media-11"),
                        jsonPath("$.items[0].description").value("description-1"),
                        jsonPath("$.items[0].category.id").value("clothes"),
                        jsonPath("$.items[0].category.name").value("Одежда и обувь"),
                        jsonPath("$.items[0].status").value("PUBLISHED"),
                        jsonPath("$.items[0].isFavourite").value("false"),
                        jsonPath("$.items[0].publishedAt").value("2024-05-20T12:30:05Z"),
                        jsonPath("$.items[1].id").value(item0.getId()),
                        jsonPath("$.items[1].author.username").value("test-username"),
                        jsonPath("$.items[1].author.contacts.size()").value(2),
                        jsonPath("$.items[1].author.contacts[0]").value("test-contact-1"),
                        jsonPath("$.items[1].author.contacts[1]").value("test-contact-2"),
                        jsonPath("$.items[1].author.room").value("test-room"),
                        jsonPath("$.items[1].title").value("title-0"),
                        jsonPath("$.items[1].media").isArray(),
                        jsonPath("$.items[1].media.size()").value(1),
                        jsonPath("$.items[1].media[0].id").value("media-00"),
                        jsonPath("$.items[1].description").value("description-0"),
                        jsonPath("$.items[1].category.id").value("clothes"),
                        jsonPath("$.items[1].category.name").value("Одежда и обувь"),
                        jsonPath("$.items[1].status").value("PUBLISHED"),
                        jsonPath("$.items[1].isFavourite").value("false"),
                        jsonPath("$.items[1].publishedAt").value("2024-05-20T12:30:02Z")
                );
    }

    @Test
    @SneakyThrows
    public void getItemTest() {
        ItemDto item = createPublicItem(new ItemInfo("title", List.of("media-0", "media-1"), "description", "clothes"));

        mockMvc.perform(
                        get("/public/items/{itemId}", item.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(item.getId()),
                        jsonPath("$.author.username").value("test-username"),
                        jsonPath("$.author.contacts.size()").value(2),
                        jsonPath("$.author.contacts[0]").value("test-contact-1"),
                        jsonPath("$.author.contacts[1]").value("test-contact-2"),
                        jsonPath("$.author.room").value("test-room"),
                        jsonPath("$.title").value("title"),
                        jsonPath("$.media").isArray(),
                        jsonPath("$.media.size()").value(2),
                        jsonPath("$.media[0].id").value("media-0"),
                        jsonPath("$.media[1].id").value("media-1"),
                        jsonPath("$.description").value("description"),
                        jsonPath("$.category.id").value("clothes"),
                        jsonPath("$.category.name").value("Одежда и обувь"),
                        jsonPath("$.status").value("PUBLISHED"),
                        jsonPath("$.isFavourite").value("false"),
                        jsonPath("$.publishedAt").value("2024-05-20T12:30:02Z")
                );
    }

    @Test
    @SneakyThrows
    public void bookItemTest() {
        ItemDto item = createPublicItem(new ItemInfo("title", List.of("media-0", "media-1"), "description", "clothes"));

        mockMvc.perform(
                        put("/public/items/{itemId}/status", item.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"status\": \"BOOKED\" }")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/public/items/bookings")
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
                        jsonPath("$.items[0].status").value("BOOKED"),
                        jsonPath("$.items[0].isFavourite").value("false"),
                        jsonPath("$.items[0].publishedAt").value("2024-05-20T12:30:02Z")
                );
    }

    @Test
    @SneakyThrows
    public void addFavouriteTest() {
        ItemDto item = createPublicItem(new ItemInfo("title", List.of("media-0", "media-1"), "description", "clothes"));

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