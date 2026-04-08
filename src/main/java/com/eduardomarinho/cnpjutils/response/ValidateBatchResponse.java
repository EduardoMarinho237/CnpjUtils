package com.eduardomarinho.cnpjutils.response;

import java.util.List;

public record ValidateBatchResponse(List<CnpjValidationResult> results) {}
