package hola.mundo.hola_mundo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

import hola.mundo.hola_mundo.dto.Quote;

@SpringBootApplication
public class HolaMundoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolaMundoApplication.class, args);
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @Profile("!test")
    public ApplicationRunner run(RestClient.Builder builder) {
        RestClient restClient = builder.baseUrl("http://localhost:8080").build();
        return args -> {
            try {
                Quote quote = restClient
                        .get().uri("/api/random")
                        .retrieve()
                        .body(Quote.class);
                System.out.println((quote.toString()));
            } catch (Exception e) {
                System.err.println(("No se pudo obtener la cita desde http://localhost:8080/api/random: {}" + e.toString()));
            }
        };
    }

}
