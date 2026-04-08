package com.eduardomarinho.cnpjutils.shared;

public interface CnpjCalculatorWeights {
    
    int[] FIRST_DIGIT_WEIGHTS = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    int[] SECOND_DIGIT_WEIGHTS = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    
    static int[] getFirstWeights() {
        return FIRST_DIGIT_WEIGHTS.clone();
    }
    
    static int[] getSecondWeights() {
        return SECOND_DIGIT_WEIGHTS.clone();
    }
}
