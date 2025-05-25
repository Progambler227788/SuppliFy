package com.supplify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/favicon.ico")
    public String redirectFavicon() {
        return "redirect:/static/images/default-favicon.ico"; // Redirect to a static image or placeholder
    }
}
