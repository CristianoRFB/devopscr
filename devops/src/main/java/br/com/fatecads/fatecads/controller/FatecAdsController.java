package br.com.fatecads.fatecads.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/fatecads")
public class FatecAdsController {
    
    @GetMapping
    public String index(Model model) {
        return "index";
    }
    
}
