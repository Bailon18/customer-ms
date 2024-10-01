package com.paucar.customer_ms.exception;

public class CustomerHasActiveAccountsException extends RuntimeException {
    public CustomerHasActiveAccountsException(String message) {
        super(message);
    }
}
