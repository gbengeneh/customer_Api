package com.semester4.customer_api.exceptions;

public class CustomerNotFoundException extends RuntimeException
{
    public CustomerNotFoundException(String message)
    {
        super(message);
    }
}
