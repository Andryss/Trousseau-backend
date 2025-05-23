package ru.andryss.trousseau.security;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserData {
    private String id;
    private String username;
    private List<String> privileges;
}
