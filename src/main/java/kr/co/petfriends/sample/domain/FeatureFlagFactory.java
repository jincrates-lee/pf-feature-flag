package kr.co.petfriends.sample.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeatureFlagFactory {

    private final Map<FeatureStore, FeatureFlag> strategyMap;

    @Autowired
    public FeatureFlagFactory(Set<FeatureFlag> strategySet) {
        this.strategyMap = initStrategy(strategySet);
    }

    public FeatureFlag findStrategy(FeatureStore strategyType) {
        FeatureFlag strategy = strategyMap.get(strategyType);
        if (strategy == null) {
            throw new IllegalStateException(String.format(
                "No strategy found for type %s",
                strategyType
            ));
        }
        return strategy;
    }

    private Map<FeatureStore, FeatureFlag> initStrategy(Set<FeatureFlag> strategySet) {
        HashMap<FeatureStore, FeatureFlag> hashMap = new HashMap<>();
        strategySet.forEach(strategy -> hashMap.put(
            strategy.getStrategyName(),
            strategy
        ));
        return hashMap;
    }
}
