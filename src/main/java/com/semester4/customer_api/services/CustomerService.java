package com.semester4.customer_api.services;

import com.semester4.customer_api.models.Address;
import com.semester4.customer_api.models.Corporate;
import com.semester4.customer_api.models.Customer;
import com.semester4.customer_api.models.Individual;

import java.util.List;

public interface CustomerService {
    Customer addCustomer(Customer customer);
    Individual addIndividual(Individual individual);
    Corporate addCorporate(Corporate corporate);
    Address saveAddress(Address address);
    List<Customer> getCustomers();
    Customer updateCustomer(long AccountNo, String newEmail);
    boolean deleteCustomer(long AccountNo);
    Customer findCustomer(long accountNo);
}
