package kr.co.petfriends.sample.application;

import kr.co.petfriends.sample.domain.FeatureFlag;
import kr.co.petfriends.sample.domain.FeatureFlagFactory;
import kr.co.petfriends.sample.domain.FeatureStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/features")
public class FeatureFlagController {

    private final FeatureFlagFactory featureFlagFactory;

    @GetMapping("/{storeType}/{featureName}")
    public ResponseEntity<?> getFeatureFlag(
        @PathVariable String storeType,
        @PathVariable String featureName
    ) {
        FeatureFlag strategy = featureFlagFactory.findStrategy(FeatureStore.fromString(storeType));
        return strategy.getFeature(featureName)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{storeType}/{featureName}/{userId}")
    public ResponseEntity<?> getFeatureFlag(
        @PathVariable String storeType,
        @PathVariable String featureName,
        @PathVariable Integer userId
    ) {
        FeatureFlag strategy = featureFlagFactory.findStrategy(FeatureStore.fromString(storeType));
        boolean isTarget = strategy.isEnabledForUser(
            featureName,
            userId
        );
        return ResponseEntity.ok(isTarget);
    }

}
