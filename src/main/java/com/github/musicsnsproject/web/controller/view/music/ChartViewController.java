package com.github.musicsnsproject.web.controller.view.music;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/music/chart")
public class ChartViewController {
    @GetMapping
    public String chart() {
        return "music/chart";
    }
}
