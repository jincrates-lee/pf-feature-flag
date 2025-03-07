package kr.co.petfriends.sample.domain;

import reactor.core.publisher.Mono;

public interface FeatureFlag {

    FeatureStore getStrategyName();

    Mono<Feature> getFeature(String featureName);

    Mono<Boolean> isEnabled(String featureName);

    Mono<Boolean> isEnabledForUser(
        String featureName,
        Integer userId
    );

    <T> Mono<T> getVariant(
        String featureName,
        String variantName,
        Class<T> classType
    );

    Mono<Boolean> isFeatureEnabledByPercentage(String featureName);

    <T> Mono<T> executeFeature(
        String featureName,
        Mono<T> enableAction,
        Mono<T> disableAction
    );
}
