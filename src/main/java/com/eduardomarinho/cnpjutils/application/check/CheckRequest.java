package com.eduardomarinho.cnpjutils.application.check;

import jakarta.validation.constraints.NotBlank;

public record CheckRequest(@NotBlank(message = "CNPJ não pode ser vazio") String cnpj) {}
