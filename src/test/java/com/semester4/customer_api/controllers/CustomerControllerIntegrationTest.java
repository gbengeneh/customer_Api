package com.semester4.customer_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semester4.customer_api.dto.AddressDTO;
import com.semester4.customer_api.dto.CustomerDTO;
import com.semester4.customer_api.dto.FullNameDTO;
import com.semester4.customer_api.models.Customer;
import com.semester4.customer_api.models.FullName;
import com.semester4.customer_api.models.Gender;
import com.semester4.customer_api.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDTO validIndividualDTO;
    private CustomerDTO validCorporateDTO;
    private CustomerDTO invalidPhoneDTO;
    private CustomerDTO individualWithoutGenderDTO;

    @BeforeEach
    void setUp() {
        FullNameDTO fullNameDTO = FullNameDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .build();

        AddressDTO addressDTO = new AddressDTO(null, "123", "Main St", "City", "State", "12345", "Country", null);

        validIndividualDTO = CustomerDTO.builder()
                .accountNo(1234567890L)
                .fullName(fullNameDTO)
                .email("john.doe@example.com")
                .password("Password1")
                .phoneNumber("1234567890")
                .address(addressDTO)
                .accountType(CustomerDTO.AccountType.INDIVIDUAL)
                .gender(Gender.MALE)
                .build();

        validCorporateDTO = CustomerDTO.builder()
                .accountNo(1234567891L)
                .fullName(fullNameDTO)
                .email("corp@example.com")
                .password("Password1")
                .phoneNumber("1234567891")
                .address(addressDTO)
                .accountType(CustomerDTO.AccountType.CORPORATE)
                .build();

        invalidPhoneDTO = CustomerDTO.builder()
                .accountNo(1234567892L)
                .fullName(fullNameDTO)
                .email("invalid@example.com")
                .password("Password1")
                .phoneNumber("123") // Invalid
                .address(addressDTO)
                .accountType(CustomerDTO.AccountType.INDIVIDUAL)
                .gender(Gender.MALE)
                .build();

        individualWithoutGenderDTO = CustomerDTO.builder()
                .accountNo(1234567893L)
                .fullName(fullNameDTO)
                .email("nogender@example.com")
                .password("Password1")
                .phoneNumber("1234567893")
                .address(addressDTO)
                .accountType(CustomerDTO.AccountType.INDIVIDUAL)
                .gender(null) // Missing
                .build();
    }

    @Test
    void testCreateIndividualCustomer_Success() throws Exception {
        mockMvc.perform(post("/customers/v1.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIndividualDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.message").value("Customer created successfully"));
    }

    @Test
    void testCreateCorporateCustomer_Success() throws Exception {
        mockMvc.perform(post("/customers/v1.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCorporateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("corp@example.com"))
                .andExpect(jsonPath("$.message").value("Customer created successfully"));
    }

    @Test
    void testCreateCustomer_InvalidPhone() throws Exception {
        mockMvc.perform(post("/customers/v1.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPhoneDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid phone number format"));
    }

    @Test
    void testCreateIndividual_WithoutGender() throws Exception {
        mockMvc.perform(post("/customers/v1.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(individualWithoutGenderDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Gender is required for individual accounts"));
    }

    @Test
    void testGetAllCustomers() throws Exception {
        // First create a customer
        customerService.addCustomer(new com.semester4.customer_api.models.Customer(
                1234567890L,
                FullName.builder()
                        .firstName(validIndividualDTO.getFullName().getFirstName())
                        .lastName(validIndividualDTO.getFullName().getLastName())
                        .middleName(validIndividualDTO.getFullName().getMiddleName())
                        .build(),
                validIndividualDTO.getEmail(),
                validIndividualDTO.getPassword(),
                Long.parseLong(validIndividualDTO.getPhoneNumber())
        ));

        mockMvc.perform(get("/customers/v1.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetCustomerById_Success() throws Exception {
        // Create customer
        Customer customer = customerService.addCustomer(new com.semester4.customer_api.models.Customer(
                1234567890L,
                FullName.builder()
                        .firstName(validIndividualDTO.getFullName().getFirstName())
                        .lastName(validIndividualDTO.getFullName().getLastName())
                        .middleName(validIndividualDTO.getFullName().getMiddleName())
                        .build(),
                validIndividualDTO.getEmail(),
                validIndividualDTO.getPassword(),
                Long.parseLong(validIndividualDTO.getPhoneNumber())
        ));

        mockMvc.perform(get("/customers/v1.0/{accountNo}", customer.getAccountNo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(customer.getEmail()));
    }

    @Test
    void testGetCustomerById_NotFound() throws Exception {
        mockMvc.perform(get("/customers/v1.0/{accountNo}", 9999999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer with account number 9999999999 not found"));
    }

    @Test
    void testUpdateCustomer_Success() throws Exception {
        // Create customer
        Customer customer = customerService.addCustomer(new com.semester4.customer_api.models.Customer(
                1234567890L,
                FullName.builder()
                        .firstName(validIndividualDTO.getFullName().getFirstName())
                        .lastName(validIndividualDTO.getFullName().getLastName())
                        .middleName(validIndividualDTO.getFullName().getMiddleName())
                        .build(),
                validIndividualDTO.getEmail(),
                validIndividualDTO.getPassword(),
                Long.parseLong(validIndividualDTO.getPhoneNumber())
        ));

        mockMvc.perform(put("/customers/v1.0")
                        .param("accountNo", customer.getAccountNo().toString())
                        .param("newEmail", "updated@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("updated@example.com"));
    }

    @Test
    void testUpdateCustomer_NotFound() throws Exception {
        mockMvc.perform(put("/customers/v1.0")
                        .param("accountNo", "9999999999")
                        .param("newEmail", "updated@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not updated"));
    }

    @Test
    void testDeleteCustomer_Success() throws Exception {
        // Create customer
        Customer customer = customerService.addCustomer(new com.semester4.customer_api.models.Customer(
                1234567890L,
                FullName.builder()
                        .firstName(validIndividualDTO.getFullName().getFirstName())
                        .lastName(validIndividualDTO.getFullName().getLastName())
                        .middleName(validIndividualDTO.getFullName().getMiddleName())
                        .build(),
                validIndividualDTO.getEmail(),
                validIndividualDTO.getPassword(),
                Long.parseLong(validIndividualDTO.getPhoneNumber())
        ));

        mockMvc.perform(delete("/customers/v1.0")
                        .param("accountNo", customer.getAccountNo().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Customer deleted successfully"));
    }

    @Test
    void testDeleteCustomer_NotFound() throws Exception {
        mockMvc.perform(delete("/customers/v1.0")
                        .param("accountNo", "9999999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer with account number 9999999999 not found"));
    }
}
