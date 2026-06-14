package pk.fg.pasir_gorka_filip.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String test() {
        return "Hello, World!";
    }

    @GetMapping("/api/info")
    public java.util.Map<String, String> getAppInfo() {
        java.util.Map<String, String> info = new java.util.HashMap<>();


        info.put("appName", "PASiR - Projekt Budżetowy");
        info.put("version", "1.0.0-PROD");
        info.put("message", "Połączenie z backendem przebiegło pomyślnie!");
        info.put("author", "Filip Górka");

        return info;
    }
}