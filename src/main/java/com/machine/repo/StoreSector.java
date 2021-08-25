package com.machine.repo;

import lombok.Getter;

@Getter
public class StoreSector {

    private static final Integer DEFAULT_CAPACITY = 800;

    private Integer minRequired;

    private Integer quantity;

    private Integer capacity;

    void setMinRequired(Integer minRequired) {
        this.minRequired = minRequired;
    }

    StoreSector(Integer quantity) {
        this.quantity = quantity;
        this.capacity = DEFAULT_CAPACITY;
        this.minRequired=quantity;
    }

    public StoreSector(Integer minRequired, Integer quantity, Integer capacity) {
        this.minRequired = minRequired;
        this.quantity = quantity;
        this.capacity = capacity;
    }

    synchronized boolean fill(Integer addedQuantity) {

        if (this.capacity >= this.quantity + addedQuantity) {
            this.quantity += addedQuantity;
            return true;
        } else {
            this.quantity=this.capacity;
            System.out.println("Overflow");
            return false;
        }
    }

    public boolean isRunningLow(){
        return quantity<minRequired;
    }

    void deduct(Integer quant){
        this.quantity-=quant;
    }
}
