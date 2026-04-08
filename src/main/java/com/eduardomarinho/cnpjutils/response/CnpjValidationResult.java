package com.eduardomarinho.cnpjutils.response;

public record CnpjValidationResult(String cnpj, String pattern, boolean result) {}
