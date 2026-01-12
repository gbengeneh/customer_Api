package com.semester4.customer_api.controllers;

import com.semester4.customer_api.dto.AddressDTO;
import com.semester4.customer_api.dto.CustomerDTO;
import com.semester4.customer_api.dto.ResponseWrapper;
import com.semester4.customer_api.models.*;
import com.semester4.customer_api.services.CustomerService;
import com.semester4.customer_api.models.Gender;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    private static final Logger logger = Logger.getLogger(CustomerController.class.getName());

    @PostMapping("/v1.0")
    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided details")
    public ResponseEntity<ResponseWrapper> saveCustomer(@Valid @RequestBody CustomerDTO customerDTO) {

        logger.info("Received request to create customer: " + customerDTO);

        long phoneNumber;
        try {
            phoneNumber = Long.parseLong(customerDTO.getPhoneNumber());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseWrapper<String>(null, "Invalid phone number format"));
        }

        logger.info("Parsed phone number: " + phoneNumber);

        FullName fullName = FullName.builder()
                .firstName(customerDTO.getFullName().getFirstName())
                .lastName(customerDTO.getFullName().getLastName())
                .middleName(customerDTO.getFullName().getMiddleName())
                .build();

        Customer savedCustomer = null;

        switch (customerDTO.getAccountType()) {

            case INDIVIDUAL -> {
                if (customerDTO.getGender() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseWrapper<>(null, "Gender is required for individual accounts"));
                }
                Individual individual = Individual.builder()
                        .accountNo(customerDTO.getAccountNo())
                        .fullName(fullName)
                        .email(customerDTO.getEmail())
                        .password(customerDTO.getPassword())
                        .phoneNumber(phoneNumber)
                        .gender(customerDTO.getGender())
                        .build();
                savedCustomer = customerService.addIndividual(individual);
            }

            case CORPORATE -> {
                Corporate corporate = Corporate.builder()
                        .accountNo(customerDTO.getAccountNo())
                        .fullName(fullName)
                        .email(customerDTO.getEmail())
                        .password(customerDTO.getPassword())
                        .phoneNumber(phoneNumber)
                        .build();
                savedCustomer = customerService.addCorporate(corporate);
            }

            default -> {
                Customer customer = Customer.builder()
                        .accountNo(customerDTO.getAccountNo())
                        .fullName(fullName)
                        .email(customerDTO.getEmail())
                        .password(customerDTO.getPassword())
                        .phoneNumber(phoneNumber)
                        .build();
                savedCustomer = customerService.addCustomer(customer);
            }
        }

        // Save address if provided
        if (savedCustomer != null && customerDTO.getAddress() != null) {
            AddressDTO addr = customerDTO.getAddress();

            Address address = new Address(
                    null,
                    addr.getDoorNo(),
                    addr.getStreet(),
                    addr.getCity(),
                    addr.getState(),
                    addr.getZip(),
                    addr.getCountry(),
                    savedCustomer
            );

            customerService.saveAddress(address);
        }

        if (savedCustomer != null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseWrapper<>(savedCustomer, "Customer created successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseWrapper<>(null, "Customer could not be saved"));
        }

    }

    @GetMapping("/v1.0")
    @Operation(summary = "Get all customers")
    public List<Customer> fetchCustomers() {
        return customerService.getCustomers();
    }

    @GetMapping("/v1.0/{accountNo}")
    @Operation(summary = "Get customer by account number")
    public ResponseEntity<ResponseWrapper> fetchCustomerById(@PathVariable long accountNo) {

        Customer customer = customerService.findCustomer(accountNo);

        if (customer != null) {
            return ResponseEntity.ok(new ResponseWrapper<>(customer, "Customer retrieved successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(null, "Customer with account number " + accountNo + " not found"));
        }
    }

    @PutMapping("/v1.0")
    @Operation(summary = "Update customer email")
    public ResponseEntity<ResponseWrapper> updateCustomer(
            @RequestParam long accountNo,
            @RequestParam String newEmail) {

        Customer customer = customerService.updateCustomer(accountNo, newEmail);

        if (customer != null) {
            return ResponseEntity.ok(new ResponseWrapper<>(customer, "Customer updated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(null, "Customer not updated"));
        }
    }

    @DeleteMapping("/v1.0")
    @Operation(summary = "Delete customer")
    public ResponseEntity<ResponseWrapper> deleteCustomer(@RequestParam long accountNo) {

        if (customerService.deleteCustomer(accountNo)) {
            return ResponseEntity.ok(new ResponseWrapper<>("Customer deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(null, "Customer with account number " + accountNo + " not found"));
        }
    }
}
