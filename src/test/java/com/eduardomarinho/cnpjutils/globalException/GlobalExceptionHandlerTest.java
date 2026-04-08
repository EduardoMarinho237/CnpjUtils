package com.eduardomarinho.cnpjutils.globalException;

import com.eduardomarinho.cnpjutils.response.StandardResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@DisplayName("Testes do GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Deve tratar IllegalArgumentException corretamente")
    void handleIllegalArgumentException_ShouldReturnBadRequest() throws Exception {
        String invalidRequest = "{\"cnpj\": \"invalid-cnpj\"}";
        
        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException corretamente")
    void handleMethodArgumentNotValidException_ShouldReturnBadRequest() throws Exception {
        String emptyRequest = "{}";
        
        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Deve tratar HttpMessageNotReadableException corretamente")
    void handleHttpMessageNotReadableException_ShouldReturnBadRequest() throws Exception {
        String malformedJson = "{\"cnpj\": \"12345678901234\""; // JSON malformado
        
        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Deve tratar MissingServletRequestParameterException corretamente")
    void handleMissingServletRequestParameterException_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve tratar exceções genéricas corretamente")
    void handleGenericException_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(post("/v1/api/nonexistent-endpoint")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isInternalServerError());
    }
}
