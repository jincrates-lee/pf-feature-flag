package kr.co.petfriends.sample.infrastructure.external.amplitude;

import java.util.Optional;
import kr.co.petfriends.sample.domain.Feature;
import kr.co.petfriends.sample.domain.FeatureFlag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
@Qualifier("AmplitudeFeatureFlag")
class AmplitudeFeatureFlag implements FeatureFlag {

    private final WebClient amplitudeWebClient;

    @Override
    public Optional<Feature> getFeature(String featureName) {
        // TODO: 단일 AmplitudeFeatureFlag만 조회할 수 있을까
        return amplitudeWebClient
            .get()
            .uri("/api/1/flags?key=" + featureName)
            .retrieve()
            .bodyToMono(AmplitudeFlagListResponse.class)
            .flatMap(response -> {
                log.info(
                    "Amplitude Response :: {}",
                    response
                );
                return Flux.fromIterable(response.flags())
                    .filter(flag -> featureName.equals(flag.key()))
                    .next()
                    .map(flag -> Feature.builder()
                        .enabled(flag.enabled())
                        .variants(flag.variants())
                        .build());
            })
            .defaultIfEmpty(Feature.defaultValue())
            .blockOptional();
    }

    @Override
    public boolean isEnabled(String featureName) {
        return false;
    }

    @Override
    public boolean isEnabledForUser(
        String featureName,
        Integer userId
    ) {
        return false;
    }
}
