package my.service.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PingController {

    @GetMapping("/")
    public Map<String, String> ping() {
        Map<String, String> res = new HashMap<>();
        res.put("Response", "Welcome to the Emporio Case API");
        return res;
    }


}
