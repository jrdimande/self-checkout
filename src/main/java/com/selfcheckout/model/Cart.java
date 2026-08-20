package com.selfcheckout.model;

import java.util.ArrayList;

public class Cart {
    private ArrayList<SaleItem> items;
    private double total;

    public Cart(){
        this.items = new ArrayList<>();
        this.total = 0;
    }

    public ArrayList<SaleItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<SaleItem> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void addItem(SaleItem saleItem){
        for (int i = 0; i < items.size(); i++){
            SaleItem item = items.get(i);

            if (item.getProduct().getUuid().equals(saleItem.getProduct().getUuid())){
                item.setQuantity(item.getQuantity() + 1);
                return;

            }
        }
        items.add(saleItem);
    }

    public double calculateTotal(){
        this.total = 0;
        for (int i = 0; i < items.size(); i++){
            SaleItem saleItem = items.get(i);
            this.total += saleItem.getSubtotal();
        }

        return total;
    }

    public void clear(){
        items.clear();
    }



}
