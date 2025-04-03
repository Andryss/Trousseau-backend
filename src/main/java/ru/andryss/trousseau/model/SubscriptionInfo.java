package ru.andryss.trousseau.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubscriptionInfo {
    private List<String> categoryIds;
}
