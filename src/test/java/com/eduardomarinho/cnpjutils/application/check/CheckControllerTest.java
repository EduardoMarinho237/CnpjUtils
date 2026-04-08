package com.eduardomarinho.cnpjutils.application.check;

import com.eduardomarinho.cnpjutils.response.StandardResponse;
import com.eduardomarinho.cnpjutils.response.CheckResponse;
import com.eduardomarinho.cnpjutils.response.BatchCheckResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckController.class)
@DisplayName("Testes do CheckController")
class CheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckService checkService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Deve verificar padrão NEW do CNPJ com sucesso")
    void checkCnpj_WhenValidNewPattern_ShouldReturnSuccess() throws Exception {
        CheckRequest request = new CheckRequest("12.345.678/0001-95");
        String expectedPattern = "NEW";
        
        when(checkService.checkCnpjPattern(any(String.class))).thenReturn(expectedPattern);
        
        CheckResponse checkResponse = new CheckResponse(expectedPattern);
        StandardResponse<CheckResponse> expectedResponse = StandardResponse.success(checkResponse, "CNPJ verificado com sucesso");
        
        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pattern").value(expectedPattern))
                .andExpect(jsonPath("$.message").value("CNPJ verificado com sucesso"));
    }

    @Test
    @DisplayName("Deve verificar padrão OLD do CNPJ com sucesso")
    void checkCnpj_WhenValidOldPattern_ShouldReturnSuccess() throws Exception {
        CheckRequest request = new CheckRequest("12.345.678/0001-81");
        String expectedPattern = "OLD";
        
        when(checkService.checkCnpjPattern(any(String.class))).thenReturn(expectedPattern);
        
        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pattern").value(expectedPattern))
                .andExpect(jsonPath("$.message").value("CNPJ verificado com sucesso"));
    }

    @Test
    @DisplayName("Deve lançar exceção para CNPJ inválido no endpoint check")
    void checkCnpj_WhenInvalidCnpj_ShouldThrowException() throws Exception {
        CheckRequest request = new CheckRequest("123.456.789-00");
        
        when(checkService.checkCnpjPattern(any(String.class)))
                .thenThrow(new IllegalArgumentException("CNPJ inválido"));
        
        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar erro quando CNPJ é nulo no endpoint check")
    void checkCnpj_WhenNullCnpj_ShouldReturnBadRequest() throws Exception {
        CheckRequest request = new CheckRequest(null);
        
        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve verificar se todos são NEW com sucesso")
    void isAllNew_WhenAllNewPattern_ShouldReturnTrue() throws Exception {
        List<String> cnpjs = Arrays.asList(
                "12.345.678/0001-95",
                "98.765.432/0001-10",
                "45.678.901/0001-23"
        );
        BatchCheckRequest request = new BatchCheckRequest(cnpjs);
        BatchCheckResponse expectedBatchResponse = new BatchCheckResponse(true);
        
        when(checkService.isAllNew(anyList())).thenReturn(expectedBatchResponse);
        
        mockMvc.perform(post("/v1/api/isAllNew")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.result").value(true))
                .andExpect(jsonPath("$.message").value("Todos os CNPJs são do padrão NEW"));
    }

    @Test
    @DisplayName("Deve verificar se não todos são NEW")
    void isAllNew_WhenNotAllNewPattern_ShouldReturnFalse() throws Exception {
        List<String> cnpjs = Arrays.asList(
                "12.345.678/0001-95",
                "12.345.678/0001-81"
        );
        BatchCheckRequest request = new BatchCheckRequest(cnpjs);
        BatchCheckResponse expectedBatchResponse = new BatchCheckResponse(false);
        
        when(checkService.isAllNew(anyList())).thenReturn(expectedBatchResponse);
        
        mockMvc.perform(post("/v1/api/isAllNew")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.result").value(false))
                .andExpect(jsonPath("$.message").value("Nem todos os CNPJs são do padrão NEW"));
    }

    @Test
    @DisplayName("Deve verificar se todos são OLD com sucesso")
    void isAllOld_WhenAllOldPattern_ShouldReturnTrue() throws Exception {
        List<String> cnpjs = Arrays.asList(
                "12.345.678/0001-81",
                "98.765.432/0001-55",
                "45.678.901/0001-33"
        );
        BatchCheckRequest request = new BatchCheckRequest(cnpjs);
        BatchCheckResponse expectedBatchResponse = new BatchCheckResponse(true);
        
        when(checkService.isAllOld(anyList())).thenReturn(expectedBatchResponse);
        
        mockMvc.perform(post("/v1/api/isAllOld")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.result").value(true))
                .andExpect(jsonPath("$.message").value("Todos os CNPJs são do padrão OLD"));
    }

    @Test
    @DisplayName("Deve verificar se não todos são OLD")
    void isAllOld_WhenNotAllOldPattern_ShouldReturnFalse() throws Exception {
        List<String> cnpjs = Arrays.asList(
                "12.345.678/0001-95",
                "12.345.678/0001-81"
        );
        BatchCheckRequest request = new BatchCheckRequest(cnpjs);
        BatchCheckResponse expectedBatchResponse = new BatchCheckResponse(false);
        
        when(checkService.isAllOld(anyList())).thenReturn(expectedBatchResponse);
        
        mockMvc.perform(post("/v1/api/isAllOld")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.result").value(false))
                .andExpect(jsonPath("$.message").value("Nem todos os CNPJs são do padrão OLD"));
    }

    @Test
    @DisplayName("Deve lançar exceção para lista vazia no endpoint isAllNew")
    void isAllNew_WhenEmptyList_ShouldThrowException() throws Exception {
        List<String> cnpjs = Arrays.asList();
        BatchCheckRequest request = new BatchCheckRequest(cnpjs);
        
        when(checkService.isAllNew(anyList()))
                .thenThrow(new IllegalArgumentException("Lista de CNPJs não pode ser vazia"));
        
        mockMvc.perform(post("/v1/api/isAllNew")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção para lista vazia no endpoint isAllOld")
    void isAllOld_WhenEmptyList_ShouldThrowException() throws Exception {
        List<String> cnpjs = Arrays.asList();
        BatchCheckRequest request = new BatchCheckRequest(cnpjs);
        
        when(checkService.isAllOld(anyList()))
                .thenThrow(new IllegalArgumentException("Lista de CNPJs não pode ser vazia"));
        
        mockMvc.perform(post("/v1/api/isAllOld")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lidar com CNPJ com máscara no endpoint check")
    void checkCnpj_WithMaskedCnpj_ShouldWork() throws Exception {
        CheckRequest request = new CheckRequest("12.345.678/0001-95");
        String expectedPattern = "NEW";
        
        when(checkService.checkCnpjPattern(any(String.class))).thenReturn(expectedPattern);
        
        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pattern").value(expectedPattern));
    }

    @Test
    @DisplayName("Deve lidar com CNPJ sem máscara no endpoint check")
    void checkCnpj_WithUnmaskedCnpj_ShouldWork() throws Exception {
        CheckRequest request = new CheckRequest("12345678000195");
        String expectedPattern = "NEW";
        
        when(checkService.checkCnpjPattern(any(String.class))).thenReturn(expectedPattern);
        
        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pattern").value(expectedPattern));
    }
}
