package ru.andryss.trousseau.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.andryss.trousseau.BaseDbTest;
import ru.andryss.trousseau.generated.model.AuthResponse;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.repository.UserRepository;
import ru.andryss.trousseau.service.MockTimeService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public abstract class BaseApiTest extends BaseDbTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockTimeService timeService;

    @BeforeEach
    void setUp() {
        timeService.reset();
    }

    @SneakyThrows
    protected void registerUser() {
        mockMvc.perform(
                        post("/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "test-user",
                                            "password": "test-user-password",
                                            "contacts": [ "test-contact-0" ]
                                        }
                                        """)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.token").isNotEmpty()
                );
    }

    @SneakyThrows
    protected void registerSeller() {
        mockMvc.perform(
                        post("/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "test-seller",
                                            "password": "test-seller-password",
                                            "contacts": [ "test-contact-0", "test-contact-1" ],
                                            "room": "test-room"
                                        }
                                        """)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.token").isNotEmpty()
                );
    }

    @SneakyThrows
    protected String loginAsUser() {
        MvcResult mvcResult = mockMvc.perform(
                        post("/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "test-user",
                                            "password": "test-user-password"
                                        }
                                        """)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.token").isNotEmpty()
                )
                .andReturn();

        return extractResponseToken(mvcResult);
    }

    @SneakyThrows
    protected String loginAsSeller() {
        MvcResult mvcResult = mockMvc.perform(
                        post("/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "test-seller",
                                            "password": "test-seller-password"
                                        }
                                        """)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.token").isNotEmpty()
                )
                .andReturn();

        return extractResponseToken(mvcResult);
    }

    @SneakyThrows
    protected void logout(String token) {
        mockMvc.perform(addAuthorization(
                        post("/auth/signout")
                                .contentType(MediaType.APPLICATION_JSON),
                        token
                ))
                .andExpect(status().isOk());
    }

    protected MockHttpServletRequestBuilder addAuthorization(MockHttpServletRequestBuilder builder, String token) {
        return builder.header("Authorization", "Bearer " + token);

    }

    @SneakyThrows
    protected ItemDto createEmptyItem(String token) {
        MvcResult mvcResult = mockMvc.perform(addAuthorization(
                        post("/seller/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"),
                        token
                ))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), ItemDto.class);
    }

    @SneakyThrows
    protected ItemDto updateItem(String token, String id, ItemInfo request) {
        MvcResult mvcResult = mockMvc.perform(addAuthorization(
                        put("/seller/items/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)),
                        token
                ))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), ItemDto.class);
    }

    @SneakyThrows
    protected void publishItem(String token, String id) {
        mockMvc.perform(addAuthorization(
                        put("/seller/items/{itemId}/status", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"status\": \"PUBLISHED\" }"),
                        token
                ))
                .andExpect(status().isOk());
    }

    protected ItemDto createPublicItem(String token, ItemInfo itemInfo) {
        String id = createEmptyItem(token).getId();
        ItemDto item = updateItem(token, id, itemInfo);
        publishItem(token, id);
        return item;
    }

    protected record ItemInfo(String title, List<String> media, String description, String category, Long cost) {
    }

    private String extractResponseToken(MvcResult mvcResult) throws IOException {
        byte[] responseBody = mvcResult.getResponse().getContentAsByteArray();
        AuthResponse response = objectMapper.readValue(responseBody, AuthResponse.class);
        return response.getToken();
    }
}
