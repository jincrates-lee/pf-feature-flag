package kr.co.petfriends.sample.application;

import kr.co.petfriends.sample.domain.FeatureFlag;
import kr.co.petfriends.sample.domain.FeatureFlagEnabled;
import kr.co.petfriends.sample.domain.FeatureFlagFactory;
import kr.co.petfriends.sample.domain.FeatureStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/samples")
public class SampleController {

    private final FeatureFlagFactory featureFlagFactory;

    /**
     * 점진적 기능 출시 예제(random)
     */
    @GetMapping("/gradual-rollout")
    public Mono<String> gradualRollout() {
        FeatureFlag strategy = featureFlagFactory.findStrategy(FeatureStore.AMPLITUDE);
        return strategy.isFeatureEnabledByPercentage("gradual-rollout-flag-test")
            .map(isFeatureEnabled -> String.format(
                "gradual-rollout target: %s",
                isFeatureEnabled ? "new feature" : "legacy feature"
            ));
    }

    /**
     * 플래그 활성화 여부 확인
     */
    @GetMapping("/check")
    public Mono<String> checkFeatureFlag(
        @RequestParam String featureName
    ) {
        FeatureFlag strategy = featureFlagFactory.findStrategy(FeatureStore.AMPLITUDE);
        return strategy.isEnabled(featureName)
            .map(isEnabled -> isEnabled ? "enabled" : "disabled");
    }

    /**
     * AOP 테스트
     */
    @GetMapping("aop-test-1")
    @FeatureFlagEnabled("flag-test-1")
    public Mono<String> aopTest1() {
        return Mono.just("Feature is enabled!");
    }

    @GetMapping("aop-test-2")
    @FeatureFlagEnabled("gradual-rollout-flag-test")
    public Mono<String> aopTest2() {
        return Mono.just("Feature is enabled!");
    }
}
