package hu.bgy.pokerapp.exceptions;

import java.util.List;

public class ValidationException extends Exception {
    public ValidationException (List<String> massages) {
        super(String.valueOf(massages));
    }
}
