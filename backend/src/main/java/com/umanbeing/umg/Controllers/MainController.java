package com.umanbeing.umg.Controllers;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MainController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

}
