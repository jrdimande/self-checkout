package org.example;

import com.selfcheckout.dao.CustomerDAO;
import com.selfcheckout.dao.ProductDAO;
import com.selfcheckout.dao.SaleDAO;
import com.selfcheckout.dao.SaleItemDAO;
import com.selfcheckout.model.*;
import com.selfcheckout.service.CheckoutService;

public class Main {

    public static void main(String[] args) {
        CheckoutService checkoutService = new CheckoutService();

        checkoutService.startCheckout();
        checkoutService.addItem("PROD003");
        checkoutService.addItem("PROD001");
        checkoutService.checkout("CUST001");
        checkoutService.addItem("PROD001");

        System.out.println(checkoutService.getTotal());


    }

}