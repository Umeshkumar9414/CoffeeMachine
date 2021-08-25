package com.machine.service;

import java.util.Map;

public interface MachineProcessor {

    String make(String beverage);

    Map<String, Boolean> getIndicator();

    void fill(String ingredient, Integer amount);

}
