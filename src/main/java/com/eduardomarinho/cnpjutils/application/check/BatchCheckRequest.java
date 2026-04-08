package com.eduardomarinho.cnpjutils.application.check;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BatchCheckRequest(@NotNull(message = "Lista de CNPJs não pode ser nula") @NotEmpty(message = "Lista de CNPJs não pode ser vazia") List<String> cnpjs) {}
