package com.machine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.machine.common.BuildInput;
import com.machine.exception.StoreException;
import com.machine.repo.Store;
import com.machine.service.MachineProcessor;
import com.machine.service.impl.MachineProcessorImpl;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class CoffeeMachineTest {

    private CoffeeMachine coffeeMachine;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final String cantPrepareString = " cannot be prepared because ";

    @Before
    public void setUp() throws StoreException, IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        reading input to build machine
        String json = IOUtils.toString(classLoader.getResourceAsStream("build-input.json"), StandardCharsets.UTF_8.name());
        BuildInput buildInput = objectMapper.readValue(json, BuildInput.class);

        Store store = new Store(buildInput.getMachine().getTotalItemsQuantity());
        Map<String, Map<String, Integer>> beverages = buildInput.getMachine().getBeverages();
//        injecting beans of store and beverages to processor
        MachineProcessor machineProcessor = new MachineProcessorImpl(store, beverages);
//        finally creating coffemachine with injection of machineProcessor
        coffeeMachine = new CoffeeMachine(machineProcessor);
    }

    @Test
    public void test1() {
//        given test cases
        String first = "hot_tea", second = "hot_coffee", third = "black_tea", forth = "green_tea";
        String a = coffeeMachine.get(first);
        String b = coffeeMachine.get(second);
        String c = coffeeMachine.get(third);
        String d = coffeeMachine.get(forth);
        AssertPrepared(first, a);
        AssertPrepared(second, b);
        assertNotSufficient(third, c, "hot_water");
        assertNotSufficient(forth, d, "sugar_syrup");

    }

    @Test
    public void test2() {
        String a = coffeeMachine.get("hot_tea");
        String b = coffeeMachine.get("black_tea");
        String c = coffeeMachine.get("hot_coffee");
        String d = coffeeMachine.get("green_tea");
        AssertPrepared("hot_tea", a);
        AssertPrepared("black_tea", b);
        assertNotAvailable("hot_coffee", c, "hot_water");
        assertNotAvailable("green_tea", d, "hot_water");
    }

    @Test
    public void test3() {
        String a = coffeeMachine.get("hot_coffee");
        String b = coffeeMachine.get("black_tea");
        String c = coffeeMachine.get("hot_tea");
        String d = coffeeMachine.get("green_tea");
        AssertPrepared("hot_coffee", a);
        AssertPrepared("black_tea", b);
        assertNotSufficient("hot_tea", c, "hot_water");
        assertNotAvailable("green_tea", d, "sugar_syrup");
    }

    @Test
    public void test1AfterRefillSuger() {
        //refill suger_syrup and green mixture but still lack of green_mixture
        String first = "hot_tea", second = "hot_coffee", third = "black_tea", forth = "green_tea";
        String a = coffeeMachine.get(first);
        String b = coffeeMachine.get(second);
        coffeeMachine.fill("sugar_syrup", 20);
        String c = coffeeMachine.get(third);
        String d = coffeeMachine.get(forth);

        AssertPrepared(first, a);
        AssertPrepared(second, b);
        assertNotSufficient(third, c, "hot_water");
        assertNotAvailable(forth, d, "green_mixture");
    }

    @Test
    public void test1AfterRefillSuccess() {
        //refill suger_syrup and green mixture so that green_tea can be prepared
        String first = "hot_tea", second = "hot_coffee", third = "black_tea", forth = "green_tea";
        String a = coffeeMachine.get(first);
        String b = coffeeMachine.get(second);
        coffeeMachine.fill("sugar_syrup", 20);
        coffeeMachine.fill("green_mixture", 50);
        String c = coffeeMachine.get(third);
        String d = coffeeMachine.get(forth);
        AssertPrepared(first, a);
        AssertPrepared(second, b);
        assertNotSufficient(third, c, "hot_water");
        AssertPrepared(forth, d);
    }

    @Test
    public void parallelGet() {

        //try to achieve different output while parallel input
        CompletableFuture.runAsync(() -> coffeeMachine.fill("hot_water", 100));
        CompletableFuture<String> future3
                = CompletableFuture.supplyAsync(() -> coffeeMachine.get("black_tea"));
        CompletableFuture<String> future1
                = CompletableFuture.supplyAsync(() -> coffeeMachine.get("hot_tea"));
        CompletableFuture<String> future2
                = CompletableFuture.supplyAsync(() -> coffeeMachine.get("hot_coffee"));

        CompletableFuture<String> future4
                = CompletableFuture.supplyAsync(() -> coffeeMachine.get("green_tea"));

        Stream.of(future3, future2, future1, future4)
                .map(CompletableFuture::join)
                .forEach(System.out::println);

    }


    private void assertNotSufficient(String input, String output, String notSufficient) {

        String notSufficientString = " is not sufficient";
        Assert.assertEquals(input + cantPrepareString + notSufficient + notSufficientString, output);
    }

    private void assertNotAvailable(String input, String output, String notAvailable) {

        String notAvailableString = " is not available";
        Assert.assertEquals(input + cantPrepareString + notAvailable + notAvailableString, output);
    }

    private void AssertPrepared(String first, String a) {
        String preparedString = " is prepared";
        Assert.assertEquals(first + preparedString, a);
    }
}