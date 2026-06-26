package com.deliverytracker.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void index_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
    }

    @Test
    void create_shouldRedirectToTop() throws Exception {
        mockMvc.perform(post("/deliveries")
                .param("recipientName", "田中 一郎")
                .param("recipientAddress", "東京都千代田区1-1-1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }

    @Test
    void complete_shouldRedirectToTop() throws Exception {
        mockMvc.perform(post("/deliveries/1/complete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }
}
