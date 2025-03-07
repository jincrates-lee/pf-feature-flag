package kr.co.petfriends.sample.infrastructure.external.amplitude;

import java.security.SecureRandom;
import kr.co.petfriends.sample.domain.Feature;
import kr.co.petfriends.sample.domain.FeatureFlag;
import kr.co.petfriends.sample.domain.FeatureStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
class AmplitudeFeatureFlag implements FeatureFlag {

    private final WebClient amplitudeWebClient;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public FeatureStore getStrategyName() {
        return FeatureStore.AMPLITUDE;
    }

    @Override
    public Mono<Feature> getFeature(String featureName) {
        return amplitudeWebClient
            .get()
            .uri("/api/1/flags?key=" + featureName)
            .retrieve()
            .bodyToMono(AmplitudeFlagListResponse.class)
            .flatMap(response -> {
                log.info(
                    "Amplitude Response :: {}",
                    response
                );
                return Flux.fromIterable(response.flags())
                    .filter(flag -> featureName.equals(flag.key()))
                    .next()
                    .map(flag -> Feature.builder()
                        .name(featureName)
                        .enabled(flag.enabled())
                        .variants(flag.variants())
                        .build());
            })
            .onErrorResume(error -> {
                log.error(
                    "Error fetching feature flag '{}' from Amplitude: {}",
                    featureName,
                    error.getMessage(),
                    error
                );
                return Mono.empty();
            });
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
        return getFeature(featureName)
            .flatMap(feature -> {
                return Flux.fromStream(feature.variants().stream())
                    .filter(variant -> variantName.equals(variant.get("name")))
                    .next()  // Flux에서 첫 번째 항목을 반환
                    .map(variant -> variant.get("payload"))
                    .flatMap(payload -> {
                        try {
                            return Mono.just(classType.cast(payload));  // 클래스로 캐스팅
                        } catch (ClassCastException e) {
                            log.error(
                                "Failed to cast payload for feature '{}', variant '{}' to type {}: {}",
                                featureName,
                                variantName,
                                classType.getName(),
                                e.getMessage()
                            );
                            return Mono.empty();  // 실패 시 빈 Mono 반환
                        }
                    });
            })
            .switchIfEmpty(Mono.empty())  // variant가 없을 경우 빈 Mono 반환
            .onErrorResume(e -> {
                log.warn(
                    "Error retrieving variant '{}' for feature '{}': {}",
                    variantName,
                    featureName,
                    e.getMessage(),
                    e
                );
                return Mono.empty();  // 오류 발생 시 빈 Mono 반환
            });
    }

    @Override
    public Mono<Boolean> isFeatureEnabledByPercentage(String featureName) {
        return getVariant(
            featureName,
            "rollout_percentage",
            Integer.class
        )
            .flatMap(rolloutPercentage -> Mono.just(isNewFeatureEnabled(rolloutPercentage)))
            .defaultIfEmpty(false);  // 값이 없을 경우 false 반환
    }

    @Override
    public <T> Mono<T> executeFeature(
        String featureName,
        Mono<T> enableAction,
        Mono<T> disableAction
    ) {
        return this.isEnabled(featureName)
            .flatMap(enabled -> enabled ? enableAction : disableAction);
    }

    private boolean isNewFeatureEnabled(int rolloutPercentage) {
        if (rolloutPercentage <= 0) {
            return false;
        }
        if (rolloutPercentage >= 100) {
            return true;
        }

        return SECURE_RANDOM.nextInt(100) < rolloutPercentage;
    }
}
