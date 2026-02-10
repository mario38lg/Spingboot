package hola.mundo.hola_mundo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Record que se devuelve en la petición REST como respuesta
@JsonIgnoreProperties(ignoreUnknown = true)
public record Quote(String type, Value value) { }
