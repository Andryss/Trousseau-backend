package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.andryss.trousseau.generated.model.ItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FavouritesApiControllerTest extends BaseApiTest {

    @Test
    @SneakyThrows
    public void addFavouriteTest() {
        ItemDto item = createPublicItem(new BaseApiTest.ItemInfo("title", List.of("media-0", "media-1"), "description", "clothes", 4L));

        mockMvc.perform(
                        get("/public/items/favourites")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "items": []
                        }
                        """)
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
                        content().json("""
                        {
                            "items": [
                                {
                                    "id": "20240520_123001000",
                                    "author": {
                                        "username": "test-username",
                                        "contacts": [ "test-contact-1", "test-contact-2" ],
                                        "room": "test-room"
                                    },
                                    "title": "title",
                                    "media": [
                                        {
                                            "id": "media-0"
                                        },
                                        {
                                            "id": "media-1"
                                        }
                                    ],
                                    "description": "description",
                                    "category": {
                                        "id": "clothes",
                                        "name": "Одежда и обувь"
                                    },
                                    "cost": 4,
                                    "status": "PUBLISHED",
                                    "isFavourite": true,
                                    "publishedAt": "2024-05-20T12:30:02Z"
                                }
                            ]
                        }
                        """)
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
                        content().json("""
                        {
                            "items": []
                        }
                        """)
                );
    }

}