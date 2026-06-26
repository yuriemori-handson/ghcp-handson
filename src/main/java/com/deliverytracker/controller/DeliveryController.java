package com.deliverytracker.controller;

import com.deliverytracker.service.DeliveryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String create(
        @RequestParam("recipientName") String recipientName,
        @RequestParam("recipientAddress") String recipientAddress,
        RedirectAttributes redirectAttributes
    ) {
        try {
            deliveryService.create(recipientName, recipientAddress);
            redirectAttributes.addFlashAttribute("successMessage", "配送を登録しました。");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "受取人名と届け先住所は必須です。");
        }
        return "redirect:/";
    }

    @PostMapping("/deliveries/{id}/complete")
    public String complete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            deliveryService.markAsDelivered(id);
            redirectAttributes.addFlashAttribute("successMessage", "配達完了に更新しました。");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "対象の配送が見つかりませんでした。");
        }
        return "redirect:/";
    }
}
