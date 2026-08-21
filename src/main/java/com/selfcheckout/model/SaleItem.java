package com.selfcheckout.model;

public class SaleItem {
    private int id;
    private int sale_id;
    private Product product;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public SaleItem(int sale_id, Product product){
        this.sale_id = sale_id;
        this.product = product;
        this.quantity = 1;
        this.unitPrice = product.getPrice();
        this.subtotal = this.unitPrice * this.quantity;

    }

    public SaleItem(){
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = this.unitPrice * this.quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice * quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
