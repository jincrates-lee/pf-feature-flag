package kr.co.petfriends.sample.domain;

import reactor.core.publisher.Mono;

public interface FeatureFlag {

    FeatureStore getStrategyName();

    /**
     * 특정 Feature 정보를 조회한다.
     *
     * @param featureName 조회할 Feature의 이름
     * @return Mono<Feature> 해당 Feature 정보를 비동기적으로 반환 (없으면 Mono.empty())
     */
    Mono<Feature> getFeature(String featureName);

    /**
     * Feature가 활성화되어 있는지 확인한다.
     *
     * @param featureName 확인할 Feature의 이름
     * @return Mono<Boolean> 활성화 여부 (true: 활성화, false: 비활성화)
     */
    Mono<Boolean> isEnabled(String featureName);

    /**
     * 특정 사용자가 Feature를 사용할 수 있는지 확인한다.
     *
     * @param featureName 확인할 Feature의 이름
     * @param userId      사용자 ID
     * @return Mono<Boolean> 사용자가 해당 Feature를 사용할 수 있는지 여부
     */
    Mono<Boolean> isEnabledForUser(
        String featureName,
        Integer userId
    );

    /**
     * 특정 Feature의 Variant 값을 조회한다.
     *
     * @param featureName Feature 이름
     * @param variantName Variant 이름
     * @param classType   변환할 데이터 타입
     * @param <T>         반환 타입
     * @return Mono<T> Variant 값을 지정된 타입으로 변환하여 반환 (없으면 Mono.empty())
     */
    <T> Mono<T> getVariant(
        String featureName,
        String variantName,
        Class<T> classType
    );

    /**
     * Feature가 특정 비율(퍼센트)로 활성화되어 있는지 확인한다. (예: 30%의 사용자에게만 노출)
     *
     * @param featureName 확인할 Feature의 이름
     * @return Mono<Boolean> 비율에 따라 활성화 여부를 결정하여 반환
     */
    Mono<Boolean> isFeatureEnabledByPercentage(String featureName);

    /**
     * Feature Flag 상태에 따라 서로 다른 실행 로직을 수행한다.
     *
     * @param featureName   확인할 Feature의 이름
     * @param enableAction  Feature가 활성화된 경우 실행할 비동기 작업
     * @param disableAction Feature가 비활성화된 경우 실행할 비동기 작업
     * @param <T>           반환 타입
     * @return Mono<T> Feature 상태에 따라 적절한 작업을 수행한 결과
     */
    <T> Mono<T> executeFeature(
        String featureName,
        Mono<T> enableAction,
        Mono<T> disableAction
    );
}
