package com.semester4.customer_api.dto;

import com.semester4.customer_api.models.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO implements Serializable {
    protected Long accountNo;
    protected FullNameDTO fullName;

    @Email(message = "Enter valid mail address")
    protected  String email;
    @NotNull
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{5,20}$",message = "Password must have mininum one digit,one lowercase and one upper case")
    protected  String password;

    @Pattern(regexp = "^\\d{10,11}$",message = "Phone Number Should be 10 or 11 Digits")
    protected  String phoneNumber;

    protected AddressDTO address;
    protected AccountType accountType;
    protected Gender gender;

    public enum AccountType {
        INDIVIDUAL, CORPORATE
    }
}
