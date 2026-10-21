package EmployeeManagementSystem.config;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Ollama-only HTTP timeouts for the non-streaming ChatClient.call() path.
 * By supplying an OllamaApi bean, Spring AI's conditional OllamaApi
 * auto-configuration backs off. Other portal HTTP clients are not modified.
 * The app does not use Ollama streaming. If streaming is added in the future,
 * configure the separate WebClient transport as well.
 */
@Configuration
public class AiOllamaHttpConfig {

    @Bean
    public OllamaApi ollamaApi(
            @Value("${spring.ai.ollama.base-url:http://127.0.0.1:11434}") String baseUrl,
            @Value("${hrms.ai.ollama-connect-timeout-ms:5000}") int connectTimeoutMs,
            @Value("${hrms.ai.ollama-read-timeout-ms:120000}") int readTimeoutMs) {
        int connect = AiOllamaTimeoutPolicy.connectMillis(connectTimeoutMs);
        int read = AiOllamaTimeoutPolicy.readMillis(readTimeoutMs);
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connect);
        factory.setReadTimeout(read);
        // This RestClient is owned by the Ollama API; do not apply a global customizer.
        return OllamaApi.builder()
                .baseUrl(baseUrl)
                .restClientBuilder(RestClient.builder().requestFactory(factory))
                .build();
    }
}
