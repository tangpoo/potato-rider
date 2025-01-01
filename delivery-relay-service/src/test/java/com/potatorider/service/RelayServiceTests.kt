package com.potatorider.service

import com.potatorider.repository.RelayRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.springframework.http.codec.ServerSentEvent
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.Many
import reactor.test.StepVerifier
import java.util.concurrent.ConcurrentHashMap

@ExtendWith(MockitoExtension::class)
class RelayServiceTests {

    private val relayRepository: RelayRepository = mock()
    private val relayService: RelayService = RelayService(relayRepository)

    @Test
    fun stream_alert() {
        // Arrange
        val receiverId = "receiverId-1234"
        val relayRequestId = "requestId-1234"

        val notAcceptedSinkMap: MutableMap<String, Many<String>> = ConcurrentHashMap()
        val sink = Sinks.many().replay().all<String>()
        notAcceptedSinkMap[receiverId] = sink

        try {
            val field = RelayService::class.java.getDeclaredField("notAcceptedSinkMap")
            field.isAccessible = true
            field[relayService] = notAcceptedSinkMap
        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }

        sink.tryEmitNext(relayRequestId)

        // Act
        val result = relayService.streamAlert(receiverId)

        // Assert
        StepVerifier.create(result).expectNextMatches { event: ServerSentEvent<String>? ->
                if (event == null) {
                    return@expectNextMatches false
                }
                relayRequestId == event.data()
            }.thenCancel().verify()
    }
}
