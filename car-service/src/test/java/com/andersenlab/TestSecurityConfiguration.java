package com.andersenlab;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@TestConfiguration
class TestSecurityConfiguration {

    static final String HUMAN_RESOURCES_SPECIALIST = "anna";
    static final String SALES_SPECIALIST = "alina";
    static final String OPERATIONAL_MANAGER = "noel";
    private static final String ADMINISTRATOR = "terry";

    @Bean
    RestTemplateBuilder restTemplateBuilder(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri
    ) {
        return new RestTemplateBuilder()
                .additionalInterceptors(new OidcClientHttpRequestInterceptor(issuerUri));
    }

    private static final class OidcClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

        private final RestOperations restTemplate = new RestTemplate();
        private final String issuerUri;

        private OidcClientHttpRequestInterceptor(String issuerUri) {
            this.issuerUri = issuerUri;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            var headers = request.getHeaders();
            var token = token(username(headers));
            headers.remove(HttpHeaders.AUTHORIZATION);
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            return execution.execute(request, body);
        }

        private String username(HttpHeaders headers) {
            if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                return headers.getFirst(HttpHeaders.AUTHORIZATION);
            }
            return ADMINISTRATOR;
        }

        private String token(String user) {
            return Objects.requireNonNull(
                            restTemplate.exchange(
                                            requestEntity(user),
                                            new ParameterizedTypeReference<Map<String, String>>() {}
                                    )
                                    .getBody()
                    )
                    .get("access_token");
        }

        private RequestEntity<LinkedMultiValueMap<String, String>> requestEntity(String user) {
            return RequestEntity.post(issuerUri + "/protocol/openid-connect/token")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(body(user));
        }

        private LinkedMultiValueMap<String, String> body(String user) {
            var body = new LinkedMultiValueMap<String, String>();
            body.add("username", user);
            body.add("password", user);
            body.add("grant_type", "password");
            body.add("client_id", "public");
            return body;
        }
    }
}
