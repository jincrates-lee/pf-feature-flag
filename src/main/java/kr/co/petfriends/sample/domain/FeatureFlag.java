package kr.co.petfriends.sample.domain;

import java.util.Optional;

public interface FeatureFlag {

    FeatureStore getStrategyName();

    Optional<Feature> getFeature(String featureName);

    boolean isEnabled(String featureName);

    boolean isEnabledForUser(
        String featureName,
        Integer userId
    );
}
