package com.machine;

import com.machine.service.MachineProcessor;

import java.util.Map;

public class CoffeeMachine {

    private MachineProcessor machineProcessor;

    CoffeeMachine(MachineProcessor machineProcessor) {
        this.machineProcessor = machineProcessor;
    }

    private Map<String, Boolean> ingredientStatus() {
        return machineProcessor.getIndicator();
    }

    public String get(String beverage) {
        try {
            synchronized (beverage.intern()) {
                return machineProcessor.make(beverage);
            }
        } finally {
            System.out.println(ingredientStatus());
        }
    }

    public void fill(String ingredient, Integer amount) {
        machineProcessor.fill(ingredient, amount);
        System.out.println(ingredientStatus());
    }


}
