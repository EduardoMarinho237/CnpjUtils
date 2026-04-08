package com.eduardomarinho.cnpjutils.application.check;

import org.springframework.stereotype.Service;
import java.util.List;
import com.eduardomarinho.cnpjutils.response.BatchCheckResponse;

@Service
public class CheckService {

    private static final int MAX_BATCH_SIZE = 100;

    public String checkCnpjPattern(String cnpj) {
        if (cnpj == null) {
            throw new IllegalArgumentException("CNPJ não pode ser nulo");
        }

        String cleanedCnpj = cleanCnpj(cnpj);
        
        validateCnpjFormat(cleanedCnpj);
        
        return hasLetters(cleanedCnpj) ? "NEW" : "OLD";
    }

    private void validateCnpjFormat(String cnpj) {
        if (!isValidLength(cnpj) || 
            !isValidFormat(cnpj) || 
            !hasValidCheckDigits(cnpj) || 
            isAllSameCharacters(cnpj)) {
            throw new IllegalArgumentException("Valor enviado não segue nenhum dos padrões de CNPJ");
        }
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

    private boolean isAllSameCharacters(String cnpj) {
        return cnpj.chars().allMatch(ch -> ch == cnpj.charAt(0));
    }

    private boolean hasValidCheckDigits(String cnpj) {
        String checkDigits = cnpj.substring(12);
        return checkDigits.matches("[0-9]{2}");
    }

    private boolean hasLetters(String cnpj) {
        return cnpj.chars().anyMatch(Character::isLetter);
    }

    public BatchCheckResponse isAllNew(List<String> cnpjs) {
        validateBatchRequest(cnpjs);
        
        boolean allNew = cnpjs.stream()
                .allMatch(cnpj -> {
                    try {
                        String cleanedCnpj = cleanCnpj(cnpj);
                        return hasLetters(cleanedCnpj);
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                });
        
        return new BatchCheckResponse(allNew);
    }

    public BatchCheckResponse isAllOld(List<String> cnpjs) {
        validateBatchRequest(cnpjs);
        
        boolean allOld = cnpjs.stream()
                .allMatch(cnpj -> {
                    try {
                        String cleanedCnpj = cleanCnpj(cnpj);
                        return !hasLetters(cleanedCnpj);
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                });
        
        return new BatchCheckResponse(allOld);
    }

    private void validateBatchRequest(List<String> cnpjs) {
        if (cnpjs == null || cnpjs.isEmpty()) {
            throw new IllegalArgumentException("Lista de CNPJs não pode ser nula ou vazia");
        }
        
        if (cnpjs.size() > MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Máximo permitido é " + MAX_BATCH_SIZE + " CNPJs por requisição");
        }
    }
}
