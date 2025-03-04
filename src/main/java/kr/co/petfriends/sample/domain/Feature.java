package kr.co.petfriends.sample.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Builder;

@Builder
public record Feature(
    boolean enabled,
    List<HashMap<String, Object>> variants
) {

    public Feature {
        variants = (variants == null) ? new ArrayList<>() : variants;
    }
}
