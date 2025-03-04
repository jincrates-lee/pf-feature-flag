package kr.co.petfriends.sample.infrastructure.external.amplitude;

import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class AmplitudeWebClientConfig {

    @Value("${api.amplitude.url}")
    private String baseUrl;

    @Value("${api.amplitude.secretKey}")
    private String secretKey;

    @Bean
    public WebClient amplitudeWebClient() {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer " + secretKey
            )
            .defaultHeader(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE
            )
            .defaultHeader(
                HttpHeaders.ACCEPT,
                MediaType.APPLICATION_JSON_VALUE
            )
            .clientConnector(reactiveClientHttpConnector())
            .codecs(ClientCodecConfigurer::defaultCodecs)
            .build();
    }

    private ClientHttpConnector reactiveClientHttpConnector() {
        final ConnectionProvider provider = ConnectionProvider.builder("amplitude-feature-flag")
            .build();

        final HttpClient httpClient = HttpClient.create(provider)
            .doOnConnected(it -> it.addHandlerLast(new ReadTimeoutHandler(
                30,
                TimeUnit.SECONDS
            )));

        return new ReactorClientHttpConnector(httpClient);
    }
}
