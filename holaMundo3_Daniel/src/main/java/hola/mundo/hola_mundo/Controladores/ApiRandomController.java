package hola.mundo.hola_mundo.controladores;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hola.mundo.hola_mundo.dto.Quote;
import hola.mundo.hola_mundo.dto.Value;

import java.util.Random;

@RestController
public class ApiRandomController {

    @GetMapping("/api/random")
    public Quote randomQuote() {
        int id = new Random().nextInt(100);
        return new Quote("success", new Value(id, "Really loving Spring Boot, makes stand alone Spring apps easy."));
    }

}
