package com.eduardomarinho.cnpjutils.integration;

import com.eduardomarinho.cnpjutils.application.validate.CnpjValidationRequest;
import com.eduardomarinho.cnpjutils.application.validate.ValidateBatchRequest;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@DisplayName("Testes de Integração da API")
class ApiIntegrationTest {

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
    @DisplayName("Deve executar fluxo completo: gerar -> verificar -> validar CNPJ")
    void completeWorkflow_GenerateCheckValidate_ShouldWork() throws Exception {
        // 1. Gerar CNPJ NEW com máscara
        Map<String, Object> generateRequest = new HashMap<>();
        generateRequest.put("pattern", "NEW");
        generateRequest.put("mask", true);

        String generateResponse = mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(generateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cnpj").exists())
                .andReturn().getResponse().getContentAsString();

        // Extrair CNPJ gerado
        String generatedCnpj = objectMapper.readTree(generateResponse)
                .get("data").get("cnpj").asText();

        // 2. Verificar padrão do CNPJ gerado
        Map<String, String> checkRequest = new HashMap<>();
        checkRequest.put("cnpj", generatedCnpj);

        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pattern").value("NEW"));

        // 3. Validar CNPJ gerado
        Map<String, String> validateRequest = new HashMap<>();
        validateRequest.put("cnpj", generatedCnpj);
        validateRequest.put("pattern", "NEW");

        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true));
    }

    @Test
    @DisplayName("Deve executar fluxo completo: gerar lote -> verificar todos -> validar lote")
    void completeBatchWorkflow_GenerateBatchCheckValidateBatch_ShouldWork() throws Exception {
        // 1. Gerar CNPJs em lote
        Map<String, Object> generateBatchRequest = new HashMap<>();
        generateBatchRequest.put("pattern", "NEW");
        generateBatchRequest.put("mask", true);
        generateBatchRequest.put("quantity", 3);

        String generateBatchResponse = mockMvc.perform(post("/v1/api/generateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(generateBatchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cnpjs").isArray())
                .andExpect(jsonPath("$.data.cnpjs.length()").value(3))
                .andReturn().getResponse().getContentAsString();

        // Extrair CNPJs gerados
        String[] generatedCnpjs = objectMapper.readTree(generateBatchResponse)
                .get("data").get("cnpjs")
                .toString()
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .split(",");

        // 2. Verificar se todos são NEW
        Map<String, Object> isAllNewRequest = new HashMap<>();
        isAllNewRequest.put("cnpjs", Arrays.asList(generatedCnpjs));

        mockMvc.perform(post("/v1/api/isAllNew")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(isAllNewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.result").value(true));

        // 3. Validar todos em lote
        List<CnpjValidationRequest> validationRequests = Arrays.asList(
                new CnpjValidationRequest(generatedCnpjs[0], "NEW"),
                new CnpjValidationRequest(generatedCnpjs[1], "NEW"),
                new CnpjValidationRequest(generatedCnpjs[2], "NEW")
        );
        ValidateBatchRequest validateBatchRequest = new ValidateBatchRequest(validationRequests);

        mockMvc.perform(post("/v1/api/validateBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateBatchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.results.length()").value(3))
                .andExpect(jsonPath("$.data.results[0].result").value(true))
                .andExpect(jsonPath("$.data.results[0].pattern").value("NEW"));
    }

    @Test
    @DisplayName("Deve gerar e validar CNPJs com ambos os padrões")
    void generateAndValidateBothPatterns_ShouldWork() throws Exception {
        // Gerar CNPJ NEW
        Map<String, Object> generateNewRequest = new HashMap<>();
        generateNewRequest.put("pattern", "NEW");
        generateNewRequest.put("mask", false);

        String newCnpjResponse = mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(generateNewRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String newCnpj = objectMapper.readTree(newCnpjResponse)
                .get("data").get("cnpj").asText();

        // Gerar CNPJ OLD
        Map<String, Object> generateOldRequest = new HashMap<>();
        generateOldRequest.put("pattern", "OLD");
        generateOldRequest.put("mask", false);

        String oldCnpjResponse = mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(generateOldRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String oldCnpj = objectMapper.readTree(oldCnpjResponse)
                .get("data").get("cnpj").asText();

        // Verificar padrões
        Map<String, String> checkNewRequest = new HashMap<>();
        checkNewRequest.put("cnpj", newCnpj);

        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkNewRequest)))
                .andExpect(jsonPath("$.data.pattern").value("NEW"));

        Map<String, String> checkOldRequest = new HashMap<>();
        checkOldRequest.put("cnpj", oldCnpj);

        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkOldRequest)))
                .andExpect(jsonPath("$.data.pattern").value("OLD"));

        // Validar com padrões corretos
        Map<String, String> validateNewRequest = new HashMap<>();
        validateNewRequest.put("cnpj", newCnpj);
        validateNewRequest.put("pattern", "NEW");

        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateNewRequest)))
                .andExpect(jsonPath("$.data.valid").value(true));

        Map<String, String> validateOldRequest = new HashMap<>();
        validateOldRequest.put("cnpj", oldCnpj);
        validateOldRequest.put("pattern", "OLD");

        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateOldRequest)))
                .andExpect(jsonPath("$.data.valid").value(true));
    }

    @Test
    @DisplayName("Deve lidar com validação cruzada de padrões")
    void crossPatternValidation_ShouldReturnInvalid() throws Exception {
        // Gerar CNPJ NEW
        Map<String, Object> generateNewRequest = new HashMap<>();
        generateNewRequest.put("pattern", "NEW");
        generateNewRequest.put("mask", false);

        String newCnpjResponse = mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(generateNewRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String newCnpj = objectMapper.readTree(newCnpjResponse)
                .get("data").get("cnpj").asText();

        // Tentar validar CNPJ NEW com padrão OLD (deve ser inválido)
        Map<String, String> validateWrongPatternRequest = new HashMap<>();
        validateWrongPatternRequest.put("cnpj", newCnpj);
        validateWrongPatternRequest.put("pattern", "OLD");

        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateWrongPatternRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(false))
                .andExpect(jsonPath("$.message").value("CNPJ inválido"));
    }

    @Test
    @DisplayName("Deve validar cenários de erro em todos os endpoints")
    void errorScenarios_ShouldReturnBadRequest() throws Exception {
        // Testar CNPJ inválido no check
        Map<String, String> invalidCheckRequest = new HashMap<>();
        invalidCheckRequest.put("cnpj", "invalid-cnpj");

        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCheckRequest)))
                .andExpect(status().isBadRequest());

        // Testar padrão inválido no generate
        Map<String, Object> invalidGenerateRequest = new HashMap<>();
        invalidGenerateRequest.put("pattern", "INVALID");
        invalidGenerateRequest.put("mask", true);

        mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidGenerateRequest)))
                .andExpect(status().isBadRequest());

        // Testar padrão inválido no validate
        Map<String, String> invalidValidateRequest = new HashMap<>();
        invalidValidateRequest.put("cnpj", "12.345.678/0001-95");
        invalidValidateRequest.put("pattern", "INVALID");

        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidValidateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lidar com requisições malformadas")
    void malformedRequests_ShouldReturnBadRequest() throws Exception {
        // JSON malformado
        mockMvc.perform(post("/v1/api/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cnpj\": \"12345678901234\""))
                .andExpect(status().isBadRequest());

        // Request body vazio
        mockMvc.perform(post("/v1/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        // Campos obrigatórios nulos
        mockMvc.perform(post("/v1/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
