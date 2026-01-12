package com.semester4.customer_api.services;

import com.semester4.customer_api.exceptions.CustomerNotFoundException;
import com.semester4.customer_api.models.Address;
import com.semester4.customer_api.models.Corporate;
import com.semester4.customer_api.models.Customer;
import com.semester4.customer_api.models.Individual;
import com.semester4.customer_api.repositories.AddressRepository;
import com.semester4.customer_api.repositories.CorporateRepository;
import com.semester4.customer_api.repositories.CustomerRepository;
import com.semester4.customer_api.repositories.IndividualRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private IndividualRepository individualRepository;
    @Autowired
    private CorporateRepository corporateRepository;
    @Override
    public Customer addCustomer(Customer customer) {

        // prevent overriding account number if provided mistakenly
        if (customer.getAccountNo() == null) {
            customer.setAccountNo(generateAccountNo());
        }

        return customerRepository.save(customer);
    }


    @Override
    public List<Customer> getCustomers(){
        return customerRepository.findAll();
    }

    @Override
    public Customer updateCustomer(long accountNo, String newEmail){
        Customer customer = findCustomer(accountNo);
        if (customer!= null){
            customer.setEmail(newEmail);
            return customerRepository.save(customer);
        }else {
            return null;
        }
    }
    @Override
    public boolean deleteCustomer(long accountNo){
        boolean status = false;
        Customer customer = findCustomer(accountNo);
        if (customer!= null){
            customerRepository.delete(customer);
            status = true;
        }
        return status;
    }

    @Override
    public Customer findCustomer(long accountNo){
        return customerRepository.findById(accountNo).orElseThrow(()->
                new CustomerNotFoundException("Customer with account number"+ accountNo+ "not found"));
    }

    @Override
    public Individual addIndividual(Individual individual) {
        if (individual.getAccountNo() == null) {
            individual.setAccountNo(generateAccountNo());
        }
        return individualRepository.save(individual);
    }

    @Override
    public Corporate addCorporate(Corporate corporate) {
        if (corporate.getAccountNo() == null) {
            corporate.setAccountNo(generateAccountNo());
        }
        return corporateRepository.save(corporate);
    }

    @Override
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    private long generateAccountNo() {
        long min = 1_000_000_000L;  // 10-digit minimum
        long max = 9_999_999_999L;  // 10-digit maximum
        SecureRandom random = new SecureRandom();
        return min + (long) (random.nextDouble() * (max - min));
    }




}
