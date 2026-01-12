package com.semester4.customer_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO implements Serializable {
    private Long addressId;
    private String doorNo;

    private String street;

    private String city;

    private String state;

    private String zip;

    private String country;

    private CustomerDTO customer;
}
