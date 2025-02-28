package kr.co.petfriends.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class FeatureFlagApplication {

    public static void main(String[] args) {
        SpringApplication.run(
            FeatureFlagApplication.class,
            args
        );
    }
}
