package com.example.deliverytracker.controller;

import com.example.deliverytracker.service.DeliveryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("deliveries", deliveryService.findAll());
        return "index";
    }

    @PostMapping("/deliveries")
    public String create(@RequestParam String recipientName,
                         @RequestParam String recipientAddress) {
        deliveryService.create(recipientName, recipientAddress);
        return "redirect:/";
    }

    @PostMapping("/deliveries/{id}/complete")
    public String complete(@PathVariable Long id) {
        deliveryService.markAsDelivered(id);
        return "redirect:/";
    }
}
