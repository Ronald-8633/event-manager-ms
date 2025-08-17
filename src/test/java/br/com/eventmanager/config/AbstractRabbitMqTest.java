package br.com.eventmanager.config;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public abstract class AbstractRabbitMqTest {

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.12-management")
            .withExposedPorts(5672, 15672)
            .withVhost("/")
            .withUser("guest", "guest");

    @BeforeAll
    static void startContainer() {
        rabbit.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String virtualHost = "/";
        String address = "amqp://" + rabbit.getAdminUsername() + ":" + rabbit.getAdminPassword() +
                "@" + rabbit.getHost() + ":" + rabbit.getAmqpPort() + virtualHost;

        registry.add("spring.rabbitmq.addresses", () -> address);
        registry.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbit::getAdminPassword);
        registry.add("spring.rabbitmq.virtual-host", () -> virtualHost);
    }
}