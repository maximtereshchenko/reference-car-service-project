package com.andersenlab;

import com.andersenlab.extension.ArtemisExtension;
import com.andersenlab.extension.PostgreSqlExtension;
import com.andersenlab.extension.PredictableUUIDExtension;
import jakarta.jms.JMSException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("artemis")
@ExtendWith({PredictableUUIDExtension.class, PostgreSqlExtension.class, ArtemisExtension.class})
final class ArtemisIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Value("${car-service.artemis.queue}")
    private String queue;

    @Test
    void publishNewOrderId(UUID orderId) throws JMSException {
        var response = restTemplate.postForEntity(
                "/orders",
                Map.of(
                        "id", orderId.toString(),
                        "price", "100"
                ),
                UUID.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(orderId);
        assertThat(jmsTemplate.receive(queue).getBody(UUID.class)).isEqualTo(orderId);
    }
}
