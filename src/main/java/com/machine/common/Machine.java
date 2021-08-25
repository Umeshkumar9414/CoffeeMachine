package com.machine.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Machine {
    private Outlets outlets;

    @JsonProperty("total_items_quantity")
    private Map<String, Integer> totalItemsQuantity;

    private Map<String, Map<String, Integer>> beverages;
}