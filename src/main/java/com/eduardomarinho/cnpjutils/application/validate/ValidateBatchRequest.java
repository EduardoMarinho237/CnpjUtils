package com.eduardomarinho.cnpjutils.application.validate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ValidateBatchRequest(@NotNull(message = "Lista de validações não pode ser nula") @NotEmpty(message = "Lista de validações não pode ser vazia") List<CnpjValidationRequest> cnpjs) {}