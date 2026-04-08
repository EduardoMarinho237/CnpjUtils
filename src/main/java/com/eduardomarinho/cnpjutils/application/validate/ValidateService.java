package com.eduardomarinho.cnpjutils.application.validate;

import com.eduardomarinho.cnpjutils.shared.CnpjDigitCalculator;
import org.springframework.stereotype.Service;
import java.util.List;
import com.eduardomarinho.cnpjutils.response.ValidateBatchResponse;
import com.eduardomarinho.cnpjutils.response.CnpjValidationResult;
import com.eduardomarinho.cnpjutils.application.validate.CnpjValidationRequest;

@Service
public class ValidateService {

    public boolean validateCnpj(String cnpj, String pattern) {
        if (cnpj == null) {
            throw new IllegalArgumentException("CNPJ não pode ser nulo");
        }

        if (pattern == null || (!pattern.equalsIgnoreCase("NEW") && !pattern.equalsIgnoreCase("OLD"))) {
            throw new IllegalArgumentException("Pattern deve ser NEW ou OLD");
        }

        String cleanedCnpj = cleanCnpj(cnpj);
        
        if (!isValidLength(cleanedCnpj)) {
            return false;
        }

        if (!isValidFormat(cleanedCnpj)) {
            return false;
        }

        if (!hasValidCheckDigits(cleanedCnpj)) {
            return false;
        }

        if (isAllSameCharacters(cleanedCnpj)) {
            return false;
        }

        if (pattern.equalsIgnoreCase("NEW") && !hasLetters(cleanedCnpj)) {
            return false;
        }

        if (pattern.equalsIgnoreCase("OLD") && hasLetters(cleanedCnpj)) {
            return false;
        }

        String base = cleanedCnpj.substring(0, 12);
        String expectedCheckDigits = CnpjDigitCalculator.calculateCheckDigits(base);
        String actualCheckDigits = cleanedCnpj.substring(12, 14);

        return expectedCheckDigits.equals(actualCheckDigits);
    }

    private String cleanCnpj(String cnpj) {
        return cnpj.replaceAll("[.\\-/]", "").toUpperCase();
    }

    private boolean isValidLength(String cnpj) {
        return cnpj.length() == 14;
    }

    private boolean isValidFormat(String cnpj) {
        return cnpj.matches("[A-Z0-9]+");
    }

    private boolean hasValidCheckDigits(String cnpj) {
        String checkDigits = cnpj.substring(12);
        return checkDigits.matches("[0-9]{2}");
    }

    private boolean isAllSameCharacters(String cnpj) {
        return cnpj.chars().allMatch(ch -> ch == cnpj.charAt(0));
    }

    private boolean hasLetters(String cnpj) {
        return cnpj.chars().anyMatch(Character::isLetter);
    }

    public boolean isAllValid(List<String> cnpjs, String pattern) {
        if (cnpjs == null || cnpjs.isEmpty()) {
            throw new IllegalArgumentException("Lista de CNPJs não pode ser nula ou vazia");
        }

        if (pattern == null || (!pattern.equalsIgnoreCase("NEW") && !pattern.equalsIgnoreCase("OLD"))) {
            throw new IllegalArgumentException("Pattern deve ser NEW ou OLD");
        }

        return cnpjs.stream()
                .allMatch(cnpj -> validateSingleCnpj(cnpj, pattern));
    }

    public ValidateBatchResponse validateBatch(List<CnpjValidationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Lista de validações não pode ser nula ou vazia");
        }

        List<CnpjValidationResult> results = requests.stream()
                .map(request -> {
                    boolean isValid = validateSingleCnpj(request.cnpj(), request.pattern());
                    return new CnpjValidationResult(request.cnpj(), request.pattern(), isValid);
                })
                .toList();

        return new ValidateBatchResponse(results);
    }

    private boolean validateSingleCnpj(String cnpj, String pattern) {
        try {
            String cleanedCnpj = cleanCnpj(cnpj);
            
            if (!isValidLength(cleanedCnpj)) {
                return false;
            }

            if (!isValidFormat(cleanedCnpj)) {
                return false;
            }

            if (!hasValidCheckDigits(cleanedCnpj)) {
                return false;
            }

            if (isAllSameCharacters(cleanedCnpj)) {
                return false;
            }

            if (pattern.equalsIgnoreCase("NEW") && !hasLetters(cleanedCnpj)) {
                return false;
            }

            if (pattern.equalsIgnoreCase("OLD") && hasLetters(cleanedCnpj)) {
                return false;
            }

            String base = cleanedCnpj.substring(0, 12);
            String expectedCheckDigits = CnpjDigitCalculator.calculateCheckDigits(base);
            String actualCheckDigits = cleanedCnpj.substring(12, 14);

            return expectedCheckDigits.equals(actualCheckDigits);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
