package com.eduardomarinho.cnpjutils.application.generate;

import com.eduardomarinho.cnpjutils.shared.CnpjDigitCalculator;
import org.springframework.stereotype.Service;
import java.util.Random;
import com.eduardomarinho.cnpjutils.response.BatchGenerateResponse;

@Service
public class GenerateService {

    private final Random random = new Random();
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 100;

    public String generateCnpj(String pattern, Boolean mask) {
        String effectivePattern = pattern != null ? pattern.toUpperCase() : "NEW";
        boolean shouldMask = mask != null ? mask : true;

        if (!effectivePattern.equals("NEW") && !effectivePattern.equals("OLD")) {
            throw new IllegalArgumentException("Pattern deve ser NEW ou OLD");
        }

        String cnpj;
        if (effectivePattern.equals("NEW")) {
            cnpj = generateNewCnpj();
        } else {
            cnpj = generateOldCnpj();
        }

        return shouldMask ? applyMask(cnpj) : cnpj;
    }

    private String generateNewCnpj() {
        String base;
        do {
            base = generateAlphanumericBase(12);
        } while (isAllSameCharacters(base));

        String checkDigits = CnpjDigitCalculator.calculateCheckDigits(base);
        return base + checkDigits;
    }

    private String generateOldCnpj() {
        String base;
        do {
            base = generateNumericBase(12);
        } while (isAllSameCharacters(base));

        String checkDigits = CnpjDigitCalculator.calculateCheckDigits(base);
        return base + checkDigits;
    }

    private String generateAlphanumericBase(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (random.nextBoolean()) {
                char letter = (char) (65 + random.nextInt(26)); // 65-90 = A-Z
                sb.append(letter);
            } else {
                // Dígitos 0-9
                sb.append(random.nextInt(10));
            }
        }
        return sb.toString();
    }

    private String generateNumericBase(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String calculateCheckDigits(String base) {
        int firstDigit = calculateDigit(base, getFirstWeights());
        String baseWithFirst = base + firstDigit;
        int secondDigit = calculateDigit(baseWithFirst, getSecondWeights());
        return String.valueOf(firstDigit) + secondDigit;
    }

    private int calculateDigit(String base, int[] weights) {
        int sum = 0;
        for (int i = 0; i < base.length(); i++) {
            char c = base.charAt(i);
            int value;
            
            if (Character.isDigit(c)) {
                value = c - '0';
            } else if (Character.isLetter(c)) {
                value = c - 48; // Conforme documentação: 'A' = 65 - 48 = 17
            } else {
                throw new IllegalArgumentException("Caractere inválido no cálculo");
            }
            
            sum += value * weights[i];
        }
        
        int remainder = sum % 11;
        return (remainder == 0 || remainder == 1) ? 0 : 11 - remainder;
    }

    private int[] getFirstWeights() {
        return new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    }

    private int[] getSecondWeights() {
        return new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    }

    private boolean isAllSameCharacters(String str) {
        return str.chars().allMatch(ch -> ch == str.charAt(0));
    }

    private String applyMask(String cnpj) {
        if (cnpj.length() != 14) {
            return cnpj;
        }
        return cnpj.substring(0, 2) + "." + 
               cnpj.substring(2, 5) + "." + 
               cnpj.substring(5, 8) + "/" + 
               cnpj.substring(8, 12) + "-" + 
               cnpj.substring(12, 14);
    }

    public BatchGenerateResponse generateBatchCnpjs(String pattern, Boolean mask, Integer quantity) {
        validateBatchGenerateRequest(pattern, quantity);
        
        String effectivePattern = pattern != null ? pattern.toUpperCase() : "NEW";
        boolean shouldMask = mask != null ? mask : true;
        
        return new BatchGenerateResponse(
            java.util.stream.IntStream.range(0, quantity)
                .mapToObj(i -> generateSingleCnpj(effectivePattern, shouldMask))
                .toList()
        );
    }

    private String generateSingleCnpj(String pattern, boolean shouldMask) {
        String cnpj;
        if (pattern.equals("NEW")) {
            cnpj = generateNewCnpj();
        } else {
            cnpj = generateOldCnpj();
        }
        
        return shouldMask ? applyMask(cnpj) : cnpj;
    }

    private void validateBatchGenerateRequest(String pattern, Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity não pode ser nulo");
        }
        
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("Quantity deve estar entre " + MIN_QUANTITY + " e " + MAX_QUANTITY);
        }
        
        if (pattern != null && !pattern.equalsIgnoreCase("NEW") && !pattern.equalsIgnoreCase("OLD")) {
            throw new IllegalArgumentException("Pattern deve ser NEW ou OLD");
        }
    }
}
