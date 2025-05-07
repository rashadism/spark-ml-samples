package main.java.com.lohika.morning.ml.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/")
    public String serveFrontend() {
        return "forward:/index.html";
    }
}
