package com.eduardomarinho.cnpjutils.application.validate;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.eduardomarinho.cnpjutils.response.StandardResponse;
import com.eduardomarinho.cnpjutils.response.ValidateResponse;
import com.eduardomarinho.cnpjutils.response.ValidateBatchResponse;
import com.eduardomarinho.cnpjutils.response.CnpjValidationResult;
import com.eduardomarinho.cnpjutils.application.validate.IsAllValidRequest;
import com.eduardomarinho.cnpjutils.application.validate.ValidateBatchRequest;

@RestController
@RequestMapping("/v1/api")
public class ValidateController {

    private final ValidateService validateService;

    public ValidateController(ValidateService validateService) {
        this.validateService = validateService;
    }

    @PostMapping("/validate")
    public ResponseEntity<StandardResponse<ValidateResponse>> validateCnpj(@Valid @RequestBody ValidateRequest request) {
        try {
            boolean isValid = validateService.validateCnpj(request.cnpj(), request.pattern());
            ValidateResponse response = new ValidateResponse(isValid);
            String message = isValid ? "CNPJ válido" : "CNPJ inválido";
            return ResponseEntity.ok(StandardResponse.success(response, message));
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    @PostMapping("/isAllValid")
    public ResponseEntity<StandardResponse<ValidateResponse>> isAllValid(@Valid @RequestBody IsAllValidRequest request) {
        try {
            boolean allValid = validateService.isAllValid(request.cnpjs(), request.pattern());
            ValidateResponse response = new ValidateResponse(allValid);
            String message = allValid ? "Todos os CNPJs são válidos" : "Nem todos os CNPJs são válidos";
            return ResponseEntity.ok(StandardResponse.success(response, message));
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    @PostMapping("/validateBatch")
    public ResponseEntity<StandardResponse<ValidateBatchResponse>> validateBatch(@Valid @RequestBody ValidateBatchRequest request) {
        try {
            ValidateBatchResponse response = validateService.validateBatch(request.cnpjs());
            return ResponseEntity.ok(StandardResponse.success(response, "Validação em lote concluída"));
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}
