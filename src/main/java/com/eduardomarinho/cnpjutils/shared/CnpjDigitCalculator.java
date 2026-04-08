package com.eduardomarinho.cnpjutils.shared;

public interface CnpjDigitCalculator {
    
    static int calculateDigit(String base, int[] weights) {
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
    
    static String calculateCheckDigits(String base) {
        int firstDigit = calculateDigit(base, CnpjCalculatorWeights.getFirstWeights());
        String baseWithFirst = base + firstDigit;
        int secondDigit = calculateDigit(baseWithFirst, CnpjCalculatorWeights.getSecondWeights());
        return String.valueOf(firstDigit) + secondDigit;
    }
}
