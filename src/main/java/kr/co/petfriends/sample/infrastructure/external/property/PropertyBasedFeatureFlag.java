package kr.co.petfriends.sample.infrastructure.external.property;

import kr.co.petfriends.sample.domain.Feature;
import kr.co.petfriends.sample.domain.FeatureFlag;
import kr.co.petfriends.sample.domain.FeatureStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PropertyBasedFeatureFlag implements FeatureFlag {

    private final PropertyBasedFeatureProperties properties;

    @Override
    public FeatureStore getStrategyName() {
        return FeatureStore.PROPERTY;
    }

    @Override
    public Mono<Feature> getFeature(String featureName) {
        return Mono.just(properties.getFeature(featureName));
    }

    @Override
    public Mono<Boolean> isEnabled(String featureName) {
        return getFeature(featureName)
            .map(Feature::enabled)
            .defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> isEnabledForUser(
        String featureName,
        Integer userId
    ) {
        return isEnabled(featureName)
            .flatMap(isEnabled -> {
                if (!isEnabled) {
                    return Mono.just(false);
                }
                return getFeature(featureName)
                    .map(feature -> feature.variants().stream()
                        .anyMatch(variant -> userId.equals(variant.get("userId"))))
                    .defaultIfEmpty(false);
            });
    }

    @Override
    public <T> Mono<T> getVariant(
        String featureName,
        String variantName,
        Class<T> classType
    ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<Boolean> isFeatureEnabledByPercentage(String featureName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> Mono<T> executeFeature(
        String featureName,
        Mono<T> enableAction,
        Mono<T> disableAction
    ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
