package com.comedor.modelo.excepciones;

public class InvalidEmailFormatException extends Exception {
    public InvalidEmailFormatException(String message) {
        super(message);
    }
}
