package com.notification.management.config;

import com.notification.management.dto.QueueMessage;
import com.notification.management.service.NotificationConsumerProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Camel Route Builder for Notification Processing.
 */
@Component
public class NotificationRouteBuilder extends RouteBuilder {

        @Value("${notification.rabbitmq.exchange}")
        private String exchange;

        @Value("${notification.rabbitmq.routing-key}")
        private String routingKey;

        @Value("${notification.rabbitmq.queue}")
        private String queue;

        @Value("${notification.smtp.host}")
        private String smtpHost;

        @Value("${notification.smtp.port}")
        private String smtpPort;

        @Value("${notification.smtp.username}")
        private String smtpUser;

        @Value("${notification.smtp.password}")
        private String smtpPass;

        @Override
        public void configure() throws Exception {

                // Producer Route: direct -> RabbitMQ
                from("direct:sendToQueue")
                                .routeId("notification-producer")
                                .log("Sending notification message with Audit ID: ${header.auditId}")
                                .marshal().json(JsonLibrary.Jackson)
                                .toD("spring-rabbitmq:" + exchange + "?routingKey=" + routingKey
                                                + "&autoDeclare=true&exchangeType=direct");

                // Consumer Route: RabbitMQ -> Processor
                from("spring-rabbitmq:" + exchange + "?queues=" + queue + "&routingKey=" + routingKey
                                + "&autoDeclare=true&exchangeType=direct")
                                .routeId("notification-consumer")
                                .unmarshal().json(JsonLibrary.Jackson, QueueMessage.class)
                                .log("Received notification message for Template: ${body.templateName}")
                                .bean(NotificationConsumerProcessor.class, "process");

                // SMTP Route: Send email using Camel Mail
                from("direct:smtpSend")
                                .routeId("smtp-sender")
                                .log("Sending email to ${header.To} with subject ${header.Subject}")
                                .toD("smtp://" + smtpHost + ":" + smtpPort +
                                                "?username=" + smtpUser +
                                                "&password=" + smtpPass +
                                                "&mail.smtp.auth=true&mail.smtp.starttls.enable=true");
        }
}
