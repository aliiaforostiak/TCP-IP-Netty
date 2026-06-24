import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NioClientTest {

    @Test
    void createOutboundBufferAppendsNewline() {
        ByteBuffer buffer = NioClient.createOutboundBuffer("Hello");

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        assertEquals("Hello\n", new String(bytes, StandardCharsets.UTF_8));
    }

    @Test
    void drainFramedMessagesExtractsMultipleLinesAndKeepsTail() {
        StringBuilder inbound = new StringBuilder("first\nsecond\npartial");

        List<String> messages = NioClient.drainFramedMessages(inbound);

        assertEquals(List.of("first", "second"), messages);
        assertEquals("partial", inbound.toString());
    }

    @Test
    void drainFramedMessagesRemovesCarriageReturnBeforeNewline() {
        StringBuilder inbound = new StringBuilder("hello\r\n");

        List<String> messages = NioClient.drainFramedMessages(inbound);

        assertEquals(List.of("hello"), messages);
        assertTrue(inbound.isEmpty());
    }
}
