package kr.co.petfriends.sample.application;

import kr.co.petfriends.sample.domain.FeatureFlag;
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

    private final FeatureFlag featureFlag;

    @GetMapping("/{featureName}")
    public ResponseEntity<?> getFeatureFlag(@PathVariable String featureName) {
        return featureFlag.getFeature(featureName)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{featureName}/{userId}")
    public ResponseEntity<?> getFeatureFlag(
        @PathVariable String featureName,
        @PathVariable Integer userId
    ) {
        boolean isTarget = featureFlag.isEnabledForUser(
            featureName,
            userId
        );
        return ResponseEntity.ok(isTarget);
    }

}
