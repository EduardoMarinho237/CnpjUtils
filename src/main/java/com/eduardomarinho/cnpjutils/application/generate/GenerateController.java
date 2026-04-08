package com.eduardomarinho.cnpjutils.application.generate;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.eduardomarinho.cnpjutils.response.StandardResponse;
import com.eduardomarinho.cnpjutils.response.GenerateResponse;
import com.eduardomarinho.cnpjutils.response.BatchGenerateResponse;
import com.eduardomarinho.cnpjutils.application.generate.BatchGenerateRequest;

@RestController
@RequestMapping("/v1/api")
public class GenerateController {

    private final GenerateService generateService;

    public GenerateController(GenerateService generateService) {
        this.generateService = generateService;
    }

    @PostMapping("/generate")
    public ResponseEntity<StandardResponse<GenerateResponse>> generateCnpj(@Valid @RequestBody GenerateRequest request) {
        try {
            String cnpj = generateService.generateCnpj(request.pattern(), request.mask());
            GenerateResponse response = new GenerateResponse(cnpj);
            return ResponseEntity.ok(StandardResponse.success(response, "CNPJ gerado com sucesso"));
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    @PostMapping("/generateBatch")
    public ResponseEntity<StandardResponse<BatchGenerateResponse>> generateBatchCnpjs(@Valid @RequestBody BatchGenerateRequest request) {
        try {
            BatchGenerateResponse response = generateService.generateBatchCnpjs(request.pattern(), request.mask(), request.quantity());
            return ResponseEntity.ok(StandardResponse.success(response, "CNPJs gerados com sucesso"));
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}
