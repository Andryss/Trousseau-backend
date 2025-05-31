package ru.andryss.trousseau.service;

import java.io.FileInputStream;
import java.util.Optional;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.config.MessagingProperties;
import ru.andryss.trousseau.repository.NotificationSettingsRepository;

@Slf4j
@Service
@Profile("!functionalTest")
@RequiredArgsConstructor
public class RuntimeMessagingServiceImpl implements RuntimeMessagingService, InitializingBean {

    private final MessagingProperties properties;
    private final NotificationSettingsRepository notificationSettingsRepository;

    @Override
    public void afterPropertiesSet() {
        if (!properties.isEnabled()) {
            log.info("Runtime messaging disabled");
            return;
        }

        try (FileInputStream stream = new FileInputStream(properties.getFilepath())) {

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(stream))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            log.error("Error while initializing FCM", e);
            throw new RuntimeException(e);
        }
        log.info("FCM successfully initialized from options file");
    }

    @Override
    public void sendMessage(MessageInfo info) {
        if (!properties.isEnabled()) {
            log.debug("Runtime messaging disabled, skip sending message");
            return;
        }
        log.info("Sending runtime message to {}", info.receiver());

        Optional<String> tokenOptional = notificationSettingsRepository.findTokenByUserId(info.receiver());
        if (tokenOptional.isEmpty()) {
            log.info("Settings for user {} is empty", info.receiver());
            return;
        }
        String targetToken = tokenOptional.get();

        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(Notification.builder()
                        .setTitle(info.title())
                        .setBody(info.content())
                        .build()
                )
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Received response {}", response);
        } catch (Exception e) {
            log.error("Error while sending runtime message", e);
            throw new RuntimeException(e);
        }
    }
}
