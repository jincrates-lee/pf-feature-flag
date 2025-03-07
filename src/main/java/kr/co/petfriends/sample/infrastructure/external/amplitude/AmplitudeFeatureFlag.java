package kr.co.petfriends.sample.infrastructure.external.amplitude;

import java.security.SecureRandom;
import java.util.Optional;
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
    public Optional<Feature> getFeature(String featureName) {
        // TODO: 단일 AmplitudeFeatureFlag만 조회할 수 있을까
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
            })
            .blockOptional();
    }

    @Override
    public boolean isEnabled(String featureName) {
        return getFeature(featureName)
            .map(Feature::enabled)
            .orElse(false);
    }

    @Override
    public boolean isEnabledForUser(
        String featureName,
        Integer userId
    ) {
        if (!isEnabled(featureName)) {
            return false;
        }

        return getFeature(featureName)
            .map(feature -> feature.variants().stream()
                .anyMatch(variant -> userId.equals(variant.get("userId"))))
            .orElse(false);
    }

    @Override
    public <T> T getVariant(
        String featureName,
        String variantName,
        Class<T> classType
    ) {
        try {
            return getFeature(featureName)
                .flatMap(feature -> feature.variants().stream()
                    .filter(variant -> variantName.equals(variant.get("name")))
                    .findFirst()
                    .map(variant -> variant.get("payload"))
                )
                .map(payload -> {
                    try {
                        return classType.cast(payload);
                    } catch (ClassCastException e) {
                        log.error(
                            "Failed to cast payload for feature '{}', variant '{}' to type {}: {}",
                            featureName,
                            variantName,
                            classType.getName(),
                            e.getMessage()
                        );
                        return null;
                    }
                })
                .orElse(null);
        } catch (Exception e) {
            log.warn(
                "Error retrieving variant '{}' for feature '{}': {}",
                variantName,
                featureName,
                e.getMessage(),
                e
            );
            return null;
        }
    }

    @Override
    public boolean isFeatureEnabledByPercentage(String featureName) {
        Integer rolloutPercentage = getVariant(
            featureName,
            "rollout_percentage",
            Integer.class
        );

        return isNewFeatureEnabled(rolloutPercentage);
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
