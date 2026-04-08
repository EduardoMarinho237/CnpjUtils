package com.eduardomarinho.cnpjutils.application.generate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BatchGenerateRequest(@NotBlank(message = "Pattern não pode ser vazio") String pattern, Boolean mask, @NotNull(message = "Quantity não pode ser nulo") @Min(value = 1, message = "Quantity deve ser maior que zero") Integer quantity) {}
