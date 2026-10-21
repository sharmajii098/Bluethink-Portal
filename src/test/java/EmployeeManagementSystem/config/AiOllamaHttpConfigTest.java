package EmployeeManagementSystem.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Runs with Spring dependencies in CI; never contacts the actual Ollama server. */
class AiOllamaHttpConfigTest {
    private final AiOllamaHttpConfig config = new AiOllamaHttpConfig();

    @Test void createsNonStreamingOllamaApiWithBoundedNetworkTimeouts() {
        assertNotNull(config.ollamaApi("http://127.0.0.1:11434", 5000, 120000));
        assertNotNull(config.ollamaApi("http://127.0.0.1:11434", 1000, 5000));
    }

    @Test void rejectsIndefiniteOrExtremeTimeoutsBeforeCreatingTransport() {
        assertThrows(IllegalArgumentException.class,
                () -> config.ollamaApi("http://127.0.0.1:11434", 0, 120000));
        assertThrows(IllegalArgumentException.class,
                () -> config.ollamaApi("http://127.0.0.1:11434", 5000, 0));
        assertThrows(IllegalArgumentException.class,
                () -> config.ollamaApi("http://127.0.0.1:11434", 30001, 120000));
        assertThrows(IllegalArgumentException.class,
                () -> config.ollamaApi("http://127.0.0.1:11434", 5000, 300001));
    }
}
