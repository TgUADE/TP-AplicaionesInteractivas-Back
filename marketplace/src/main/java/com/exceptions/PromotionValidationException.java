package com.exceptions;

public class PromotionValidationException extends Exception {
    public PromotionValidationException() {
        super("Promotion validation failed");
    }
    
    public PromotionValidationException(String message) {
        super(message);
    }
}