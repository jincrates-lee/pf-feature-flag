package kr.co.petfriends.sample.domain;

public enum FeatureStore {
    PROPERTY,
    AMPLITUDE,
    REDIS,
    ;

    public static FeatureStore fromString(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("FeatureStore name is null or empty");
        }

        try {
            return FeatureStore.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid feature store: " + name);
        }
    }
}
