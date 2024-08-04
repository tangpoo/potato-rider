package com.potatorider.service;

import com.potatorider.repository.RelayRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.bytebuddy.build.Plugin.Engine.Target.Sink;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class RelayServiceTests {

    @InjectMocks
    private RelayService relayService;

    @Mock
    private RelayRepository relayRepository;

    @Test
    void stream_alert() {
        // Arrange
        String receiverId = "receiverId-1234";
        String relayRequestId = "requestId-1234";

        Map<String, Sinks.Many<String>> notAcceptedSinkMap = new ConcurrentHashMap<>();
        Sinks.Many<String> sink = Sinks.many().replay().all();
        notAcceptedSinkMap.put(receiverId, sink);

        try {
            var field = RelayService.class.getDeclaredField("notAcceptedSinkMap");
            field.setAccessible(true);
            field.set(relayService, notAcceptedSinkMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        sink.tryEmitNext(relayRequestId);

        // Act
        final Flux<ServerSentEvent<String>> result = relayService.streamAlert(receiverId);

        // Assert
        StepVerifier.create(result)
            .expectNextMatches(event -> {
                if (event == null) {
                    return false;
                }
                return relayRequestId.equals(event.data());
            })
            .thenCancel()
            .verify();
    }
}
