package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class FunctionalInterfacesTest {

    // 1. Predicate [10 Marks]
    // Write a lambda expression implementation in below test case using Predicate that tests
    // whether a string is longer than four characters.
    @Test
    public void a_predicate1() {
        Predicate<String> pred = x -> x.length() > 4; // TODO

        Assertions.assertTrue(pred.test("abcde"));
        Assertions.assertFalse(pred.test("abcd"));
        Assertions.assertFalse(pred.test(""));
    }

    //2. Function [10 Marks]
    // Write a lambda expression that wraps the given string in parentheses.
    @Test
    public void function1() {
        Function<String, String> func = x -> String.format("(%s)", x);
        // TODO

        assertEquals("(abc)", func.apply("abc"));
        assertEquals("()", func.apply(""));
    }

    // 3. Consumer [10 Marks]
    // Write a lambda expression that appends the string "abc" to the given StringBuilder.
    @Test
    void c_consumer1() {
        StringBuilder sb = new StringBuilder("xyz");
        Consumer<StringBuilder> appenderConsumer = x -> x.append("abc"); // TODO

        appenderConsumer.accept(sb);

        assertEquals("xyzabc", sb.toString());
    }

    // 4. Supplier [10 Marks]
    // Write a lambda expression that returns a new StringBuilder containing the string "abc".
    @Test
    public void d_supplier1() {
        Supplier<StringBuilder> sup = () -> new StringBuilder("abc"); // TODO

        assertEquals("abc", sup.get().toString());
    }



}