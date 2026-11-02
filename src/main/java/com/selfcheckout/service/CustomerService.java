package com.selfcheckout.service;

import com.selfcheckout.dao.CustomerDAO;
import com.selfcheckout.model.Customer;

public class CustomerService {
    CustomerDAO customerDAO;

    public CustomerService(){
        customerDAO = new CustomerDAO();
    }

    public void updateBalance(Customer customer){
        customerDAO.update(customer);
    }
}

