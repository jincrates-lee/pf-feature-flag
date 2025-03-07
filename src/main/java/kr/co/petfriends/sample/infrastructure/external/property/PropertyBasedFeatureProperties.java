package kr.co.petfriends.sample.infrastructure.external.property;

import java.util.Map;
import kr.co.petfriends.sample.domain.Feature;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "local")
public class PropertyBasedFeatureProperties {

    private Map<String, Feature> features;

    public Map<String, Feature> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Feature> features) {
        this.features = features;
    }

    // 편의 메서드
    public Feature getFeature(String name) {
        return features.get(name).withName(name);
    }
}
