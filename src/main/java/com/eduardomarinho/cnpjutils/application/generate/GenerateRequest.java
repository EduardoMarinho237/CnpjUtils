package com.eduardomarinho.cnpjutils.application.generate;

import jakarta.validation.constraints.NotBlank;

public record GenerateRequest(@NotBlank(message = "Pattern não pode ser vazio") String pattern, Boolean mask) {}
