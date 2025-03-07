package kr.co.petfriends.sample.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Builder;

@Builder
public record Feature(
    String name,
    boolean enabled,
    List<HashMap<String, Object>> variants
) {

    public Feature {
        variants = (variants == null) ? new ArrayList<>() : variants;
    }

    public Feature withName(String name) {
        return Feature.builder()
            .name(name)
            .enabled(this.enabled)
            .variants(this.variants)
            .build();
    }
}
