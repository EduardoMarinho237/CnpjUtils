package com.eduardomarinho.cnpjutils.application.check;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.eduardomarinho.cnpjutils.response.StandardResponse;
import com.eduardomarinho.cnpjutils.response.CheckResponse;
import com.eduardomarinho.cnpjutils.response.BatchCheckResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/api")
@Tag(name = "CNPJ Check", description = "Endpoints para verificação de CNPJ")
public class CheckController {

    private final CheckService checkService;

    public CheckController(CheckService checkService) {
        this.checkService = checkService;
    }

    @Operation(summary = "Verificar padrão do CNPJ", description = "Verifica se um CNPJ é do padrão NEW ou OLD")
    @PostMapping("/check")
    public ResponseEntity<StandardResponse<CheckResponse>> checkCnpj(@Valid @RequestBody CheckRequest request) {
        try {
            String pattern = checkService.checkCnpjPattern(request.cnpj());
            CheckResponse response = new CheckResponse(pattern);
            return ResponseEntity.ok(StandardResponse.success(response, "CNPJ verificado com sucesso"));
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    @Operation(summary = "Verificar se todos são NEW", description = "Verifica se todos os CNPJs da lista são do padrão NEW")
    @PostMapping("/isAllNew")
    public ResponseEntity<StandardResponse<BatchCheckResponse>> isAllNew(@Valid @RequestBody BatchCheckRequest request) {
        try {
            BatchCheckResponse response = checkService.isAllNew(request.cnpjs());
            String message = response.result() ? "Todos os CNPJs são do padrão NEW" : "Nem todos os CNPJs são do padrão NEW";
            return ResponseEntity.ok(StandardResponse.success(response, message));
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    @Operation(summary = "Verificar se todos são OLD", description = "Verifica se todos os CNPJs da lista são do padrão OLD")
    @PostMapping("/isAllOld")
    public ResponseEntity<StandardResponse<BatchCheckResponse>> isAllOld(@Valid @RequestBody BatchCheckRequest request) {
        try {
            BatchCheckResponse response = checkService.isAllOld(request.cnpjs());
            String message = response.result() ? "Todos os CNPJs são do padrão OLD" : "Nem todos os CNPJs são do padrão OLD";
            return ResponseEntity.ok(StandardResponse.success(response, message));
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}
