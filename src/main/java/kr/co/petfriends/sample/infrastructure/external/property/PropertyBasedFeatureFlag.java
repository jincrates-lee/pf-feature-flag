package kr.co.petfriends.sample.infrastructure.external.property;

import java.util.Optional;
import kr.co.petfriends.sample.domain.Feature;
import kr.co.petfriends.sample.domain.FeatureFlag;
import kr.co.petfriends.sample.domain.FeatureStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertyBasedFeatureFlag implements FeatureFlag {

    private final PropertyBasedFeatureProperties properties;

    @Override
    public FeatureStore getStrategyName() {
        return FeatureStore.PROPERTY;
    }

    @Override
    public Optional<Feature> getFeature(String featureName) {
        return Optional.ofNullable(properties.getFeature(featureName));
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
}
