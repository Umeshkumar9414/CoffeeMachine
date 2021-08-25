package com.machine.repo;

import com.machine.exception.NotAvailableException;
import com.machine.exception.NotSufficientException;
import com.machine.exception.StoreException;

import java.util.Map;
import java.util.stream.Collectors;


public class Store {

    private Map<String, StoreSector> ingredients;

    private Map<String, Boolean> indicator;

    public Map<String, Boolean> getIndicator() {
        return indicator;
    }

    public Store(Map<String, Integer> quantity) {
        this.ingredients = quantity.entrySet().stream().collect
                (Collectors.toMap(Map.Entry::getKey, en -> new StoreSector(en.getValue())));
    }

    public void updateMinRequired(String ingredient, Integer quantity) {
        /* *
         * I have taken max amount of any ingredient as min amount required
         * */
        if (!ingredients.containsKey(ingredient)) {
            ingredients.put(ingredient, new StoreSector(0));
        }
        if (ingredients.get(ingredient).getMinRequired() < quantity) {
            ingredients.get(ingredient).setMinRequired(quantity);
        }
    }

    public void initializeIndicator() {
        /* *
         * initialize indicator ar first
         * */
        indicator = ingredients.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, en -> en.getValue().isRunningLow()));
    }

    public boolean fill(String ingredient, Integer quantity) {
        boolean success = ingredients.get(ingredient).fill(quantity);
        updateIndicator(ingredient);
        return success;
    }

    private void checkPossibility(Map<String, Integer> recipe) throws StoreException {
        /* *
         * checking if given recipe is possible in stored amount if not won't create the mixture
         * */

        for (Map.Entry<String, Integer> ingredient : recipe.entrySet()) {
            if (ingredients.get(ingredient.getKey()).getQuantity() == 0) {
                throw new NotAvailableException(ingredient.getKey());
            }
            boolean available = ingredient.getValue() <= ingredients.get(ingredient.getKey()).getQuantity();
            if (!available) {
                throw new NotSufficientException(ingredient.getKey());
            }
        }

    }

    public synchronized String getMixture(Map<String, Integer> recipe) throws StoreException {

        /* *
         * after check possibility assigned the items
         * synchronised so that in parallel if one possible then it didn't give wrong answer
         * method is thread safe so no need to use stringBuffer
         * */

        checkPossibility(recipe);
        StringBuilder mix = new StringBuilder();
        for (Map.Entry<String, Integer> ingredient : recipe.entrySet()) {
            ingredients.get(ingredient.getKey()).deduct(ingredient.getValue());
            //after deducting the items update the indicator
            updateIndicator(ingredient.getKey());
            mix.append(ingredient.getKey());
        }
        return mix.toString();
    }

    private void updateIndicator(String item) {
        indicator.put(item, ingredients.get(item).isRunningLow());
    }
}
