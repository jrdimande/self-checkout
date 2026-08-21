package com.selfcheckout.service;

import com.selfcheckout.dao.*;
import com.selfcheckout.model.*;

public class CheckoutService {
    private final Cart cart;
    private final ProductDAO productDAO;
    private final CustomerDAO customerDAO;
    private final SaleDAO saleDAO;
    private final SaleItemDAO saleItemDAO;
    private final PaymentService paymentService;
    private Sale sale;

    public CheckoutService(){
        this.cart = new Cart();
        this.productDAO = new ProductDAO();
        this.customerDAO = new CustomerDAO();
        this.saleDAO = new SaleDAO();
        this.paymentService = new PaymentService();
        this.saleItemDAO =  new SaleItemDAO();
    }

    public void startCheckout(){
        sale = new Sale();
        cart.clear();
    }

    public void addItem(String uuid){
        Product product = productDAO.findByUuid(uuid);
        SaleItem saleItem = new SaleItem(0, product);
        cart.addItem(saleItem);
    }

    public void removeItem(String uuid){
        cart.removeItem(uuid);
    }

    public void checkout(String uuid){
        cart.calculateTotal();


        Customer customer = customerDAO.findByUuid(uuid);

        sale.setCustomer_uuid(customer.getUuid());
        sale.setTotal(cart.getTotal());

        int saleId = saleDAO.save(sale);

        for (SaleItem saleItem : cart.getItems()){
            saleItem.setSale_id(saleId);
            saleItemDAO.save(saleItem);
        }


        paymentService.savePayment(saleId, cart.getTotal(), "CARD");

        cart.clear();
        sale = null;

    }


}
