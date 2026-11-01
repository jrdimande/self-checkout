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

    public CheckoutService() {
        this.cart = new Cart();
        this.productDAO = new ProductDAO();
        this.customerDAO = new CustomerDAO();
        this.saleDAO = new SaleDAO();
        this.paymentService = new PaymentService();
        this.saleItemDAO = new SaleItemDAO();
    }

    public void startCheckout() {
        sale = new Sale();
        cart.clear();
    }

    public Product processRFID(String uid) {

        Product product = productDAO.findByUuid(uid);

        if (product == null) {
            System.out.println("Produto não encontrado.");
            return null;
        }

        return product;
    }

    public void addItem(String uuid) {
        Product product = productDAO.findByUuid(uuid);

        if (product == null) {
            return;
        }

        SaleItem saleItem = new SaleItem(0, product);
        cart.addItem(saleItem);
    }

    public void removeItem(String uuid) {
        cart.removeItem(uuid);
    }

    public double getTotal() {
        cart.calculateTotal();
        return cart.getTotal();
    }

    /**
     * Conclui a venda usando o cartão (UID) do cliente.
     *
     * @return true se a venda foi concluída com sucesso
     */
    public boolean checkout(String customerUuid) {
        try {
            if (sale == null || cart.getItems().isEmpty()) {
                return false;
            }

            Customer customer = customerDAO.findByUuid(customerUuid);

            if (customer == null) {
                return false;   // cartão não reconhecido
            }

            cart.calculateTotal();

            sale.setCustomer_uuid(customer.getUuid());
            sale.setTotal(cart.getTotal());

            int saleId = saleDAO.save(sale);

            for (SaleItem saleItem : cart.getItems()) {
                saleItem.setSale_id(saleId);
                saleItemDAO.save(saleItem);   // grava os itens da venda
            }

            paymentService.savePayment(saleId, cart.getTotal(), "CARD");

            cart.clear();
            sale = null;

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}