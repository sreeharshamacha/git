package com.notification.management.config;

import com.notification.management.entity.Channel;
import com.notification.management.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ChannelRepository channelRepository;

    @Override
    public void run(String... args) throws Exception {
        seedChannels();
    }

    private void seedChannels() {
        if (channelRepository.count() == 0) {
            log.info("Seeding default channels...");
            List<Channel> channels = Arrays.asList(
                    Channel.builder().type("EMAIL").status("01").isDelete("N").build(),
                    Channel.builder().type("SMS").status("01").isDelete("N").build(),
                    Channel.builder().type("IN-APP").status("01").isDelete("N").build());
            channelRepository.saveAll(channels);
            log.info("Default channels seeded successfully.");
        } else {
            log.info("Channels already exist, skipping seeding.");
        }
    }
}
