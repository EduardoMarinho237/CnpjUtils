package com.eduardomarinho.cnpjutils.application.generate;

import com.eduardomarinho.cnpjutils.response.StandardResponse;
import com.eduardomarinho.cnpjutils.response.GenerateResponse;
import com.eduardomarinho.cnpjutils.response.BatchGenerateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenerateController.class)
@DisplayName("Testes do GenerateController")
class GenerateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenerateService generateService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Deve gerar CNPJ com padrão NEW e com máscara")
    void generateCnpj_WhenNewPatternWithMask_ShouldReturnSuccess() throws Exception {
        GenerateRequest request = new GenerateRequest("NEW", true);
        String expectedCnpj = "12.345.678/0001-95";
        
        when(generateService.generateCnpj(anyString(), anyBoolean())).thenReturn(expectedCnpj);
        
        mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cnpj").value(expectedCnpj))
                .andExpect(jsonPath("$.message").value("CNPJ gerado com sucesso"));
    }

    @Test
    @DisplayName("Deve gerar CNPJ com padrão NEW e sem máscara")
    void generateCnpj_WhenNewPatternWithoutMask_ShouldReturnSuccess() throws Exception {
        GenerateRequest request = new GenerateRequest("NEW", false);
        String expectedCnpj = "12345678000195";
        
        when(generateService.generateCnpj(anyString(), anyBoolean())).thenReturn(expectedCnpj);
        
        mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cnpj").value(expectedCnpj))
                .andExpect(jsonPath("$.message").value("CNPJ gerado com sucesso"));
    }

    @Test
    @DisplayName("Deve gerar CNPJ com padrão OLD e com máscara")
    void generateCnpj_WhenOldPatternWithMask_ShouldReturnSuccess() throws Exception {
        GenerateRequest request = new GenerateRequest("OLD", true);
        String expectedCnpj = "12.345.678/0001-81";
        
        when(generateService.generateCnpj(anyString(), anyBoolean())).thenReturn(expectedCnpj);
        
        mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cnpj").value(expectedCnpj))
                .andExpect(jsonPath("$.message").value("CNPJ gerado com sucesso"));
    }

    @Test
    @DisplayName("Deve gerar CNPJ com padrão OLD e sem máscara")
    void generateCnpj_WhenOldPatternWithoutMask_ShouldReturnSuccess() throws Exception {
        GenerateRequest request = new GenerateRequest("OLD", false);
        String expectedCnpj = "12345678000181";
        
        when(generateService.generateCnpj(anyString(), anyBoolean())).thenReturn(expectedCnpj);
        
        mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cnpj").value(expectedCnpj))
                .andExpect(jsonPath("$.message").value("CNPJ gerado com sucesso"));
    }

    @Test
    @DisplayName("Deve lançar exceção para padrão inválido no endpoint generate")
    void generateCnpj_WhenInvalidPattern_ShouldThrowException() throws Exception {
        GenerateRequest request = new GenerateRequest("INVALID", true);
        
        when(generateService.generateCnpj(anyString(), anyBoolean()))
                .thenThrow(new IllegalArgumentException("Padrão inválido. Use NEW ou OLD"));
        
        mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pattern é nulo no endpoint generate")
    void generateCnpj_WhenNullPattern_ShouldReturnBadRequest() throws Exception {
        GenerateRequest request = new GenerateRequest(null, true);
        
        mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve gerar CNPJ com mask nulo (default false)")
    void generateCnpj_WhenNullMask_ShouldReturnSuccess() throws Exception {
        GenerateRequest request = new GenerateRequest("NEW", null);
        
        when(generateService.generateCnpj(anyString(), any())).thenReturn("12.345.678/0001-95");
        
        mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Deve gerar CNPJs em lote com padrão NEW e com máscara")
    void generateBatchCnpjs_WhenNewPatternWithMask_ShouldReturnSuccess() throws Exception {
        BatchGenerateRequest request = new BatchGenerateRequest("NEW", true, 3);
        List<String> expectedCnpjs = Arrays.asList(
                "12.345.678/0001-95",
                "98.765.432/0001-10",
                "45.678.901/0001-23"
        );
        BatchGenerateResponse expectedResponse = new BatchGenerateResponse(expectedCnpjs);
        
        when(generateService.generateBatchCnpjs(anyString(), anyBoolean(), anyInt()))
                .thenReturn(expectedResponse);
        
        mockMvc.perform(post("/v1/api/generateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cnpjs").isArray())
                .andExpect(jsonPath("$.data.cnpjs.length()").value(3))
                .andExpect(jsonPath("$.data.cnpjs[0]").value("12.345.678/0001-95"))
                .andExpect(jsonPath("$.message").value("CNPJs gerados com sucesso"));
    }

    @Test
    @DisplayName("Deve gerar CNPJs em lote com padrão OLD e sem máscara")
    void generateBatchCnpjs_WhenOldPatternWithoutMask_ShouldReturnSuccess() throws Exception {
        BatchGenerateRequest request = new BatchGenerateRequest("OLD", false, 2);
        List<String> expectedCnpjs = Arrays.asList(
                "12345678000181",
                "98765432000155"
        );
        BatchGenerateResponse expectedResponse = new BatchGenerateResponse(expectedCnpjs);
        
        when(generateService.generateBatchCnpjs(anyString(), anyBoolean(), anyInt()))
                .thenReturn(expectedResponse);
        
        mockMvc.perform(post("/v1/api/generateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cnpjs").isArray())
                .andExpect(jsonPath("$.data.cnpjs.length()").value(2))
                .andExpect(jsonPath("$.data.cnpjs[0]").value("12345678000181"))
                .andExpect(jsonPath("$.message").value("CNPJs gerados com sucesso"));
    }

    @Test
    @DisplayName("Deve gerar CNPJs em lote com quantidade 1")
    void generateBatchCnpjs_WhenQuantityIsOne_ShouldReturnSuccess() throws Exception {
        BatchGenerateRequest request = new BatchGenerateRequest("NEW", true, 1);
        List<String> expectedCnpjs = Arrays.asList("12.345.678/0001-95");
        BatchGenerateResponse expectedResponse = new BatchGenerateResponse(expectedCnpjs);
        
        when(generateService.generateBatchCnpjs(anyString(), anyBoolean(), anyInt()))
                .thenReturn(expectedResponse);
        
        mockMvc.perform(post("/v1/api/generateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cnpjs").isArray())
                .andExpect(jsonPath("$.data.cnpjs.length()").value(1))
                .andExpect(jsonPath("$.message").value("CNPJs gerados com sucesso"));
    }

    @Test
    @DisplayName("Deve lançar exceção para quantidade negativa no endpoint generateBatch")
    void generateBatchCnpjs_WhenNegativeQuantity_ShouldThrowException() throws Exception {
        BatchGenerateRequest request = new BatchGenerateRequest("NEW", true, -1);
        
        when(generateService.generateBatchCnpjs(anyString(), anyBoolean(), anyInt()))
                .thenThrow(new IllegalArgumentException("Quantidade deve ser maior que zero"));
        
        mockMvc.perform(post("/v1/api/generateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção para quantidade zero no endpoint generateBatch")
    void generateBatchCnpjs_WhenZeroQuantity_ShouldThrowException() throws Exception {
        BatchGenerateRequest request = new BatchGenerateRequest("NEW", true, 0);
        
        when(generateService.generateBatchCnpjs(anyString(), anyBoolean(), anyInt()))
                .thenThrow(new IllegalArgumentException("Quantidade deve ser maior que zero"));
        
        mockMvc.perform(post("/v1/api/generateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção para padrão inválido no endpoint generateBatch")
    void generateBatchCnpjs_WhenInvalidPattern_ShouldThrowException() throws Exception {
        BatchGenerateRequest request = new BatchGenerateRequest("INVALID", true, 5);
        
        when(generateService.generateBatchCnpjs(anyString(), anyBoolean(), anyInt()))
                .thenThrow(new IllegalArgumentException("Padrão inválido. Use NEW ou OLD"));
        
        mockMvc.perform(post("/v1/api/generateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pattern é nulo no endpoint generateBatch")
    void generateBatchCnpjs_WhenNullPattern_ShouldReturnBadRequest() throws Exception {
        BatchGenerateRequest request = new BatchGenerateRequest(null, true, 5);
        
        mockMvc.perform(post("/v1/api/generateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve gerar CNPJs em lote com mask nulo (default false)")
    void generateBatchCnpjs_WhenNullMask_ShouldReturnSuccess() throws Exception {
        BatchGenerateRequest request = new BatchGenerateRequest("NEW", null, 5);
        
        List<String> expectedCnpjs = Arrays.asList("12.345.678/0001-95");
        BatchGenerateResponse expectedResponse = new BatchGenerateResponse(expectedCnpjs);
        
        when(generateService.generateBatchCnpjs(anyString(), any(), anyInt()))
                .thenReturn(expectedResponse);
        
        mockMvc.perform(post("/v1/api/generateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantity é nulo no endpoint generateBatch")
    void generateBatchCnpjs_WhenNullQuantity_ShouldReturnBadRequest() throws Exception {
        BatchGenerateRequest request = new BatchGenerateRequest("NEW", true, null);
        
        mockMvc.perform(post("/v1/api/generateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve gerar CNPJs em lote com grande quantidade")
    void generateBatchCnpjs_WhenLargeQuantity_ShouldReturnSuccess() throws Exception {
        BatchGenerateRequest request = new BatchGenerateRequest("NEW", false, 100);
        
        List<String> expectedCnpjs = Arrays.asList("12345678000195"); // Simplificado para o teste
        BatchGenerateResponse expectedResponse = new BatchGenerateResponse(expectedCnpjs);
        
        when(generateService.generateBatchCnpjs(anyString(), anyBoolean(), anyInt()))
                .thenReturn(expectedResponse);
        
        mockMvc.perform(post("/v1/api/generateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cnpjs").isArray())
                .andExpect(jsonPath("$.message").value("CNPJs gerados com sucesso"));
    }
}
