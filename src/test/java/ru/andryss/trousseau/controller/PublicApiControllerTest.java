package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.andryss.trousseau.generated.model.ItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicApiControllerTest extends BaseApiTest {

    @Test
    @SneakyThrows
    public void searchItemsTest() {
        createPublicItem(new ItemInfo("title-0", List.of("media-00"), "description-0", "clothes", 1L));
        createPublicItem(new ItemInfo("title-1", List.of("media-10", "media-11"), "description-1", "clothes", 2L));

        mockMvc.perform(
                        post("/public/items:search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "sort": {
                                        "field": "published_at",
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
                        content().json("""
                        {
                            "items": [
                                {
                                    "id": "20240520_123004000",
                                    "author": {
                                        "username": "test-username",
                                        "contacts": [ "test-contact-1", "test-contact-2" ],
                                        "room": "test-room"
                                    },
                                    "title": "title-1",
                                    "media": [
                                        {
                                            "id": "media-10"
                                        },
                                        {
                                            "id": "media-11"
                                        }
                                    ],
                                    "description": "description-1",
                                    "category": {
                                        "id": "clothes",
                                        "name": "Одежда и обувь"
                                    },
                                    "cost": 2,
                                    "status": "PUBLISHED",
                                    "isFavourite": false,
                                    "publishedAt": "2024-05-20T12:30:05Z"
                                },
                                {
                                    "id": "20240520_123001000",
                                    "author": {
                                        "username": "test-username",
                                        "contacts": [ "test-contact-1", "test-contact-2" ],
                                        "room": "test-room"
                                    },
                                    "title": "title-0",
                                    "media": [
                                        {
                                            "id": "media-00"
                                        }
                                    ],
                                    "description": "description-0",
                                    "category": {
                                        "id": "clothes",
                                        "name": "Одежда и обувь"
                                    },
                                    "cost": 1,
                                    "status": "PUBLISHED",
                                    "isFavourite": false,
                                    "publishedAt": "2024-05-20T12:30:02Z"
                                }
                            ]
                        }
                        """)
                );
    }

    @Test
    @SneakyThrows
    public void getItemTest() {
        ItemDto item = createPublicItem(new ItemInfo("title", List.of("media-0", "media-1"), "description", "clothes", 5L));

        mockMvc.perform(
                        get("/public/items/{itemId}", item.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        content().json("""
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
                            "cost": 5,
                            "status": "PUBLISHED",
                            "isFavourite": false,
                            "publishedAt": "2024-05-20T12:30:02Z"
                        }
                        """)
                );
    }

    @Test
    @SneakyThrows
    public void bookItemTest() {
        ItemDto item = createPublicItem(new ItemInfo("title", List.of("media-0", "media-1"), "description", "clothes", 10L));

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
                                    "cost": 10,
                                    "status": "BOOKED",
                                    "isFavourite": false,
                                    "publishedAt": "2024-05-20T12:30:02Z"
                                }
                            ]
                        }
                        """)
                );

        mockMvc.perform(
                        get("/seller/items/{itemId}/booking", item.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "author": {
                                "username": "test-username",
                                "contacts": [ "test-contact-1", "test-contact-2" ],
                                "room": "test-room"
                            },
                            "bookedAt": "2024-05-20T12:30:04Z"
                        }
                        """)
                );
    }

}