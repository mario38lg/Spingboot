package hola.mundo.hola_mundo.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HolaControlador {

    @GetMapping("/hola")
    public void HolaMundo(
            @RequestParam(name = "nombre",required = false, defaultValue = "Mundo") String nombre,
            Model modelo
    ){
        modelo.addAttribute("nombre",nombre);
    }
}
