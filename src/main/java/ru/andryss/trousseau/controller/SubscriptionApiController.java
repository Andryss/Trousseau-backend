package ru.andryss.trousseau.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.SubscriptionApi;
import ru.andryss.trousseau.generated.model.CategoryDto;
import ru.andryss.trousseau.generated.model.SubscriptionDataDto;
import ru.andryss.trousseau.generated.model.SubscriptionDto;
import ru.andryss.trousseau.generated.model.SubscriptionInfoRequest;
import ru.andryss.trousseau.generated.model.SubscriptionListResponse;
import ru.andryss.trousseau.model.SubscriptionEntity;
import ru.andryss.trousseau.service.CategoryService;
import ru.andryss.trousseau.service.FavouriteService;
import ru.andryss.trousseau.service.MediaService;
import ru.andryss.trousseau.service.SubscriptionService;

@RestController
public class SubscriptionApiController extends ItemApiController implements SubscriptionApi {

    private final SubscriptionService subscriptionService;

    public SubscriptionApiController(MediaService mediaService, FavouriteService favouriteService,
                                     CategoryService categoryService, SubscriptionService subscriptionService) {
        super(mediaService, favouriteService, categoryService);
        this.subscriptionService = subscriptionService;
    }

    @Override
    public SubscriptionDto createSubscription(SubscriptionInfoRequest request) {
        SubscriptionEntity entity = subscriptionService.create(request);

        return mapToDto(entity);
    }

    @Override
    public void deleteSubscription(String subscriptionId) {
        subscriptionService.delete(subscriptionId);
    }

    @Override
    public SubscriptionListResponse getSubscriptions() {
        List<SubscriptionEntity> entities = subscriptionService.getAll();

        List<SubscriptionDto> dtoList = entities.stream()
                .map(this::mapToDto)
                .toList();

        return new SubscriptionListResponse()
                .subscriptions(dtoList);
    }

    @Override
    public SubscriptionDto updateSubscription(String subscriptionId, SubscriptionInfoRequest request) {
        SubscriptionEntity entity = subscriptionService.update(subscriptionId, request);

        return mapToDto(entity);
    }

    private SubscriptionDto mapToDto(SubscriptionEntity entity) {
        return new SubscriptionDto()
                .id(entity.getId())
                .name(entity.getName())
                .data(new SubscriptionDataDto()
                        .categories(mapCategoryDto(entity.getData().getCategoryIds())));
    }

    private List<CategoryDto> mapCategoryDto(List<String> categoryIds) {
        return categoryIds.stream()
                .map(this::mapCategoryDto)
                .toList();
    }
}
