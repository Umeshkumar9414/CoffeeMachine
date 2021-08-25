package com.machine.service.impl;

import com.machine.exception.NotAvailableException;
import com.machine.exception.NotSufficientException;
import com.machine.exception.StoreException;
import com.machine.repo.Store;
import com.machine.service.MachineProcessor;

import java.util.Map;


public class MachineProcessorImpl implements MachineProcessor {

    private Store store;
    private Map<String, Map<String, Integer>> beverages;

    public MachineProcessorImpl(Store store, Map<String, Map<String, Integer>> beverages) {
        //initialization
        this.store = store;
        this.beverages = beverages;
        beverages.forEach((beverage, value) -> value.forEach(this.store::updateMinRequired));
        this.store.initializeIndicator();
    }

    @Override
    public String make(String beverage) {
        try {
            String mix = store.getMixture(beverages.get(beverage));
//            prepare(mix)
            return beverage + " is prepared";
        } catch (NotAvailableException ex) {
            return beverage + " cannot be prepared because " + ex.getItem() + " is not available";
        } catch (NotSufficientException exc) {
            return beverage + " cannot be prepared because " + exc.getItem() + " is not sufficient";
        } catch (StoreException e) {
            return "Error Occurred";
        }
    }

    @Override
    public Map<String, Boolean> getIndicator() {
        return store.getIndicator();
    }

    @Override
    public void fill(String ingredient, Integer amount) {
        store.fill(ingredient,amount);
    }


}
