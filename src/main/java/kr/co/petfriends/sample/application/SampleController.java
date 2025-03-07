package kr.co.petfriends.sample.application;

import kr.co.petfriends.sample.domain.FeatureFlag;
import kr.co.petfriends.sample.domain.FeatureFlagFactory;
import kr.co.petfriends.sample.domain.FeatureStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/samples")
public class SampleController {

    private final FeatureFlagFactory featureFlagFactory;

    /**
     * 점진적 기능 출시 예제(random)
     */
    @GetMapping("/gradual-rollout")
    public String gradualRollout() {
        FeatureFlag strategy = featureFlagFactory.findStrategy(FeatureStore.AMPLITUDE);
        boolean isFeatureEnabled = strategy.isFeatureEnabledByPercentage("gradual-rollout-flag-test");

        String result = isFeatureEnabled ? "new feature" : "legacy feature";

        return String.format(
            "gradual-rollout target: %s",
            result
        );
    }

    /**
     * 플래그 활성화 여부 확인
     */
    @GetMapping("/check")
    public String checkFeatureFlag(
        @RequestParam String featureName
    ) {
        FeatureFlag strategy = featureFlagFactory.findStrategy(FeatureStore.AMPLITUDE);
        boolean isEnabled = strategy.isEnabled(featureName);
        return isEnabled ? "enabled" : "disabled";
    }
}
