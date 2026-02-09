package com.antoniogatfdez.holaMundo.Cotroladores;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HolaControlador {

    @GetMapping("/hola")
    public String holaMundo(
            @RequestParam(name = "nombre", required = false, defaultValue = "Mundo")
                              String nombre, Model modelo)
    {
        modelo.addAttribute("nombre", nombre);
        return "hola";
    }
}
