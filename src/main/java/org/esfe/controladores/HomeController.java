package org.esfe.controladores;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String holaMundo() {
        return "¡Hola Mundo desde la api de equipos y jugadores";
    }
}