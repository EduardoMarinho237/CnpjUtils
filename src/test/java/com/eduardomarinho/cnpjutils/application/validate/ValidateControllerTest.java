package com.eduardomarinho.cnpjutils.application.validate;

import com.eduardomarinho.cnpjutils.response.StandardResponse;
import com.eduardomarinho.cnpjutils.response.ValidateResponse;
import com.eduardomarinho.cnpjutils.response.ValidateBatchResponse;
import com.eduardomarinho.cnpjutils.response.CnpjValidationResult;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ValidateController.class)
@DisplayName("Testes do ValidateController")
class ValidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ValidateService validateService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Deve validar CNPJ com padrão NEW e ser válido")
    void validateCnpj_WhenNewPatternAndValid_ShouldReturnSuccess() throws Exception {
        ValidateRequest request = new ValidateRequest("12.345.678/0001-95", "NEW");
        
        when(validateService.validateCnpj(anyString(), anyString())).thenReturn(true);
        
        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.message").value("CNPJ válido"));
    }

    @Test
    @DisplayName("Deve validar CNPJ com padrão NEW e ser inválido")
    void validateCnpj_WhenNewPatternAndInvalid_ShouldReturnSuccess() throws Exception {
        ValidateRequest request = new ValidateRequest("12.345.678/0001-81", "NEW");
        
        when(validateService.validateCnpj(anyString(), anyString())).thenReturn(false);
        
        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(false))
                .andExpect(jsonPath("$.message").value("CNPJ inválido"));
    }

    @Test
    @DisplayName("Deve validar CNPJ com padrão OLD e ser válido")
    void validateCnpj_WhenOldPatternAndValid_ShouldReturnSuccess() throws Exception {
        ValidateRequest request = new ValidateRequest("12.345.678/0001-81", "OLD");
        
        when(validateService.validateCnpj(anyString(), anyString())).thenReturn(true);
        
        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.message").value("CNPJ válido"));
    }

    @Test
    @DisplayName("Deve validar CNPJ com padrão OLD e ser inválido")
    void validateCnpj_WhenOldPatternAndInvalid_ShouldReturnSuccess() throws Exception {
        ValidateRequest request = new ValidateRequest("12.345.678/0001-95", "OLD");
        
        when(validateService.validateCnpj(anyString(), anyString())).thenReturn(false);
        
        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(false))
                .andExpect(jsonPath("$.message").value("CNPJ inválido"));
    }

    @Test
    @DisplayName("Deve lançar exceção para padrão inválido no endpoint validate")
    void validateCnpj_WhenInvalidPattern_ShouldThrowException() throws Exception {
        ValidateRequest request = new ValidateRequest("12.345.678/0001-95", "INVALID");
        
        when(validateService.validateCnpj(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Padrão inválido. Use NEW ou OLD"));
        
        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção para CNPJ inválido no endpoint validate")
    void validateCnpj_WhenInvalidCnpj_ShouldThrowException() throws Exception {
        ValidateRequest request = new ValidateRequest("123.456.789-00", "NEW");
        
        when(validateService.validateCnpj(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("CNPJ inválido"));
        
        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ é nulo no endpoint validate")
    void validateCnpj_WhenNullCnpj_ShouldReturnBadRequest() throws Exception {
        ValidateRequest request = new ValidateRequest(null, "NEW");
        
        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pattern é nulo no endpoint validate")
    void validateCnpj_WhenNullPattern_ShouldReturnBadRequest() throws Exception {
        ValidateRequest request = new ValidateRequest("12.345.678/0001-95", null);
        
        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve validar CNPJ sem máscara no endpoint validate")
    void validateCnpj_WithUnmaskedCnpj_ShouldWork() throws Exception {
        ValidateRequest request = new ValidateRequest("12345678000195", "NEW");
        
        when(validateService.validateCnpj(anyString(), anyString())).thenReturn(true);
        
        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.message").value("CNPJ válido"));
    }

    @Test
    @DisplayName("Deve verificar se todos são válidos com padrão NEW")
    void isAllValid_WhenAllValidWithNewPattern_ShouldReturnTrue() throws Exception {
        List<String> cnpjs = Arrays.asList(
                "12.345.678/0001-95",
                "98.765.432/0001-10",
                "45.678.901/0001-23"
        );
        IsAllValidRequest request = new IsAllValidRequest("NEW", cnpjs);
        
        when(validateService.isAllValid(anyList(), anyString())).thenReturn(true);
        
        mockMvc.perform(post("/v1/api/isAllValid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.message").value("Todos os CNPJs são válidos"));
    }

    @Test
    @DisplayName("Deve verificar se não todos são válidos com padrão NEW")
    void isAllValid_WhenNotAllValidWithNewPattern_ShouldReturnFalse() throws Exception {
        List<String> cnpjs = Arrays.asList(
                "12.345.678/0001-95",
                "12.345.678/0001-81"
        );
        IsAllValidRequest request = new IsAllValidRequest("NEW", cnpjs);
        
        when(validateService.isAllValid(anyList(), anyString())).thenReturn(false);
        
        mockMvc.perform(post("/v1/api/isAllValid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(false))
                .andExpect(jsonPath("$.message").value("Nem todos os CNPJs são válidos"));
    }

    @Test
    @DisplayName("Deve verificar se todos são válidos com padrão OLD")
    void isAllValid_WhenAllValidWithOldPattern_ShouldReturnTrue() throws Exception {
        List<String> cnpjs = Arrays.asList(
                "12.345.678/0001-81",
                "98.765.432/0001-55",
                "45.678.901/0001-33"
        );
        IsAllValidRequest request = new IsAllValidRequest("OLD", cnpjs);
        
        when(validateService.isAllValid(anyList(), anyString())).thenReturn(true);
        
        mockMvc.perform(post("/v1/api/isAllValid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.message").value("Todos os CNPJs são válidos"));
    }

    @Test
    @DisplayName("Deve lançar exceção para padrão inválido no endpoint isAllValid")
    void isAllValid_WhenInvalidPattern_ShouldThrowException() throws Exception {
        List<String> cnpjs = Arrays.asList("12.345.678/0001-95");
        IsAllValidRequest request = new IsAllValidRequest("INVALID", cnpjs);
        
        when(validateService.isAllValid(anyList(), anyString()))
                .thenThrow(new IllegalArgumentException("Padrão inválido. Use NEW ou OLD"));
        
        mockMvc.perform(post("/v1/api/isAllValid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção para lista vazia no endpoint isAllValid")
    void isAllValid_WhenEmptyList_ShouldThrowException() throws Exception {
        List<String> cnpjs = Arrays.asList();
        IsAllValidRequest request = new IsAllValidRequest("NEW", cnpjs);
        
        when(validateService.isAllValid(anyList(), anyString()))
                .thenThrow(new IllegalArgumentException("Lista de CNPJs não pode ser vazia"));
        
        mockMvc.perform(post("/v1/api/isAllValid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pattern é nulo no endpoint isAllValid")
    void isAllValid_WhenNullPattern_ShouldReturnBadRequest() throws Exception {
        List<String> cnpjs = Arrays.asList("12.345.678/0001-95");
        IsAllValidRequest request = new IsAllValidRequest(null, cnpjs);
        
        mockMvc.perform(post("/v1/api/isAllValid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista de CNPJs é nula no endpoint isAllValid")
    void isAllValid_WhenNullCnpjs_ShouldReturnBadRequest() throws Exception {
        IsAllValidRequest request = new IsAllValidRequest("NEW", null);
        
        mockMvc.perform(post("/v1/api/isAllValid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve validar CNPJs em lote com sucesso")
    void validateBatch_WhenValidCnpjs_ShouldReturnSuccess() throws Exception {
        List<CnpjValidationRequest> validationRequests = Arrays.asList(
                new CnpjValidationRequest("12.345.678/0001-95", "NEW"),
                new CnpjValidationRequest("98.765.432/0001-10", "NEW"),
                new CnpjValidationRequest("12.345.678/0001-81", "OLD")
        );
        ValidateBatchRequest request = new ValidateBatchRequest(validationRequests);
        
        List<CnpjValidationResult> results = Arrays.asList(
                new CnpjValidationResult("12.345.678/0001-95", "NEW", true),
                new CnpjValidationResult("98.765.432/0001-10", "NEW", true),
                new CnpjValidationResult("12.345.678/0001-81", "OLD", true)
        );
        ValidateBatchResponse expectedResponse = new ValidateBatchResponse(results);
        
        when(validateService.validateBatch(anyList())).thenReturn(expectedResponse);
        
        mockMvc.perform(post("/v1/api/validateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.results.length()").value(3))
                .andExpect(jsonPath("$.data.results[0].cnpj").value("12.345.678/0001-95"))
                .andExpect(jsonPath("$.data.results[0].result").value(true))
                .andExpect(jsonPath("$.data.results[0].pattern").value("NEW"))
                .andExpect(jsonPath("$.message").value("Validação em lote concluída"));
    }

    @Test
    @DisplayName("Deve validar CNPJs em lote com inválidos")
    void validateBatch_WhenInvalidCnpjs_ShouldReturnSuccess() throws Exception {
        List<CnpjValidationRequest> validationRequests = Arrays.asList(
                new CnpjValidationRequest("12.345.678/0001-95", "NEW"),
                new CnpjValidationRequest("123.456.789-00", "NEW"),
                new CnpjValidationRequest("12.345.678/0001-81", "OLD")
        );
        ValidateBatchRequest request = new ValidateBatchRequest(validationRequests);
        
        List<CnpjValidationResult> results = Arrays.asList(
                new CnpjValidationResult("12.345.678/0001-95", "NEW", true),
                new CnpjValidationResult("123.456.789-00", null, false),
                new CnpjValidationResult("12.345.678/0001-81", "OLD", true)
        );
        ValidateBatchResponse expectedResponse = new ValidateBatchResponse(results);
        
        when(validateService.validateBatch(anyList())).thenReturn(expectedResponse);
        
        mockMvc.perform(post("/v1/api/validateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.results.length()").value(3))
                .andExpect(jsonPath("$.data.results[1].result").value(false))
                .andExpect(jsonPath("$.data.results[1].pattern").isEmpty())
                .andExpect(jsonPath("$.message").value("Validação em lote concluída"));
    }

    @Test
    @DisplayName("Deve validar CNPJs em lote com um único CNPJ")
    void validateBatch_WhenSingleCnpj_ShouldReturnSuccess() throws Exception {
        List<CnpjValidationRequest> validationRequests = Arrays.asList(
                new CnpjValidationRequest("12.345.678/0001-95", "NEW")
        );
        ValidateBatchRequest request = new ValidateBatchRequest(validationRequests);
        
        List<CnpjValidationResult> results = Arrays.asList(
                new CnpjValidationResult("12.345.678/0001-95", "NEW", true)
        );
        ValidateBatchResponse expectedResponse = new ValidateBatchResponse(results);
        
        when(validateService.validateBatch(anyList())).thenReturn(expectedResponse);
        
        mockMvc.perform(post("/v1/api/validateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.results.length()").value(1))
                .andExpect(jsonPath("$.message").value("Validação em lote concluída"));
    }

    @Test
    @DisplayName("Deve lançar exceção para lista vazia no endpoint validateBatch")
    void validateBatch_WhenEmptyList_ShouldThrowException() throws Exception {
        List<CnpjValidationRequest> validationRequests = Arrays.asList();
        ValidateBatchRequest request = new ValidateBatchRequest(validationRequests);
        
        when(validateService.validateBatch(anyList()))
                .thenThrow(new IllegalArgumentException("Lista de CNPJs não pode ser vazia"));
        
        mockMvc.perform(post("/v1/api/validateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista de CNPJs é nula no endpoint validateBatch")
    void validateBatch_WhenNullCnpjs_ShouldReturnBadRequest() throws Exception {
        ValidateBatchRequest request = new ValidateBatchRequest(null);
        
        mockMvc.perform(post("/v1/api/validateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve validar CNPJs em lote sem máscara")
    void validateBatch_WithUnmaskedCnpjs_ShouldReturnSuccess() throws Exception {
        List<CnpjValidationRequest> validationRequests = Arrays.asList(
                new CnpjValidationRequest("12345678000195", "NEW"),
                new CnpjValidationRequest("98765432000110", "NEW")
        );
        ValidateBatchRequest request = new ValidateBatchRequest(validationRequests);
        
        List<CnpjValidationResult> results = Arrays.asList(
                new CnpjValidationResult("12345678000195", "NEW", true),
                new CnpjValidationResult("98765432000110", "NEW", true)
        );
        ValidateBatchResponse expectedResponse = new ValidateBatchResponse(results);
        
        when(validateService.validateBatch(anyList())).thenReturn(expectedResponse);
        
        mockMvc.perform(post("/v1/api/validateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.results.length()").value(2))
                .andExpect(jsonPath("$.message").value("Validação em lote concluída"));
    }
}
