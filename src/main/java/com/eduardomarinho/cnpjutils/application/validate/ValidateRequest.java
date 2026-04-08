package com.eduardomarinho.cnpjutils.application.validate;

import jakarta.validation.constraints.NotBlank;

public record ValidateRequest(@NotBlank(message = "CNPJ não pode ser vazio") String cnpj, @NotBlank(message = "Pattern não pode ser vazio") String pattern) {}
