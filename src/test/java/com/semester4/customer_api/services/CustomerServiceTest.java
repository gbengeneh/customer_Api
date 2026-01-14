package com.semester4.customer_api.services;

import com.semester4.customer_api.exceptions.CustomerNotFoundException;
import com.semester4.customer_api.models.*;
import com.semester4.customer_api.repositories.AddressRepository;
import com.semester4.customer_api.repositories.CorporateRepository;
import com.semester4.customer_api.repositories.CustomerRepository;
import com.semester4.customer_api.repositories.IndividualRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private IndividualRepository individualRepository;

    @Mock
    private CorporateRepository corporateRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private Individual individual;
    private Corporate corporate;
    private Address address;

    @BeforeEach
    void setUp() {
        FullName fullName = FullName.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .build();

        customer = Customer.builder()
                .accountNo(1234567890L)
                .fullName(fullName)
                .email("john.doe@example.com")
                .password("Password1")
                .phoneNumber(1234567890L)
                .build();

        individual = Individual.builder()
                .accountNo(1234567891L)
                .fullName(fullName)
                .email("jane.doe@example.com")
                .password("Password1")
                .phoneNumber(1234567891L)
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        corporate = Corporate.builder()
                .accountNo(1234567892L)
                .fullName(fullName)
                .email("corp@example.com")
                .password("Password1")
                .phoneNumber(1234567892L)
                .companyType(CompanyType.PRIVATE)
                .build();

        address = new Address(null, "123", "Main St", "City", "State", "12345", "Country", customer);
    }

    @Test
    void testAddCustomer_WithAccountNo() {
        when(customerRepository.save(customer)).thenReturn(customer);

        Customer result = customerService.addCustomer(customer);

        assertEquals(customer, result);
        verify(customerRepository).save(customer);
    }

    @Test
    void testAddCustomer_WithoutAccountNo() {
        customer.setAccountNo(null);
        Customer savedCustomer = Customer.builder()
                .accountNo(1000000000L) // Mock generated
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .password(customer.getPassword())
                .phoneNumber(customer.getPhoneNumber())
                .build();
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = customerService.addCustomer(customer);

        assertNotNull(result.getAccountNo());
        assertTrue(result.getAccountNo() >= 1000000000L && result.getAccountNo() <= 9999999999L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testAddIndividual_WithAccountNo() {
        when(individualRepository.save(individual)).thenReturn(individual);

        Individual result = customerService.addIndividual(individual);

        assertEquals(individual, result);
        verify(individualRepository).save(individual);
    }

    @Test
    void testAddIndividual_WithoutAccountNo() {
        individual.setAccountNo(null);
        Individual savedIndividual = Individual.builder()
                .accountNo(1000000001L)
                .fullName(individual.getFullName())
                .email(individual.getEmail())
                .password(individual.getPassword())
                .phoneNumber(individual.getPhoneNumber())
                .gender(individual.getGender())
                .dateOfBirth(individual.getDateOfBirth())
                .build();
        when(individualRepository.save(any(Individual.class))).thenReturn(savedIndividual);

        Individual result = customerService.addIndividual(individual);

        assertNotNull(result.getAccountNo());
        verify(individualRepository).save(any(Individual.class));
    }

    @Test
    void testAddCorporate_WithAccountNo() {
        when(corporateRepository.save(corporate)).thenReturn(corporate);

        Corporate result = customerService.addCorporate(corporate);

        assertEquals(corporate, result);
        verify(corporateRepository).save(corporate);
    }

    @Test
    void testAddCorporate_WithoutAccountNo() {
        corporate.setAccountNo(null);
        Corporate savedCorporate = Corporate.builder()
                .accountNo(1000000002L)
                .fullName(corporate.getFullName())
                .email(corporate.getEmail())
                .password(corporate.getPassword())
                .phoneNumber(corporate.getPhoneNumber())
                .companyType(corporate.getCompanyType())
                .build();
        when(corporateRepository.save(any(Corporate.class))).thenReturn(savedCorporate);

        Corporate result = customerService.addCorporate(corporate);

        assertNotNull(result.getAccountNo());
        verify(corporateRepository).save(any(Corporate.class));
    }

    @Test
    void testSaveAddress() {
        when(addressRepository.save(address)).thenReturn(address);

        Address result = customerService.saveAddress(address);

        assertEquals(address, result);
        verify(addressRepository).save(address);
    }

    @Test
    void testGetCustomers() {
        List<Customer> customers = List.of(customer, individual, corporate);
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.getCustomers();

        assertEquals(customers, result);
        verify(customerRepository).findAll();
    }

    @Test
    void testUpdateCustomer_Success() {
        when(customerRepository.findById(1234567890L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        Customer result = customerService.updateCustomer(1234567890L, "new.email@example.com");

        assertEquals(customer, result);
        assertEquals("new.email@example.com", customer.getEmail());
        verify(customerRepository).findById(1234567890L);
        verify(customerRepository).save(customer);
    }

    @Test
    void testUpdateCustomer_NotFound() {
        when(customerRepository.findById(9999999999L)).thenReturn(Optional.empty());

        Customer result = customerService.updateCustomer(9999999999L, "new.email@example.com");

        assertNull(result);
        verify(customerRepository).findById(9999999999L);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void testDeleteCustomer_Success() {
        when(customerRepository.findById(1234567890L)).thenReturn(Optional.of(customer));

        boolean result = customerService.deleteCustomer(1234567890L);

        assertTrue(result);
        verify(customerRepository).findById(1234567890L);
        verify(customerRepository).delete(customer);
    }

    @Test
    void testDeleteCustomer_NotFound() {
        when(customerRepository.findById(9999999999L)).thenReturn(Optional.empty());

        boolean result = customerService.deleteCustomer(9999999999L);

        assertFalse(result);
        verify(customerRepository).findById(9999999999L);
        verify(customerRepository, never()).delete(any());
    }

    @Test
    void testFindCustomer_Success() {
        when(customerRepository.findById(1234567890L)).thenReturn(Optional.of(customer));

        Customer result = customerService.findCustomer(1234567890L);

        assertEquals(customer, result);
        verify(customerRepository).findById(1234567890L);
    }

    @Test
    void testFindCustomer_NotFound() {
        when(customerRepository.findById(9999999999L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.findCustomer(9999999999L));
        verify(customerRepository).findById(9999999999L);
    }
}
