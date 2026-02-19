package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class FunctionalInterfacesTest {

    @Test
    public void a_predicate1() {
        Predicate<String> pred = x -> x.length() > 4; // TODO

        Assertions.assertTrue(pred.test("abcde"));
        Assertions.assertFalse(pred.test("abcd"));
        Assertions.assertFalse(pred.test(""));
    }


}