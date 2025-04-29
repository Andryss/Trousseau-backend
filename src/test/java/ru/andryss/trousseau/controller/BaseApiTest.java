package ru.andryss.trousseau.controller;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.andryss.trousseau.BaseDbTest;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.model.UserEntity;
import ru.andryss.trousseau.repository.UserRepository;
import ru.andryss.trousseau.security.UserData;
import ru.andryss.trousseau.service.MockTimeService;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

        UserEntity userEntity = new UserEntity();
        userEntity.setId("test-id");
        userEntity.setUsername("test-username");
        userEntity.setPasswordHash("test-password-hash");
        userEntity.setContacts(List.of("test-contact-1", "test-contact-2"));
        userEntity.setRoom("test-room");
        userEntity.setCreatedAt(Instant.ofEpochMilli(1_000_000));

        userRepository.save(userEntity);

        UserData userData = new UserData();
        userData.setId(userEntity.getId());
        userData.setUsername(userEntity.getUsername());
        userData.setPrivileges(List.of("test-privilege-1", "test-privilege-2"));

        List<SimpleGrantedAuthority> authorities = userData.getPrivileges().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        SecurityContextHolder.getContext().setAuthentication(
                authenticated(userData, "test-token", authorities)
        );
    }

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

    protected record ItemInfo(String title, List<String> media, String description, String category, Long cost) {
    }
}
