package kr.co.petfriends.sample.infrastructure.aop;

import kr.co.petfriends.sample.domain.FeatureFlag;
import kr.co.petfriends.sample.domain.FeatureFlagEnabled;
import kr.co.petfriends.sample.domain.FeatureFlagFactory;
import kr.co.petfriends.sample.domain.FeatureStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class FeatureFlagAspect {

    private final FeatureFlagFactory factory;

    @Around("@annotation(featureFlagEnabled)")
    public Mono<Object> checkFeatureFlag(
        ProceedingJoinPoint joinPoint,
        FeatureFlagEnabled featureFlagEnabled
    ) {
        String featureName = featureFlagEnabled.value();
        FeatureFlag strategy = factory.findStrategy(FeatureStore.AMPLITUDE);

        // 비동기적으로 피쳐 플래그 활성화 여부 체크
        return strategy.isEnabled(featureName)
            .flatMap(isEnabled -> {
                if (isEnabled) {
                    // 피쳐 플래그가 활성화되었으면 원래 메서드 실행
                    return Mono.fromCallable(() -> {
                        try {
                            return joinPoint.proceed();
                        } catch (Throwable e) {
                            log.error(
                                e.getMessage(),
                                e
                            );
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    // 비활성화된 경우 다른 처리를 하거나 기본값을 반환
                    return Mono.just("Feature is disabled!!!");
                }
            });
    }
}
