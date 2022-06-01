package my.service.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @GetMapping("/")
    public Map<String, String> getAllReports() {
        Map<String, String> res = new HashMap<>();
        res.put("GetAll", "Report");
        return res;
    }

    @GetMapping("/{id}")
    public Map<String, String> getSingleReport() {
        Map<String, String> res = new HashMap<>();
        res.put("GetOne", "Report");
        return res;
    }

    @PostMapping("/")
    public Object postReport() {
        Map<String, String> res = new HashMap<>();
        res.put("Post", "Report");
        return res;
    }

    @PatchMapping("/{id}")
    public Object patchReport() {
        Map<String, String> res = new HashMap<>();
        res.put("Patch", "Report");
        return res;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteReport() {
        Map<String, String> res = new HashMap<>();
        res.put("Delete", "Report");
        return res;
    }

}
