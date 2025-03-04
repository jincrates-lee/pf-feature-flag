package kr.co.petfriends.sample.infrastructure.external.redis;

import java.util.Optional;
import kr.co.petfriends.sample.domain.Feature;
import kr.co.petfriends.sample.domain.FeatureFlag;
import kr.co.petfriends.sample.domain.FeatureStore;
import org.springframework.stereotype.Component;

@Component
class RedisFeatureFlag implements FeatureFlag {

    @Override
    public FeatureStore getStrategyName() {
        return FeatureStore.REDIS;
    }

    @Override
    public Optional<Feature> getFeature(String featureName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isEnabled(String featureName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isEnabledForUser(
        String featureName,
        Integer userId
    ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
