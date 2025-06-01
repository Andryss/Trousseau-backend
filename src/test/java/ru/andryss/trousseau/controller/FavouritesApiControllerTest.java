package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.andryss.trousseau.generated.model.ItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FavouritesApiControllerTest extends BaseApiTest {

    @BeforeEach
    void before() {
        registerUser();
        registerSeller();
    }

    @Test
    @SneakyThrows
    public void addFavouriteTest() {
        String sellerToken = loginAsSeller();

        ItemDto item = createPublicItem(sellerToken, new ItemInfo("title", List.of("media-0", "media-1"), "description", "clothes", 4L));

        String token = loginAsUser();

        mockMvc.perform(addAuthorization(
                        get("/public/items/favourites")
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "items": []
                        }
                        """)
                );

        mockMvc.perform(addAuthorization(
                        post("/public/items/" + item.getId() + "/favourite")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"isFavourite\": \"true\" }"),
                        token
                ))
                .andExpect(status().isOk());

        mockMvc.perform(addAuthorization(
                        get("/public/items/favourites")
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "items": [
                                {
                                    "id": "20240520_123012000",
                                    "author": {
                                        "username": "test-seller",
                                        "contacts": [ "test-contact-0", "test-contact-1" ],
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
                                    "publishedAt": "2024-05-20T12:30:19Z"
                                }
                            ]
                        }
                        """)
                );

        mockMvc.perform(addAuthorization(
                        post("/public/items/" + item.getId() + "/favourite")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"isFavourite\": \"false\" }"),
                        token
                ))
                .andExpect(status().isOk());

        mockMvc.perform(addAuthorization(
                        get("/public/items/favourites")
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
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