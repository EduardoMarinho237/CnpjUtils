package com.eduardomarinho.cnpjutils.application.validate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record IsAllValidRequest(@NotBlank(message = "Pattern não pode ser vazio") String pattern, @NotNull(message = "Lista de CNPJs não pode ser nula") @NotEmpty(message = "Lista de CNPJs não pode ser vazia") List<String> cnpjs) {}
