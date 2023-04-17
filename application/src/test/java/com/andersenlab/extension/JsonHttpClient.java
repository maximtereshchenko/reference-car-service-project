package com.andersenlab.extension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

public final class JsonHttpClient {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public HttpResponse<Void> delete(String uri) throws Exception {
        return httpClient.send(
                request(uri)
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.discarding()
        );
    }

    public <T> HttpResponse<T> post(String uri, Map<String, String> body, TypeReference<T> typeReference)
            throws Exception {
        return httpClient.send(
                request(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                        .build(),
                new JsonBodyHandler<>(objectMapper, typeReference)
        );
    }

    public HttpResponse<Void> post(String uri, Map<String, String> body) throws Exception {
        return httpClient.send(
                request(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                        .build(),
                HttpResponse.BodyHandlers.discarding()
        );
    }

    public <T> HttpResponse<T> get(String uri, TypeReference<T> typeReference) throws Exception {
        return httpClient.send(
                request(uri)
                        .GET()
                        .build(),
                new JsonBodyHandler<>(objectMapper, typeReference)
        );
    }

    public HttpResponse<Void> get(String uri) throws Exception {
        return httpClient.send(
                request(uri)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.discarding()
        );
    }

    private HttpRequest.Builder request(String uri) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + uri))
                .header("Content-Type", "application/json");
    }

    private static final class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {

        private final ObjectMapper objectMapper;
        private final TypeReference<T> typeReference;
        private final HttpResponse.BodyHandler<String> original = HttpResponse.BodyHandlers.ofString();

        private JsonBodyHandler(ObjectMapper objectMapper, TypeReference<T> typeReference) {
            this.objectMapper = objectMapper;
            this.typeReference = typeReference;
        }

        @Override
        public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
            return new JsonBodySubscriber<>(objectMapper, typeReference, original.apply(responseInfo));
        }

        private static final class JsonBodySubscriber<T> implements HttpResponse.BodySubscriber<T> {

            private final ObjectMapper objectMapper;
            private final TypeReference<T> typeReference;
            private final HttpResponse.BodySubscriber<String> original;

            private JsonBodySubscriber(
                    ObjectMapper objectMapper,
                    TypeReference<T> typeReference,
                    HttpResponse.BodySubscriber<String> original
            ) {
                this.objectMapper = objectMapper;
                this.typeReference = typeReference;
                this.original = original;
            }

            @Override
            public CompletionStage<T> getBody() {
                return original.getBody()
                        .thenApply(this::json);
            }

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                original.onSubscribe(subscription);
            }

            @Override
            public void onNext(List<ByteBuffer> item) {
                original.onNext(item);
            }

            @Override
            public void onError(Throwable throwable) {
                original.onError(throwable);
            }

            @Override
            public void onComplete() {
                original.onComplete();
            }

            private T json(String raw) {
                try {
                    return objectMapper.readValue(raw, typeReference);
                } catch (JsonProcessingException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }
}
