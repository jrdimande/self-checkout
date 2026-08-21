package com.selfcheckout.service;

import com.selfcheckout.dao.PaymentDAO;
import com.selfcheckout.model.Payment;

public class PaymentService {
    private PaymentDAO paymentDAO;

    public PaymentService(){
        this.paymentDAO = new PaymentDAO();
    }

    public void savePayment(int saleID, double amount, String paymentMethod){
        Payment payment = new Payment(saleID, amount, paymentMethod);
        paymentDAO.save(payment);
    }
}
