package EmployeeManagementSystem.config;

/**
 * Bounded timeouts for synchronous Ollama inference requests. Zero or negative
 * timeouts would silently permit indefinite waits at the HTTP client level.
 * These are transport idle-read/connect limits, NOT a total response deadline.
 */
public final class AiOllamaTimeoutPolicy {
    private AiOllamaTimeoutPolicy() {}

    public static int connectMillis(int value) {
        if (value < 1000 || value > 30000)
            throw new IllegalArgumentException("Ollama connect timeout must be between 1000 and 30000 ms");
        return value;
    }

    public static int readMillis(int value) {
        if (value < 5000 || value > 300000)
            throw new IllegalArgumentException("Ollama read timeout must be between 5000 and 300000 ms");
        return value;
    }
}
